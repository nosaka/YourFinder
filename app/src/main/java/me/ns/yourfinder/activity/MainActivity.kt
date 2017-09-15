package me.ns.yourfinder.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import me.ns.yourfinder.R
import me.ns.yourfinder.adapter.FinderAdapter
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.FinderDao
import me.ns.yourfinder.databinding.ActivityMainBinding
import me.ns.yourfinder.listener.ItemTouchCallbackMethod
import me.ns.yourfinder.listener.ItemTouchHelperCallback
import java.util.*


class MainActivity : LifecycleActivity(), ItemTouchCallbackMethod {

    /**
     * Handlers
     */
    @Suppress("UNUSED_PARAMETER")
    inner class Handlers {

        fun onClickFloatingActionButton(view: View) {
            startActivity(EditFinderActivity.intent(this@MainActivity, null))
        }
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var finderDao: FinderDao

    private lateinit var finderAdapter: FinderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        finderDao = AppDatabase.getInMemoryDatabase(this@MainActivity).finderDao()

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.handlers = Handlers()

        setActionBar(binding.mainToolbar)

        val finderItems = finderDao.allLiveData()
        finderItems.observe(this@MainActivity, Observer { value ->
            finderAdapter.items = value ?: ArrayList()
            finderAdapter.notifyDataSetChanged()
        })
        finderAdapter = FinderAdapter(finderItems.value ?: ArrayList())
        val helper = ItemTouchHelper(ItemTouchHelperCallback(this@MainActivity))
        finderAdapter.onItemClick = { item ->
            item.id?.let {
                startActivity(EditFinderActivity.intent(this@MainActivity, it))
            }
        }
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        helper.attachToRecyclerView(binding.mainRecyclerView)
        binding.mainRecyclerView.adapter = finderAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onMove(fromPos: Int, toPos: Int) {
    }

    override fun onMoved(fromPos: Int, toPos: Int) {
        finderAdapter.notifyItemMoved(fromPos, toPos)
        Collections.swap(finderAdapter.items, fromPos, toPos)
    }

    override fun onDelete(position: Int) {
        val item = finderAdapter.getItem(position)
        val message = getString(R.string.message_delete_finder, item.name ?: getString(R.string.finder))
        Snackbar.make(binding.mainRecyclerView, message, Snackbar.LENGTH_SHORT)
                .setAction(R.string.action_undo, {
                    finderAdapter.notifyItemChanged(position)
                })
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event != DISMISS_EVENT_ACTION) {
                            finderDao.delete(item)
                        }
                    }
                })
                .show()

    }

    override fun onMoveFinished() {
        finderAdapter.items.withIndex().forEach {
            it.value.position = it.index
        }
        finderDao.update(finderAdapter.items)
    }
}
