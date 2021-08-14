package ir.hdb.sms_server.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ir.hdb.sms_server.models.MessageModel
import java.util.*

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, "Messages", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {

        val query = ("CREATE TABLE IF NOT EXISTS " + RECEIVED_TABLE_NAME +
                "(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "message TEXT,"
                + "recipient TEXT,"
                + "date BIGINT,"
                + "status TINYINT" +
                ");")

        val querySent = ("CREATE TABLE IF NOT EXISTS " + SENT_TABLE_NAME +
                "(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "message TEXT,"
                + "recipient TEXT,"
                + "date BIGINT,"
                + "status TINYINT);")

        db.execSQL(query)
        db.execSQL(querySent)
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("DROP TABLE IF EXISTS $RECEIVED_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $SENT_TABLE_NAME")
        onCreate(db)
    }

    fun addReceivedMessage(messageModel: MessageModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("message", messageModel.message)
        contentValues.put("recipient", messageModel.recipient)
        contentValues.put("date", messageModel.date)
        contentValues.put("status", messageModel.status)
        return db.insert(RECEIVED_TABLE_NAME, null, contentValues)
    }

    fun updateReceivedMessage(messageModel: MessageModel): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("_ID", messageModel.id)
        contentValues.put("message", messageModel.message)
        contentValues.put("recipient", messageModel.recipient)
        contentValues.put("date", messageModel.date)
        contentValues.put("status", messageModel.status)
        val result =
            db.update(RECEIVED_TABLE_NAME, contentValues, "_ID=" + messageModel.id, null).toLong()
        return result != -1L
    }

    fun updateReceivedMessageStatus(id: String, status: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("status", status)
        val result =
            db.update(RECEIVED_TABLE_NAME, contentValues, "_ID=$id", null).toLong()
        db.close()
        return result != -1L
    }

    fun updateSentMessageStatus(id: String, status: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("status", status)
        val result =
            db.update(SENT_TABLE_NAME, contentValues, "_ID=$id", null).toLong()
        db.close()
        return result != -1L
    }
    val receivedMessages: List<MessageModel>
        get() = getAll("SELECT * FROM $RECEIVED_TABLE_NAME order by _ID DESC")

    fun deleteReceivedMessageById(id: String): Boolean {
        val db = this.readableDatabase
        return db.delete(RECEIVED_TABLE_NAME, "_ID=?", arrayOf(id)) > 0
    }

    fun addSentMessage(messageModel: MessageModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("message", messageModel.message)
        contentValues.put("recipient", messageModel.recipient)
        contentValues.put("date", messageModel.date)
        contentValues.put("status", messageModel.status)
        return db.insert(SENT_TABLE_NAME, null, contentValues)
    }

    fun updateSentMessage(messageModel: MessageModel): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("_ID", messageModel.id)
        contentValues.put("message", messageModel.message)
        contentValues.put("recipient", messageModel.recipient)
        contentValues.put("date", messageModel.date)
        contentValues.put("status", messageModel.status)
        val result =
            db.update(SENT_TABLE_NAME, contentValues, "_ID=" + messageModel.id, null).toLong()
        return result != -1L
    }


    val sentMessages: List<MessageModel>
        get() = getAll("SELECT * FROM $SENT_TABLE_NAME order by _ID DESC")

    fun deleteSentMessageById(id: String): Boolean {
        val db = this.readableDatabase
        return db.delete(SENT_TABLE_NAME, "_ID=?", arrayOf(id)) > 0
    }

    fun getAll(query: String?): ArrayList<MessageModel> {
        val messageModels = ArrayList<MessageModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0).toString()
            val message = cursor.getString(1)
            val recipient = cursor.getString(2)
            val date = cursor.getLong(3)
            val status = cursor.getInt(4)

            val messageModel = MessageModel(
                id,
                message,
                recipient,
                date,
                status
            )
            messageModels.add(messageModel)
        }
        cursor.close()
        return messageModels
    }

    companion object {
        private const val RECEIVED_TABLE_NAME = "RECEIVED"
        private const val SENT_TABLE_NAME = "SENT"
    }
}