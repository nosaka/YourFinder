package me.ns.yourfinder.activity

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import me.ns.yourfinder.R
import me.ns.yourfinder.adapter.FinderAdapter
import me.ns.yourfinder.data.AppDatabase
import me.ns.yourfinder.data.Finder
import me.ns.yourfinder.databinding.ActivityMainBinding
import me.ns.yourfinder.dialog.EditFinderDialog

class MainActivity : LifecycleActivity() {

    /**
     * ViewModel
     */
    class VC(activity: Activity) : ViewModel() {

        private val finderDao = AppDatabase.getInMemoryDatabase(activity).finderDao()

        val finders: LiveData<List<Finder>> = finderDao.allLiveData()
        private val activity: Activity = activity


        fun onClickFab(view: View) {
            EditFinderDialog.show(activity.fragmentManager, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setActionBar(binding.mainToolbar)

        binding.vc = VC(this@MainActivity)

        val adapter = FinderAdapter(this@MainActivity, binding.vc.finders)
        adapter.onItemClick = { item ->
            item.id?.let {
                EditFinderDialog.show(fragmentManager, it)
            }
        }
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.mainRecyclerView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }
}
