package com.mohaberabi.bluechat.core.domain.model.errros

import com.mohaberabi.bluechat.core.domain.model.AppResult
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException


inline fun <T> handleBluetoothOperation(
    block: () -> T
): AppResult<T, AppError> {
    return try {
        AppResult.Done(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        AppResult.Error(BluetoothError.AlreadyUnregisteredResources, e)
    } catch (e: SecurityException) {
        e.printStackTrace()
        AppResult.Error(BluetoothError.PermissionNotGranted, e)
    } catch (e: UnsupportedOperationException) {
        e.printStackTrace()
        AppResult.Error(BluetoothError.NotSupported, e)
    } catch (e: IOException) {
        e.printStackTrace()
        AppResult.Error(SocketConnectionError.DataTransferError)
    } catch (e: Exception) {
        e.printStackTrace()
        AppResult.Error(BluetoothError.Unknown, e)
    }
}