package ir.hdb.sms_server.fcm.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.hdb.sms_server.R
import ir.hdb.sms_server.database.DatabaseHelper
import ir.hdb.sms_server.models.MessageModel
import ir.hdb.sms_server.ui.activities.MainActivity
import org.json.JSONObject

//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        scheduleJob();

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d("hdb2", "From: " + remoteMessage.getFrom());
        val params: String = remoteMessage.data.toString()
//        if (params.isNotBlank()) {
        val json = JSONObject(params)
        Log.e("hdb2--json", json.toString())
        if (json.has("recipient")) {
            val message = json.getString("message")
            var recipient: String = json.getString("recipient")
            if (recipient.contains("tel-"))
                recipient = recipient.replace("tel-", "")
            sendSMS(this, message, recipient)
        }
//        }

//        Toast.makeText(this, object.toString(), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, remoteMessage.getFrom(), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, remoteMessage.getData().toString(), Toast.LENGTH_LONG).show();

//        String cId = null, catId = null;

//        if (remoteMessage.getData() != null) {
//            if (remoteMessage.getData().containsKey("cId")) {
//                cId = remoteMessage.getData().get("cId");
//            } else if (remoteMessage.getData().containsKey("catId")) {
//                catId = remoteMessage.getData().get("catId");
//            }
//        }

        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
////            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
//            } else {
//                handleNow();
//            }
//
//        }
        if (remoteMessage.notification != null && remoteMessage.notification!!.body != null) sendNotification(
            remoteMessage.notification!!.title, remoteMessage.notification!!.body
        )
    }

    override fun onNewToken(token: String) {
//        Log.d("hdb", "Refreshed token: " + token);
//        sendRegistrationToServer(token);
    }

    //    private void scheduleJob() {
    //        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
    //                .build();
    //        WorkManager.getInstance().beginWith(work).enqueue();
    //
    //    }
    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.");
    }

    private fun sendRegistrationToServer(token: String) {}
    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    fun sendSMS(context: Context, message: String, recipient: String) {

        val db = DatabaseHelper(context)
        val messageModel = MessageModel(
            "0",
            message,
            recipient,
            System.currentTimeMillis(),
            1
        )
        val id = db.addSentMessage(messageModel)
        val sms: SmsManager = SmsManager.getDefault()
        val sentPI: PendingIntent = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), 0)
        sms.sendMultipartTextMessage(
            recipient,
            null,
            arrayListOf(message),
            arrayListOf(sentPI),
            null
        )
        db.updateSentMessageStatus(id.toString(), 2)

    }
}