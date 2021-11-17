package com.AashuDeveloper.vegiwell.service

import com.AashuDeveloper.vegiwell.Common.Common
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFCMservices : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        Common.updateToken(this,p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val dataRecv = remoteMessage.data
        if (dataRecv != null)
        {
            Common.showNotification(this, Random.nextInt(),
            dataRecv[Common.NOTI_TITLE],
            dataRecv[Common.NOTI_CONTENT],
            null)

        }
    }
}