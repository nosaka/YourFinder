package me.ns.yourfinder.listener

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * ItemTouchCallbackMethod
 *
 * Created by shintaro.nosaka on 2017/09/12.
 */
class ItemTouchHelperCallback(private val method: Method) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

    interface Method {
        fun onDelete(position: Int)
        fun onMoved(fromPos: Int, toPos: Int)
        fun onMoveFinished()
    }

    private var moving = false

    var removeIconBitmap: Bitmap? = null

    var removePaint = Paint().apply {
        color = Color.GRAY
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
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

    override fun onChildDraw(canvas: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && viewHolder?.itemView != null) {
            canvas?.apply {
                if (dX > 0) {
                    drawRect(viewHolder.itemView.left.toFloat(),
                            viewHolder.itemView.top.toFloat(),
                            dX,
                            viewHolder.itemView.bottom.toFloat(),
                            removePaint)

                    removeIconBitmap?.let {
                        val centerVertical = viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - it.height / 2
                        drawBitmap(it, 0f, centerVertical.toFloat(), removePaint)
                    }

                } else {
                    drawRect(viewHolder.itemView.right.toFloat() + dX,
                            viewHolder.itemView.top.toFloat(),
                            viewHolder.itemView.right.toFloat(),
                            viewHolder.itemView.bottom.toFloat(),
                            removePaint)
                }
            }
        }
    }
}