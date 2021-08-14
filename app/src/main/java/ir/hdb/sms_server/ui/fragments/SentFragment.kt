package ir.hdb.sms_server.ui.fragments

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import ir.hdb.sms_server.database.DatabaseHelper
import ir.hdb.sms_server.databinding.FragmentReccyclerBinding
import ir.hdb.sms_server.models.MessageModel
import ir.hdb.sms_server.ui.adapters.MessagesAdapter


/**
 * A placeholder fragment containing a simple view.
 */
class SentFragment : Fragment() {

    private var _binding: FragmentReccyclerBinding? = null
    private val items: ArrayList<MessageModel> = arrayListOf()
    private var db: DatabaseHelper? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReccyclerBinding.inflate(inflater, container, false)

        db = DatabaseHelper(requireContext())

        binding.recyclerView.adapter = MessagesAdapter(items) { index ->
            dialogOptions(index)
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshList()
            binding.swipeRefresh.isRefreshing = false

        }

        refreshList()
        return binding.root
    }

    fun dialogOptions(index: Int) {
        val selected = items[index]
        val dialog = AlertDialog.Builder(requireContext())
        val items = if (selected.status != 1) arrayOf("Delete", "Cancel") else arrayOf(
            "Send SMS", "Delete", " Cancel "
        )
        dialog.setItems(
            items
        ) { dialog, which ->
            apply {
                val item = items[which]
                if (item == "Send SMS") {

                    sendSMS(selected)
                } else if (item == "Delete") {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Are you about deleting this message?")
                        .setNegativeButton("Cancel", null).setPositiveButton(
                            "Delete"
                        ) { dialog, which ->
                            apply {
                                db?.deleteSentMessageById(selected.id)
                                refreshList()
                            }
                        }.show()
                } else if (item == "Cancel") {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun sendSMS(messageModel: MessageModel) {
        val phoneNumber: String = messageModel.recipient
        val message: String = messageModel.message
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sentPI = PendingIntent.getBroadcast(
            requireContext(), 0, Intent(
                SENT
            ), 0
        )
        val deliveredPI = PendingIntent.getBroadcast(
            requireContext(), 0,
            Intent(DELIVERED), 0
        )

        // ---when the SMS has been sent---
        requireActivity().registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val values = ContentValues()
                        var i = 0
//                        while (i < phoneNumber.size() - 1) {
                            values.put(
                                "address",
                                phoneNumber
                            ) // txtPhoneNo.getText().toString());
                            values.put("body", message)
//                            i++
//                        }
                        requireContext().contentResolver.insert(
                            Uri.parse("content://sms/sent"), values
                        )
                        Toast.makeText(
                            requireContext(), "SMS sent",
                            Toast.LENGTH_SHORT
                        ).show()
                        val result = db?.updateSentMessageStatus(messageModel.id, 2)
                        Log.d("hdb--update", result.toString())
                        refreshList()
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        requireContext(), "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        requireContext(), "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                        requireContext(), "Null PDU",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        requireContext(), "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(SENT))

        // ---when the SMS has been delivered---
        requireContext().registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        requireContext(), "SMS delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        requireContext(), "SMS not delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(DELIVERED))
        val sms = SmsManager.getDefault()
        sms.sendMultipartTextMessage(phoneNumber, null, sms.divideMessage(message), arrayListOf(sentPI), arrayListOf(deliveredPI))
    }

    private fun refreshList() {
        items.clear()
        db?.sentMessages?.let { items.addAll(it) }
        binding.recyclerView.adapter?.notifyDataSetChanged()
        binding.listEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance(): SentFragment {
            return SentFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}