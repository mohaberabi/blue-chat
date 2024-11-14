package com.mohaberabi.bluechat.core.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.databinding.DeviceListItemBinding


class DeviceListAdapter(
    private val onClick: (AppBluetoothDevice) -> Unit,
) :
    ListAdapter<AppBluetoothDevice, DeviceListAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = DeviceListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)
    }

    inner class DeviceViewHolder(
        private val binding: DeviceListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: AppBluetoothDevice) {
            binding.name.text = device.name
            binding.mac.text = device.macAddress
            binding.root.setOnClickListener {
                onClick(device)
            }
        }
    }

    class DeviceDiffCallback : DiffUtil.ItemCallback<AppBluetoothDevice>() {
        override fun areItemsTheSame(
            oldItem: AppBluetoothDevice,
            newItem: AppBluetoothDevice
        ): Boolean = oldItem.macAddress == newItem.macAddress

        override fun areContentsTheSame(
            oldItem: AppBluetoothDevice,
            newItem: AppBluetoothDevice
        ): Boolean = oldItem == newItem
    }
}