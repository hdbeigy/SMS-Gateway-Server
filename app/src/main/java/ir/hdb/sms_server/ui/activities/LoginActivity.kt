package ir.hdb.sms_server.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hdb.dreamyapp.utils.Utilities
import ir.hdb.sms_server.R
import ir.hdb.sms_server.apis.RequestListener
import ir.hdb.sms_server.apis.RequestManager
import ir.hdb.sms_server.apis.exceptions.ConnectionFailedException
import ir.hdb.sms_server.databinding.ActivityLoginBinding
import ir.hdb.sms_server.utils.AppPreference
import ir.hdb.sms_server.utils.DelayedProgressDialog
import org.json.JSONObject


class LoginActivity : AppCompatActivity(), RequestListener {

    private var fcmToken: String? = null
    lateinit var requestManager: RequestManager
    private val progressDialog = DelayedProgressDialog()

    private lateinit var binding: ActivityLoginBinding


    private lateinit var appPreference: AppPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        appPreference = AppPreference.getInstance(this)
        requestManager = RequestManager(this)

        Utilities.setupCustomActivtyCloseToolbar(Utilities.setToolbar(this, "Login"))

        getToken()

        binding.buttonLogin.setOnClickListener {
            if (checkInput()) {
                requestManager.login(
                    binding.editTextPhone.text.toString().trim(),
                    binding.editTextTextPassword.text.toString().trim(),
                    Utilities.getAppId(this).toString(),
                    fcmToken!!
                )
                progressDialog.show(supportFragmentManager, "")
            }
        }

    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                fcmToken = task.result
            })
    }

    private fun checkInput(): Boolean {

        if (binding.editTextPhone.text.toString().isBlank()) {
            binding.editTextPhone.error = "Enter your phone number first"
            binding.editTextPhone.requestFocus()
            return false
        }

        if (binding.editTextTextPassword.text.toString().isBlank()) {
            binding.editTextTextPassword.error = "Enter your password"
            binding.editTextTextPassword.requestFocus()
            return false
        }

        if (fcmToken.isNullOrBlank())
            getToken()

        return true
    }

    override fun onResponseReceived(requestId: RequestManager.RequestId?, response: String) {
        val jsonResult = JSONObject(response)

        if (progressDialog.showsDialog)
            progressDialog.cancel()

        Toast.makeText(this, jsonResult.getString("message"), Toast.LENGTH_SHORT).show()

        if (jsonResult.getBoolean("status")) {
            appPreference.setBoolean("is_logged_in", true)
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }

    override fun onErrorReceived(t: Throwable?) {
        if (t is ConnectionFailedException)
            Toast.makeText(
                this,
                "Connection lost, check your connection and try again!",
                Toast.LENGTH_SHORT
            ).show()
        t?.printStackTrace()
    }
}