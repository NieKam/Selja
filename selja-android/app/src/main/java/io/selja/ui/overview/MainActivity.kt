package io.selja.ui.overview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import io.selja.R
import io.selja.base.BaseActivity
import io.selja.databinding.ActivityMainBinding
import io.selja.model.AdItem
import io.selja.model.PARCEL_PARAM
import io.selja.permissions.LOCATION_PERMISSION
import io.selja.ui.details.AdItemDetailsActivity
import io.selja.ui.newITem.NewItemActivity
import io.selja.ui.overview.adapter.ItemsAdapter
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

private const val NEW_ITEM_REQUEST = 10

class MainActivity : BaseActivity<ItemsOverviewViewModel>() {
    private val itemsObserver = Observer<List<AdItem>> { items ->
        itemsAdapter.add(items)
        binding.swipeLayout.isRefreshing = false
    }
    private val itemsAdapter: ItemsAdapter by inject()
    override val viewModel: ItemsOverviewViewModel by inject()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).also {
            it.viewModel = viewModel
            RxView.clicks(it.tvPermissionText).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe { permissionManager.requestLocationPermissions(this) }
            RxView.clicks(it.fab).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe { showNewItemForm() }
            it.swipeLayout.setOnRefreshListener { viewModel.refresh() }

            it.rvAdItems.apply {
                adapter = itemsAdapter
                setHasFixedSize(false)
            }
        }

        viewModel.initPermissionState(permissionManager.hasPermission(this, LOCATION_PERMISSION))

        itemsAdapter.apply {
            itemClickListener = { item, sharedViews -> showItem(item, sharedViews) }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.adItems.observe(this, itemsObserver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.adItems.removeObserver(itemsObserver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onResults(requestCode, grantResults) { granted ->
            viewModel.onRequestPermissionsResult(granted)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != NEW_ITEM_REQUEST || resultCode != Activity.RESULT_OK) {
            return
        }

        val newItem = requireNotNull(data).getParcelableExtra<AdItem>(PARCEL_PARAM)
        viewModel.addNewItem(requireNotNull(newItem))
    }

    private fun showItem(adItem: AdItem, sharedViews: List<View>) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair.create(sharedViews[0], "mainImageTransition")
        )

        val intent = Intent(this, AdItemDetailsActivity::class.java).apply {
            putExtra(PARCEL_PARAM, adItem)
        }
        startActivity(intent, options.toBundle())
    }

    private fun showNewItemForm() {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.fab, "fabTransition")
        val intent = Intent(this, NewItemActivity::class.java)
        startActivityForResult(intent, NEW_ITEM_REQUEST, options.toBundle())
    }
}
