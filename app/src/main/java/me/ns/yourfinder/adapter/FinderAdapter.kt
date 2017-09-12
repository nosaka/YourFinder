package me.ns.yourfinder.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.ns.yourfinder.R
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.databinding.ListItemFinderBinding


/**
 * FinderAdapter
 *
 * Created by shintaro.nosaka on 2017/09/11.
 */
class FinderAdapter(var items: List<Finder>) : RecyclerView.Adapter<FinderAdapter.VH>() {

    var onItemClick: ((item: Finder) -> Unit)? = null

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.item = getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_finder, parent, false)
        return VH(view)
    }

    fun getItem(position: Int): Finder = items.get(position)

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var binding: ListItemFinderBinding = DataBindingUtil.bind(itemView)

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(binding.item)
            }
        }

    }

}