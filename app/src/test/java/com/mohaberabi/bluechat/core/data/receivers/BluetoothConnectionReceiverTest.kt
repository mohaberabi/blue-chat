package com.mohaberabi.bluechat.core.data.receivers

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BluetoothConnectionReceiverTest {
    private lateinit var context: Context
    private var onConnectionLostCalled = false
    private lateinit var receiver: BluetoothConnectionReceiver

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        receiver = BluetoothConnectionReceiver {
            onConnectionLostCalled = true
        }
    }

    @Test
    fun `should call onConnectionLost when disconnected`() {
        val intent = Intent(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        receiver.onReceive(context, intent)
        assertTrue(onConnectionLostCalled)
    }

    @Test
    fun `receiver emits  nothing when intent matches none of registered  1 `() {
        val intent = Intent(BluetoothDevice.ACTION_ACL_CONNECTED)
        receiver.onReceive(context, intent)
        assertFalse(onConnectionLostCalled)
    }

    @Test
    fun `receiver emits  nothing when intent matches none of registered  2 `() {
        val intent = Intent(WifiManager.WIFI_STATE_CHANGED_ACTION).apply {
            putExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_ENABLED)
        }
        receiver.onReceive(context, intent)
        assertFalse(onConnectionLostCalled)
    }
}