package me.ns.yourfinder.activity

import android.app.DialogFragment
import android.arch.lifecycle.LifecycleActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.data.FinderDao
import me.ns.yourfinder.dialog.EditFinderDialog
import me.ns.yourfinder.listener.DialogListener

class TransientActivity : LifecycleActivity(), DialogListener {

    companion object {
        private val KEY_BUNDLE_FINDER_ID = "finder_id"

        fun intent(context: Context, finderId: Int): Intent =
                Intent(context, TransientActivity::class.java).apply {
                    putExtra(KEY_BUNDLE_FINDER_ID, finderId)
                }
    }

    private lateinit var finderDao: FinderDao

    private var finder: Finder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finderDao = AppDatabase.getInMemoryDatabase(this@TransientActivity).finderDao()
        intent?.let {
            val finderId = it.getIntExtra(KEY_BUNDLE_FINDER_ID, -1)
            finderDao.find(finderId)?.let {
                finder = it
            }
        }
        finder?.let {
            EditFinderDialog.show(fragmentManager, it.id)
        }
    }

    override fun onDismiss(dialog: DialogFragment) {
        finish()
        overridePendingTransition(0, 0);
    }

}