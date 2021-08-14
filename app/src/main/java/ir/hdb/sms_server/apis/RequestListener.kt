package ir.hdb.sms_server.apis

import ir.hdb.sms_server.apis.RequestManager.RequestId

/**
 * Created by Hadi Beigy on 3/9/2018.
 */
interface RequestListener {
    fun onResponseReceived(requestId: RequestId?, vararg response: String)
    fun onErrorReceived(t: Throwable?)
}