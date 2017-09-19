package me.ns.yourfinder.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.ns.yourfinder.R
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.databinding.ListItemFinderBinding
import java.util.*


/**
 * FinderAdapter
 *
 * Created by shintaro.nosaka on 2017/09/11.
 */
class FinderAdapter : RecyclerView.Adapter<FinderAdapter.VH>() {

    private var mItems = ArrayList<Finder>()

    var onItemClick: ((item: Finder) -> Unit)? = null

    override fun getItemCount(): Int = mItems.count()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.item = getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_finder, parent, false)
        return VH(view)
    }

    fun getItem(position: Int): Finder? = if (position in 0..(itemCount - 1)) mItems[position] else null

    fun setItems(items: List<Finder>) {
        mItems = ArrayList(items)
    }

    fun removeItem(position: Int) {
        if (position in 0..(itemCount - 1)) mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun insertItem(item: Finder, position: Int) {
        mItems.add(position, item)
        notifyItemInserted(position)
    }

    fun moveItem(fromPos: Int, toPos: Int) {
        Collections.swap(mItems, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }

    fun updateOrderItems(): List<Finder> {
        mItems.withIndex().forEach {
            it.value.position = it.index
        }
        return mItems
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var binding: ListItemFinderBinding = DataBindingUtil.bind(itemView)

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(binding.item)
            }
        }

    }

}