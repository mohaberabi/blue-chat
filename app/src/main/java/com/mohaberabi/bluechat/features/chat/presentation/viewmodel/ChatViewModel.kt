package com.mohaberabi.bluechat.features.chat.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.bluechat.core.data.services.ChatService
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.core.domain.model.asUiText
import com.mohaberabi.bluechat.core.domain.model.onDone
import com.mohaberabi.bluechat.core.domain.model.onFailed
import com.mohaberabi.bluechat.core.domain.usecase.connection.CheckConnectionLossUseCase
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.features.chat.domain.usecase.ListenToChatMessagesUseCase
import com.mohaberabi.bluechat.features.chat.domain.usecase.SendMessageUseCase
import com.mohaberabi.bluechat.features.chat.domain.usecase.SendChatForegroundIntentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing chat features , getting messages in real time and sending messages as well.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val connectionUseCases: SocketConnectionUseCases,
    private val checkConnectionLoss: CheckConnectionLossUseCase,
    private val sendChatForegroundIntent: SendChatForegroundIntentUseCase,
    private val sendMessage: SendMessageUseCase,
    private val listenToChatMessages: ListenToChatMessagesUseCase
) : ViewModel() {

    companion object {
        /**
         * Key for getting the passed nav arg saved in saved state handle when navigation to the chat screen
         * in which the viewmodel is scoped into.
         */
        const val SENDER_NAME_KEY = "senderName"
    }

    private val name = savedStateHandle.get<String>(
        SENDER_NAME_KEY
    ) ?: ""
    private val _state = MutableStateFlow(
        ChatState(senderName = name),
    )
    val state = _state.asStateFlow()

    private val _events = Channel<ChatEvents>()

    /**
     * Event channel for one-time events like errors for example.
     */
    val events = _events.receiveAsFlow()

    /**
     * collecting the chat messages whenever viewmodel  is created
     */
    init {
        collectChatMessages()
    }


    fun onAction(action: ChatActions) {
        when (action) {
            is ChatActions.MessageTextChanged -> textMessageChanged(action.text)
            ChatActions.SendMessage -> sendMessage()
        }
    }


    /**
     * Starts collecting chat messages. it has also side effect
     * when the flow starts it will fire [sendChatForegroundIntent] with action [ACTION_START]
     * the intent passed here is the domain one it has nothing to do with the android intents
     */
    private fun collectChatMessages() {
        listenToChatMessages()
            .onStart {
                collectConnectivityLost()
                sendChatForegroundIntent(ChatService.ACTION_START)
            }.onEach { newMessage ->
                addNewMessages(newMessage)
            }.catch {
                _events.send(ChatEvents.MessageReceiveError)
            }.launchIn(viewModelScope)
    }

    /**
     * this flow keeps track of current connection session as if the other side leaves the session or connection
     * for any reason we also should leave it and release resources
     * so it will send to the component [Fragment] event indicating that connection is lost
     * you should take action eg: pop the backstack
     */
    private fun collectConnectivityLost() {
        checkConnectionLoss()
            .onEach { lost ->
                if (lost) {
                    _events.send(ChatEvents.ConnectionLost)
                }
            }.launchIn(viewModelScope)
    }

    /**
     * Taking the typed user messages and saves it to the state
     * to be used to send message to the chatter
     */
    private fun textMessageChanged(
        text: String,
    ) = _state.update { it.copy(messageText = text) }

    /**
     * Sends a chat message and updates the state with our message to be added
     * to the list  of message for us to be informed or sends an error event indicating
     * that message send was not done and have some error
     */
    private fun sendMessage() {
        viewModelScope.launch {
            sendMessage(_state.value.messageText)
                .onDone { newMessage ->
                    addNewMessages(newMessage)
                }.onFailed {
                    _events.send(ChatEvents.SendMessageError(it.asUiText()))
                }
        }
    }


    /**
     * used to update the state messages by adding one message to current list
     */
    private fun addNewMessages(message: MessageModel) =
        _state.update { it.copy(messages = it.messages + message) }

    /**
     * viewmodel killing should free and release resources
     */
    override fun onCleared() {
        super.onCleared()
        connectionUseCases.closeConnection()
        sendChatForegroundIntent(ChatService.ACTION_STOP)
    }
}