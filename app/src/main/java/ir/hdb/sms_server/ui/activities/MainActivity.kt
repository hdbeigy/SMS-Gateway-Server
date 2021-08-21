package ir.hdb.sms_server.ui.activities

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import ir.hdb.sms_server.R
import ir.hdb.sms_server.database.DatabaseHelper
import ir.hdb.sms_server.databinding.ActivityMainBinding
import ir.hdb.sms_server.models.MessageModel
import ir.hdb.sms_server.ui.adapters.SectionsPagerAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbar)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        getSendPermission()
        getPermission()

    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECEIVE_SMS),
                1200
            )
        }
    }

    fun getSendPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.SEND_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.SEND_SMS),
                1200
            )
        }
    }
//    fun sendSms(context: Context, message: String, recipient: String) {
//        val db = DatabaseHelper(context)
//        val messageModel = MessageModel(
//            "0",
//            message,
//            recipient,
//            System.currentTimeMillis(),
//            1
//        )
//        val id = db.addSentMessage(messageModel)
//        val sms: SmsManager = SmsManager.getDefault()
////        val sentPI: PendingIntent = PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), 0)
//        sms.sendMultipartTextMessage(recipient, null, sms.divideMessage(message), null, null)
////        db.updateSentMessageStatus(id.toString(), 2)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_settings)
            startActivity(Intent(this, SettingsActivity::class.java))

        return super.onOptionsItemSelected(item)
    }
}