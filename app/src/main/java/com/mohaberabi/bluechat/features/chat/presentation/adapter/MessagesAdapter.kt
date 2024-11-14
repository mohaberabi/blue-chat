package com.mohaberabi.bluechat.features.chat.presentation.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mohaberabi.bluechat.R
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.databinding.MessageListItemBinding

class MessagesAdapter :
    ListAdapter<MessageModel, MessageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val binding = MessageListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ) {
        val message = getItem(position)
        holder.bind(message)
    }

    private class DiffCallback : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem.senderName == newItem.senderName && oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }
    }
}

class MessageViewHolder(private val binding: MessageListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: MessageModel) {
        binding.apply {
            messageText.text = message.text
            if (message.sentByMe) {
                root.setBackgroundResource(R.drawable.own_message_bg)
            } else {
                root.setBackgroundResource(R.drawable.other_message_bg)
            }
        }
    }
}