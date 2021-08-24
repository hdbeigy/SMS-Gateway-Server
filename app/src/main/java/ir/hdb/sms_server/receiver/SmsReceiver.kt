package ir.hdb.sms_server.receiver

import android.R.attr.phoneNumber
import android.R.id
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.hdb.dreamyapp.utils.Utilities
import ir.hdb.sms_server.apis.APIBaseCreator
import ir.hdb.sms_server.database.DatabaseHelper
import ir.hdb.sms_server.models.MessageModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SmsReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val setting = PreferenceManager.getDefaultSharedPreferences(context)
            Log.d("hdb-----", "SMS_RECEIVED")
            val bundle = intent.extras
            if (bundle != null) {
                // get sms objects
                val pdus = bundle["pdus"] as Array<*>?
                if (pdus!!.isEmpty()) {
                    return
                }
                // large message might be broken into many
                val messages = arrayOfNulls<SmsMessage>(
                    pdus.size
                )
                val sb = StringBuilder()
                for (i in pdus.indices) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val format: String? = bundle.getString("format")
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                    } else {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                    sb.append(messages[i]?.getMessageBody())
                }
                val sender = messages[0]!!.originatingAddress
                val message = sb.toString()
                Toast.makeText(context, "$message->$sender", Toast.LENGTH_SHORT).show()
                val messageModel = MessageModel(
                    "0",
                    message,
                    sender!!,
                    System.currentTimeMillis(),
                    1
                )

                val id = DatabaseHelper(context).addReceivedMessage(messageModel)

                if (setting.getString("reply_rule", "reply_no_server") == "reply_all") {
                    val autoReplyMessage = setting.getString("auto_reply_message", "")

                    if (!autoReplyMessage.isNullOrBlank() && setting.getBoolean(
                            "auto_reply_enable",
                            false
                        )
                    )
                        sendSMS(
                            context,
                            autoReplyMessage,
                            sender
                        )
                }

                if (setting.getBoolean("message_to_server", true))
                    sendMessage(context, id.toString(), "", message, sender)
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }

    fun sendMessage(
        context: Context,
        messageId: String,
        deviceId: String,
        message: String,
        recipient: String
    ) {
        if (Utilities.isOnline(context)) {
            APIBaseCreator.apiAdapter.sendMessage(deviceId, message, recipient)
                ?.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        try {
                            val jsonStr: String = if (response.body() != null) {
                                response.body()!!.string()
                            } else {
                                assert(response.errorBody() != null)
                                response.errorBody()!!.string()
                            }

                            Log.d("hdb---sendsms", jsonStr)
                            val json = JSONObject(jsonStr)
                            val setting =
                                PreferenceManager.getDefaultSharedPreferences(context)

                            if (json.getBoolean("status")) {
                                DatabaseHelper(context).updateReceivedMessageStatus(messageId, 2)
                                if (json.has("reply") && setting.getBoolean(
                                        "message_from_server",
                                        true
                                    )
                                ) {
                                    val replyMessages = json.getJSONArray("reply")
                                    for (i in 0 until replyMessages.length()) {
                                        val replyMessage = replyMessages.getJSONObject(i)
                                        sendSMS(
                                            context,
                                            replyMessage.getString("message"),
                                            replyMessage.getString("recipient")
                                        )
                                    }
                                }
                                if (!json.has("reply") || json.getJSONArray("reply")
                                        .length() <= 0
                                ) {


                                    if (setting.getString(
                                            "reply_rule",
                                            "reply_no_server"
                                        ) == "reply_no_server"
                                    ) {
                                        val autoReplyMessage =
                                            setting.getString("auto_reply_message", "")

                                        if (!autoReplyMessage.isNullOrBlank() && setting.getBoolean(
                                                "auto_reply_enable",
                                                false
                                            )
                                        )
                                            sendSMS(
                                                context,
                                                autoReplyMessage,
                                                recipient
                                            )
                                    }
                                }

                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call2: Call<ResponseBody?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }
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
//
//    private fun sendSMS(context: Context, message: String, recipient: String) {
//
//        val db = DatabaseHelper(context)
//        val messageModel = MessageModel(
//            "0",
//            message,
//            recipient,
//            System.currentTimeMillis(),
//            1
//        )
//        val id = db.addSentMessage(messageModel)
//
//        val SENT = "SMS_SENT"
//        val DELIVERED = "SMS_DELIVERED"
//        val sentPI = PendingIntent.getBroadcast(
//            context, 0, Intent(
//                SENT
//            ), 0
//        )
//        val deliveredPI = PendingIntent.getBroadcast(
//            context, 0,
//            Intent(DELIVERED), 0
//        )
//
//        // ---when the SMS has been sent---
//        context.registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context, arg1: Intent) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        val values = ContentValues()
//                        var i = 0
////                        while (i < phoneNumber.size() - 1) {
//                        values.put(
//                            "address",
//                            phoneNumber
//                        ) // txtPhoneNo.getText().toString());
//                        values.put("body", message)
////                            i++
////                        }
//                        context.contentResolver.insert(
//                            Uri.parse("content://sms/sent"), values
//                        )
//                        Toast.makeText(
//                            context, "SMS sent",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        val result = db.updateSentMessageStatus(id.toString(), 2)
//                    }
//                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
//                        context, "Generic failure",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
//                        context, "No service",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
//                        context, "Null PDU",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
//                        context, "Radio off",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }, IntentFilter(SENT))
//
//        // ---when the SMS has been delivered---
//        context.registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context, arg1: Intent) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> Toast.makeText(
//                        context, "SMS delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Activity.RESULT_CANCELED -> Toast.makeText(
//                        context, "SMS not delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }, IntentFilter(DELIVERED))
//        val sms = SmsManager.getDefault()
//        sms.sendMultipartTextMessage(
//            recipient,
//            null,
//            sms.divideMessage(message),
//            arrayListOf(sentPI),
//            arrayListOf(deliveredPI)
//        )
//    }


    companion object {
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }
}