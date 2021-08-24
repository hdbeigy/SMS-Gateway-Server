package ir.hdb.sms_server.apis

import android.content.Context
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import com.hdb.dreamyapp.utils.Utilities.isOnline
import ir.hdb.sms_server.apis.exceptions.ConnectionFailedException
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestManager(var requestListener: RequestListener) {
    enum class RequestId {
        SEND_MESSAGE,
        LOGIN,
    }

    private var context: Context? = null
//    fun getIndex(userId: String?, brand: String?) {
//        enque(RequestId.GET_MAIN_PAGE, APIBaseCreator.apiAdapter.getMainPage(userId, brand))
//    }

    fun sendMessage(deviceId: String, message: String, recipient: String) {
        APIBaseCreator.apiAdapter.sendMessage(deviceId, message, recipient)?.let {
            enque(
                RequestId.SEND_MESSAGE,
                it
            )
        };

    }

    fun login(username: String, password: String, deviceId: String, fcmToken: String) {
        APIBaseCreator.apiAdapter.login(username, password, deviceId, fcmToken)?.let {
            enque(
                RequestId.LOGIN,
                it
            )
        };

    }

    private fun enque(requestId: RequestId, call: Call<ResponseBody>): Call<ResponseBody> {
        if (isOnline(context!!)) {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    try {
                        val responseString: String
                        responseString = if (response.body() != null) {
                            response.body()!!.string()
                        } else {
                            assert(response.errorBody() != null)
                            response.errorBody()!!.string()
                        }
                        requestListener.onResponseReceived(requestId, responseString)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        requestListener.onErrorReceived(e)
                    }
                }

                override fun onFailure(call2: Call<ResponseBody?>, t: Throwable) {
                    t.printStackTrace()
                    requestListener.onErrorReceived(t)
                }
            })
            return call
        } else {
            requestListener.onErrorReceived(ConnectionFailedException())
        }
        return call
    }

    init {
        if (requestListener is Context) {
            context = requestListener as Context
        } else if (requestListener is Fragment) {
            Log.d("hdb", "its fragment")
            context = (requestListener as Fragment).activity
        }
    }
}