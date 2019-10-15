package io.selja.ui.overview.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import io.selja.R
import io.selja.base.DeviceId
import io.selja.databinding.ItemLayoutBinding
import io.selja.model.AdItem
import io.selja.ui.overview.ItemDetailsViewModel
import java.util.concurrent.TimeUnit


class ItemsAdapter(deviceId: DeviceId) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    private val adapterList: MutableList<AdItem> = mutableListOf()
    private val deviceIdString = deviceId.getDeviceId()

    var itemClickListener: ((AdItem, List<View>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_layout, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return adapterList.size
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = adapterList[position]
        val sharedViews = listOf<View>(holder.binding.ivItemPicture)
        holder.bind(ItemDetailsViewModel(item, deviceIdString))
        RxView.clicks(holder.itemView).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe {
            itemClickListener?.invoke(item, sharedViews)
        }
    }

    fun add(items: List<AdItem>) {
        adapterList.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemViewModel: ItemDetailsViewModel) {
            binding.viewModel = itemViewModel
        }
    }
}