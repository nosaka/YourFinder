package me.ns.yourfinder.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import me.ns.yourfinder.R
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.data.FinderDao
import me.ns.yourfinder.databinding.DialogEditFinderBinding
import me.ns.yourfinder.listener.DialogListener


/**
 * EditFinderDialog
 *
 * Created by shintaro.nosaka on 2017/09/11.
 */
class EditFinderDialog : DialogFragment(), LifecycleOwner {

    companion object {
        private val KEY_BUNDLE_FINDER_ID = "finder_id"

        fun show(fragmentManager: FragmentManager, finderId: Int?, tag: String? = null) {
            val instance = EditFinderDialog()
            instance.arguments = Bundle()
            finderId?.let {
                instance.arguments?.apply {
                    putInt(KEY_BUNDLE_FINDER_ID, it)
                }
            }
            instance.show(fragmentManager, null)
        }
    }

    private lateinit var finderDao: FinderDao

    private lateinit var binding: DialogEditFinderBinding

    private var finderId: Int = -1

    private var listener: DialogListener? = null

    override fun getLifecycle(): Lifecycle = LifecycleRegistry(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = (activity as? DialogListener)

        finderDao = AppDatabase.getInMemoryDatabase(context).finderDao()
        arguments?.let {
            if (it.containsKey(KEY_BUNDLE_FINDER_ID)) {
                finderId = it.getInt(KEY_BUNDLE_FINDER_ID, -1)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_finder, null, false)
        binding = DataBindingUtil.bind<DialogEditFinderBinding>(view)
        binding.finder = AppDatabase.getInMemoryDatabase(context).finderDao().find(finderId) ?: Finder()

        return AlertDialog.Builder(activity)
                .setView(binding.root)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    finderDao.insertOrUpdate(binding.finder)
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener?.onDismiss(this@EditFinderDialog)
    }

}