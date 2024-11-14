package com.mohaberabi.bluechat.features.chat.presentation.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohaberabi.bluechat.R
import com.mohaberabi.bluechat.core.domain.model.UiText
import com.mohaberabi.bluechat.core.util.extensions.collectWithLifeCycle
import com.mohaberabi.bluechat.core.util.requiresNotificationsPermissions
import com.mohaberabi.bluechat.core.util.extensions.showSnackBar
import com.mohaberabi.bluechat.databinding.FragmentChatBinding
import com.mohaberabi.bluechat.features.chat.presentation.adapter.MessagesAdapter
import com.mohaberabi.bluechat.features.chat.presentation.viewmodel.ChatActions
import com.mohaberabi.bluechat.features.chat.presentation.viewmodel.ChatEvents
import com.mohaberabi.bluechat.features.chat.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val viewmodel: ChatViewModel by viewModels()
    private var _binding: FragmentChatBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var messagesAdapter: MessagesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(
            layoutInflater,
            container,
            false
        )
        messagesAdapter = MessagesAdapter()
        setupRecyclerView()
        setupBinding()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        collectState()
        collectEvents()
    }

    private fun setupBinding() {
        with(binding) {
            messageInput.addTextChangedListener(
                onTextChanged = { text, _, _, _ ->
                    viewmodel.onAction(ChatActions.MessageTextChanged(text.toString()))
                },
            )
            sendButton.setOnClickListener {
                viewmodel.onAction(ChatActions.SendMessage)
            }

        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            messagesRecyclerView.adapter = messagesAdapter
        }

    }


    private fun collectState() {
        collectWithLifeCycle(
            viewmodel.state,
        ) { state ->
            with(binding) {
                senderName.text = state.senderName
                avatarText.text = state.senderName.firstOrNull()?.uppercase() ?: ""
            }
            messagesAdapter.submitList(state.messages)
        }
    }

    private fun collectEvents() {
        collectWithLifeCycle(
            viewmodel.events,
        ) { event ->
            when (event) {
                ChatEvents.ConnectionLost -> handleConnectionLost()
                ChatEvents.MessageReceiveError -> showError(R.string.message_error)
                is ChatEvents.SendMessageError -> showError(event.error)
            }
        }
    }

    private fun handleConnectionLost() {
        showError(R.string.connection_lost).also {
            findNavController().popBackStack()
        }
    }

    private fun showError(
        @StringRes id: Int
    ) = requireView().showSnackBar(getString(id))

    private fun showError(
        text: UiText
    ) = requireView().showSnackBar(text)


}