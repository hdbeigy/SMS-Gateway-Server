package ir.hdb.sms_server.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import ir.hdb.sms_server.database.DatabaseHelper
import ir.hdb.sms_server.databinding.FragmentReccyclerBinding
import ir.hdb.sms_server.ui.adapters.MessagesAdapter
import ir.hdb.sms_server.models.MessageModel

/**
 * A placeholder fragment containing a simple view.
 */
class ReceivedFragment : Fragment() {

    private var _binding: FragmentReccyclerBinding? = null
    private var db: DatabaseHelper? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val items: ArrayList<MessageModel> = arrayListOf()

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

    private fun refreshList() {
        items.clear()
        db?.receivedMessages?.let { items.addAll(it) }
        binding.recyclerView.adapter?.notifyDataSetChanged()
        binding.listEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance(): ReceivedFragment {
            return ReceivedFragment()
        }
    }

    fun dialogOptions(index: Int) {
        val selected = items[index]
        val dialog = AlertDialog.Builder(requireContext())
        val items = if (selected.status != 1) arrayOf("Delete", "Cancel") else arrayOf(
            "Send to server", "Delete", " Cancel "
        )
        dialog.setItems(
            items
        ) { dialog, which ->
            apply {
                val item = items[which]
                if (item == "Send to server") {

                } else if (item == "Delete") {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Are you about deleting this message?")
                        .setNegativeButton("Cancel", null).setPositiveButton(
                            "Delete"
                        ) { dialog, which ->
                            apply {
                                db?.deleteReceivedMessageById(selected.id)
                                this.items.clear()
                                db?.receivedMessages?.let { this.items.addAll(it) }
                                binding.recyclerView.adapter?.notifyItemRemoved(index)
                            }
                        }.show()
                } else if (item == "Cancel") {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}