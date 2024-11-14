package com.mohaberabi.bluechat.core.data.receivers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.mohaberabi.bluechat.core.domain.model.ScanDeviceResult
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BluetoothScanReceiverTest {
    private lateinit var context: Context
    private var result: ScanDeviceResult = ScanDeviceResult.Idle
    private lateinit var receiver: BluetoothScanReceiver


    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        receiver = BluetoothScanReceiver(
            onReceiveResult = {
                result = it

            },
        )
    }


    @Test
    fun `receiver emits found  device with the nearby found device`() {
        val device: BluetoothDevice = mockk(relaxed = true)
        val intent = Intent(
            BluetoothDevice.ACTION_FOUND,
        ).apply {
            putExtra(
                BluetoothDevice.EXTRA_DEVICE,
                device
            )
        }
        receiver.onReceive(context, intent)
        assertTrue(result is ScanDeviceResult.DeviceFound)
    }

    @Test
    fun `receiver emits disconnected  device disconnected `() {
        val intent = Intent(
            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED
        ).apply {
            putExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.STATE_OFF
            )
        }
        receiver.onReceive(context, intent)
        assertTrue(result is ScanDeviceResult.ConnectionLost)
    }

    @Test
    fun `receiver emits  nothing when intent matches none of registered `() {
        val intent = Intent(WifiManager.WIFI_STATE_CHANGED_ACTION)
        receiver.onReceive(context, intent)
        assertTrue(result is ScanDeviceResult.Idle)

    }

}

