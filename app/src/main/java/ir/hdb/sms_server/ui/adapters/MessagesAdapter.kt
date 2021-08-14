package ir.hdb.sms_server.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.hdb.sms_server.R
import ir.hdb.sms_server.databinding.ListMessageItemBinding
import ir.hdb.sms_server.models.MessageModel
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(val items: ArrayList<MessageModel>, val onItemClick: (index: Int) -> Unit) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    @SuppressLint("SimpleDateFormat")
    var dt: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val viewHolder = MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_message_item, parent, false)
        )

        viewHolder.itemView.setOnClickListener {
            onItemClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        val current = items[position]

        holder.binding.messageMessage.text = current.message
        holder.binding.messageDate.text = "(" + dt.format(Date(current.date)) + ")"
        holder.binding.messageRecipient.text = current.recipient
        holder.binding.messageStatus.text = current.statusString
        holder.binding.messageStatus.setBackgroundResource(current.statusBg)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListMessageItemBinding.bind(itemView)
    }
}