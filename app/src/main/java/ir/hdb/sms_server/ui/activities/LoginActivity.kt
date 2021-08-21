package ir.hdb.sms_server.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hdb.dreamyapp.utils.Utilities
import ir.hdb.sms_server.R
import ir.hdb.sms_server.apis.RequestListener
import ir.hdb.sms_server.apis.RequestManager
import ir.hdb.sms_server.utils.AppPreference
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), RequestListener {
    private lateinit var appPreference: AppPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        appPreference = AppPreference.getInstance(this)

        Utilities.setupCustomActivtyCloseToolbar(Utilities.setToolbar(this, "Login"))
    }

    override fun onResponseReceived(requestId: RequestManager.RequestId?, response: String) {
        val jsonResult = JSONObject(response)

        Toast.makeText(this, jsonResult.getString("message"), Toast.LENGTH_SHORT).show()

        if (jsonResult.getBoolean("status")) {
            appPreference.setBoolean("is_logged_in", true)
            finish()
        }
    }

    override fun onErrorReceived(t: Throwable?) {
        t?.printStackTrace()
    }
}