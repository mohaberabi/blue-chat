package com.mohaberabi.bluechat.features.chat.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.mohaberabi.bluechat.core.data.services.ChatService
import com.mohaberabi.bluechat.core.domain.model.AppBluetoothDevice
import com.mohaberabi.bluechat.core.domain.model.AppResult
import com.mohaberabi.bluechat.core.domain.model.MessageModel
import com.mohaberabi.bluechat.core.domain.model.UiText
import com.mohaberabi.bluechat.core.domain.model.asUiText
import com.mohaberabi.bluechat.core.domain.model.errros.BluetoothError
import com.mohaberabi.bluechat.core.domain.usecase.connection.CheckConnectionLossUseCase
import com.mohaberabi.bluechat.core.domain.usecase.connection.SocketConnectionUseCases
import com.mohaberabi.bluechat.features.chat.domain.usecase.ListenToChatMessagesUseCase
import com.mohaberabi.bluechat.features.chat.domain.usecase.SendChatForegroundIntentUseCase
import com.mohaberabi.bluechat.features.chat.domain.usecase.SendMessageUseCase
import com.mohaberabi.bluechat.features.paired_devices.domain.usecase.GetPairedDevicesUseCase
import com.mohaberabi.bluechat.generator.bluetoothDevice
import com.mohaberabi.bluechat.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: ChatViewModel
    private val connectionUseCases: SocketConnectionUseCases = mockk()
    private val checkConnectionLoss: CheckConnectionLossUseCase = mockk()
    private val sendChatForegroundIntent: SendChatForegroundIntentUseCase = mockk()
    private val sendMessageUseCase: SendMessageUseCase = mockk()
    private val listenToChatMessages: ListenToChatMessagesUseCase = mockk()
    private val savedStateHandle: SavedStateHandle =
        SavedStateHandle(mapOf("senderName" to "loser"))

    @Before
    fun setup() {
        every { listenToChatMessages() } returns flowOf()
        every { checkConnectionLoss() } returns flowOf()

        viewModel = ChatViewModel(
            savedStateHandle = savedStateHandle,
            connectionUseCases = connectionUseCases,
            checkConnectionLoss = checkConnectionLoss,
            sendChatForegroundIntent = sendChatForegroundIntent,
            sendMessage = sendMessageUseCase,
            listenToChatMessages = listenToChatMessages
        )
    }

    @Test
    fun `collectChatMessages should collect incoming messages`() = runTest {
        val messages = listOf(
            MessageModel(true, "loser1", "hiloser!"),
            MessageModel(false, "loser2", "yesiamloser")
        )
        every { listenToChatMessages() } returns flowOf(*messages.toTypedArray())
        every { sendChatForegroundIntent(ChatService.ACTION_START) } just Runs

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.messages.isEmpty())
            viewModel.onAction(ChatActions.SendMessage)
            val stateAfterFirstMessage = awaitItem()
            assertEquals(messages[0], stateAfterFirstMessage.messages.first())
            val stateAfterSecondMessage = awaitItem()
            assertEquals(messages[1], stateAfterSecondMessage.messages.last())

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { listenToChatMessages() }
        coVerify { sendChatForegroundIntent(ChatService.ACTION_START) }
    }

    @Test
    fun `sendMessage should emit SendMessageError on failure`() = runTest {
        val error = BluetoothError.Unknown
        coEvery { sendMessageUseCase(any()) } coAnswers { AppResult.Error(error) }

        viewModel.events.test {
            viewModel.onAction(ChatActions.SendMessage)
            val event = awaitItem()
            assertTrue(event is ChatEvents.SendMessageError)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { sendMessageUseCase(any()) }
    }

    @Test
    fun `collectConnectivityLost should emit ConnectionLost event on disconnection`() = runTest {
        every { checkConnectionLoss() } returns flowOf(false, true)
        assertTrue(viewModel.events.first() is ChatEvents.ConnectionLost)

        coVerify { checkConnectionLoss() }
    }


    @Test
    fun `textMessageChanged should update state with new message text`() = runTest {
        val newText = "hey loser"
        viewModel.onAction(ChatActions.MessageTextChanged(newText))
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(newText, state.messageText)
            cancelAndIgnoreRemainingEvents()
        }
    }
}