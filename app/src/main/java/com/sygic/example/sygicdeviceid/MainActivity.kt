package com.sygic.example.sygicdeviceid

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.database.getStringOrNull
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

private const val KEY_ID = "id"

class MainActivity : AppCompatActivity() {

    // use "content://com.sygic.fleet.provider/id" for Sygic Professional navigation
    // use "content://com.sygic.truck.provider/id" for Sygic Truck navigation
    private val sygicProviderUri = Uri.parse("content://com.sygic.truck.provider/id")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetId.setOnClickListener {
            val sygicId = getSygicId()
            if(sygicId != null)
                textId.text = sygicId
            else
                Toast.makeText(this, "Failed to get ID", Toast.LENGTH_SHORT).show()
        }

        btnGenerateId.setOnClickListener {
            editId.setText(UUID.randomUUID().toString(), TextView.BufferType.EDITABLE)
        }

        btnSetId.setOnClickListener {
            val newId = editId.text.toString()
            if(!TextUtils.isEmpty(newId)) {
                val success = setSygicId(newId)
                val msg = if(success) "New ID set successfully" else "Failed to set ID"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Returns Sygic device ID or null if device ID can't be read. */
    private fun getSygicId(): String? {
        contentResolver.query(sygicProviderUri, arrayOf(KEY_ID), null, null, null)?.use {
            return if(it.moveToFirst()) it.getStringOrNull(0) else null
        }
        return null
    }

    /** Pushes new alternative ID to Sygic app. Returns true if success. */
    private fun setSygicId(id: String): Boolean {
        val contentValues = ContentValues(1).apply {
            put(KEY_ID, id)
        }
        return contentResolver.insert(sygicProviderUri, contentValues) != null
    }
}
