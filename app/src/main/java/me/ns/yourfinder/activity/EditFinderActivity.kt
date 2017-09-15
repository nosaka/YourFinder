package me.ns.yourfinder.activity

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import me.ns.yourfinder.R
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.data.FinderDao
import me.ns.yourfinder.databinding.ActivityEditFinderBinding
import me.ns.yourfinder.util.AppBindingAdapter


/**
 * EditFinderActivity
 *
 * Created by shintaro.nosaka on 2017/09/13.
 */
class EditFinderActivity : LifecycleActivity() {

    companion object {

        private val KEY_BUNDLE_FINDER_ID = "finder_id"

        private val RQ_PERMISSION_RQ = 0x001

        private val RQ_PICK_IMAGE = 0x002

        fun intent(context: Context, finderId: Int?): Intent {
            return Intent(context, EditFinderActivity::class.java).apply {
                putExtra(KEY_BUNDLE_FINDER_ID, finderId)
            }
        }
    }

    /**
     * Handlers
     */
    inner class Handlers {

        fun onClickContentImageView(view: View) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RQ_PERMISSION_RQ)
                return
            }

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RQ_PICK_IMAGE)
        }

        fun onClickOkButton(view: View) {
            finderDao.insertOrUpdate(binding.finder)
            finish()
        }

        fun onClickCancelButton(view: View) {
            finish()
        }
    }

    private lateinit var binding: ActivityEditFinderBinding

    private lateinit var finderDao: FinderDao

    private var finderId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            finderId = it.getIntExtra(KEY_BUNDLE_FINDER_ID, -1)
        }

        finderDao = AppDatabase.getInMemoryDatabase(this@EditFinderActivity).finderDao()

        binding = DataBindingUtil.setContentView<ActivityEditFinderBinding>(this, R.layout.activity_edit_finder)
        binding.finder = finderDao.find(finderId) ?: Finder()
        binding.handlers = Handlers()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RQ_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    AppBindingAdapter.loadSmallImage(binding.editFinderContentImageView, data?.data)
                    binding.finder.iconUrl = getPath(data?.data)
                }
            }
            else -> {

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RQ_PERMISSION_RQ -> {
                var hasPermission = true
                for (grantResult in grantResults) {
                    hasPermission = grantResult == PackageManager.PERMISSION_GRANTED
                }
                if (hasPermission) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, RQ_PICK_IMAGE)
                }
            }
        }

    }

    private fun getPath(contentUri: Uri?): String? {
        if (contentUri == null) {
            return null
        }
        val cursor = contentResolver.query(contentUri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
        cursor.use {
            if (it != null) {
                if (it.count > 0) {
                    it.moveToFirst()
                    return it.getString(dataColumnIndex)
                }
            }
        }
        return null
    }
}