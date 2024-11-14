package com.mohaberabi.bluechat.core.domain.source

import com.mohaberabi.bluechat.core.domain.model.UserModel

interface UserLocalDataSource {


    fun getCurrentUser(): UserModel
}