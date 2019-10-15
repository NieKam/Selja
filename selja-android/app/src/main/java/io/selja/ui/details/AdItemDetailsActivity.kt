package io.selja.ui.details

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import io.selja.R
import io.selja.base.BaseActivity
import io.selja.databinding.ActivityAdItemDetailsBinding
import io.selja.model.AdItem
import io.selja.model.PARCEL_PARAM
import org.koin.android.ext.android.inject

class AdItemDetailsActivity : BaseActivity<AdItemDetailsViewModel>() {
    override val viewModel: AdItemDetailsViewModel by inject()

    private lateinit var binding: ActivityAdItemDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityAdItemDetailsBinding>(this, R.layout.activity_ad_item_details)
            .also {
                it.viewModel = viewModel
                it.swipeLayout.setOnRefreshListener { viewModel.refresh() }
            }

        intent?.let {
            val item = requireNotNull(it.getParcelableExtra<AdItem>(PARCEL_PARAM))
            supportActionBar?.title = item.name
            viewModel.oAdItem.set(item)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.swipeLayout.isRefreshing = false
    }
}
