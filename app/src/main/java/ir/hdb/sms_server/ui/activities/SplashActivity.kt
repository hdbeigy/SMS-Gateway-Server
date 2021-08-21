package ir.hdb.sms_server.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import ir.hdb.sms_server.R
import ir.hdb.sms_server.utils.AppPreference

class SplashActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        appPreference = AppPreference.getInstance(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent =
                if (!appPreference.getBoolean("is_logged_in", false))
                    Intent(this, LoginActivity::class.java)
                else
                    Intent(this, MainActivity::class.java)

            startActivity(intent)
            finish()

        }, 1000)
    }
}