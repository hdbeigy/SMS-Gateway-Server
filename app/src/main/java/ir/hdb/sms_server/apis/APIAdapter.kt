package ir.hdb.sms_server.apis

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface APIAdapter {

    @GET("App.php?do=IsCouponValid")
    fun isCouponValid(@Query("code") code: String?): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("sms_server.php?do=send_sms")
    fun sendMessage(
        @Field("deviceId") deviceId: String?,
        @Field("message") message: String,
        @Field("recipient") recipient: String
    ): Call<ResponseBody>?

    @FormUrlEncoded
    @POST("sms_server.php?do=login")
    fun login(
        @Field("username") username: String?,
        @Field("password") password: String,
        @Field("device_id") deviceId: String,
        @Field("fcm_token") fcmToken: String
    ): Call<ResponseBody>?

}