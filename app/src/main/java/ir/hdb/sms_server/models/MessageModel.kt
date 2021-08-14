package ir.hdb.sms_server.models

import ir.hdb.sms_server.R

data class MessageModel(
    val id: String,
    val message: String,
    val recipient: String,
    val date: Long,
    val status: Int
) {
    val statusString: String
        get() = if (status == 2) "Sent" else "Saved"

    val statusBg: Int
        get() = if (status == 2) R.drawable.shape_round_accent else R.drawable.shape_round_primary
}