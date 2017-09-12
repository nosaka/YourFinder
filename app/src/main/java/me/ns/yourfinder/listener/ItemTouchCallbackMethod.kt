package me.ns.yourfinder.listener

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * ItemTouchCallbackMethod
 *
 * Created by shintaro.nosaka on 2017/09/12.
 */
interface ItemTouchCallbackMethod {
    fun onDelete(position: Int)
    fun onMove(fromPos: Int, toPos: Int)
    fun onMoved(fromPos: Int, toPos: Int)
    fun onMoveFinished()
}

class ItemTouchHelperCallback(private val method: ItemTouchCallbackMethod) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

    private var moving = false

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPos = viewHolder.adapterPosition;
        val toPos = target.adapterPosition;
        method.onMove(fromPos, toPos)
        moving = true
        return true
    }

    override fun onMoved(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, fromPos: Int, target: RecyclerView.ViewHolder?, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        method.onMoved(fromPos, toPos)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        method.onDelete(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (moving && actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            method.onMoveFinished()
        }

    }
}