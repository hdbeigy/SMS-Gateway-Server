package ir.hdb.sms_server.apis

import android.os.Build
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object APIBaseCreator {
    var baseUrl = "https://raiatolhoda.ir/targolapp/"

    //            File httpCacheDirectory = new File(MyApplication.context.getCacheDir(), "http-cache");
//            int cacheSize = 10 * 1024 * 1024; // 10 MiB
//            Cache cache = new Cache(httpCacheDirectory, cacheSize);
    val apiAdapter: APIAdapter
        get() {

            val retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseUrl + "api/")

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            File httpCacheDirectory = new File(MyApplication.context.getCacheDir(), "http-cache");
//            int cacheSize = 10 * 1024 * 1024; // 10 MiB
//            Cache cache = new Cache(httpCacheDirectory, cacheSize);
                retrofitBuilder
                    .client(
                        OkHttpClient()
                            .newBuilder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(
                                30,
                                TimeUnit.SECONDS
                            ) //                        .cache(cache)
                            .retryOnConnectionFailure(true)
                            .build()
                    )
            }
            return retrofitBuilder.build().create(APIAdapter::class.java)
        }
}