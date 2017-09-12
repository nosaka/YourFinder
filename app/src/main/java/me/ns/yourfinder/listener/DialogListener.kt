package me.ns.yourfinder.listener

import android.app.DialogFragment

/**
 * DialogListener
 *
 * Created by shintaro.nosaka on 2017/09/12.
 */
interface DialogListener {
    fun onDismiss(dialog: DialogFragment)
}