package me.ns.yourfinder.adapter

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
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
class FinderAdapter(lifecycleOwner: LifecycleOwner,
                    private var items: LiveData<List<Finder>>) : RecyclerView.Adapter<FinderAdapter.VH>() {

    var onItemClick: ((item: Finder) -> Unit)? = null

    init {
        items.observe(lifecycleOwner, Observer {
            notifyDataSetChanged()
        })

    }

    override fun getItemCount(): Int = items.value?.count() ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.item = getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_finder, parent, false)
        return VH(view)
    }

    private fun getItem(position: Int): Finder? = items.value?.get(position)

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var binding: ListItemFinderBinding = DataBindingUtil.bind(itemView)

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(binding.item)
            }
        }

    }


}