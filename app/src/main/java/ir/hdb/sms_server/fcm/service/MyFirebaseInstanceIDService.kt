package ir.hdb.sms_server.fcm.service

import com.google.firebase.messaging.FirebaseMessagingService
import ir.hdb.sms_server.fcm.service.MyFirebaseInstanceIDService

class MyFirebaseInstanceIDService : FirebaseMessagingService() {
    private fun sendRegistrationToServer(token: String) {
        // sending gcm token to server
//        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        //        Log.d("hdb-token", s);
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        // Saving reg id to shared preferences
//        storeRegIdInPref(refreshedToken);
//
//        // sending reg id to your server
//        sendRegistrationToServer(refreshedToken);
//
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", refreshedToken);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    } //    private void storeRegIdInPref(String token) {

    //
    //        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
    //        SharedPreferences.Editor editor = pref.edit();
    //        editor.putString("regId", token);
    //        editor.commit();
    //    }

    companion object {
        private val TAG = MyFirebaseInstanceIDService::class.java.simpleName
    }
}