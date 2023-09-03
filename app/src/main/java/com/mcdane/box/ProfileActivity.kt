package com.mcdane.box

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText

class ProfileActivity : AppCompatActivity() {
    companion object {
        const val RESULT_CANCEL = 0
        const val RESULT_SAVE = 1
    }

    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initUI()
    }

    private fun initUI() {
        firstNameText = findViewById(R.id.first_name_edit)
        lastNameText = findViewById(R.id.last_name_edit)
        val playerSet: Boolean = intent.getBooleanExtra("playerSet", false)
        if (playerSet) {
            firstNameText.setText(intent.getStringExtra("firstName") ?: "")
            lastNameText.setText(intent.getStringExtra("lastName") ?: "")
        }

        cancelButton = findViewById(R.id.cancel_name_button)
        cancelButton.setOnClickListener { onCancelClicked() }
        saveButton = findViewById(R.id.save_name_button)
        saveButton.setOnClickListener { onSaveClicked() }
    }

    private fun onCancelClicked() {
        setResult(RESULT_CANCEL)
        finish()
    }

    private fun onSaveClicked() {
        if (!validate()) {
            return
        }

        val intent = Intent()
        intent.putExtra("firstName", firstNameText.text.toString())
        intent.putExtra("lastName", lastNameText.text.toString())
        setResult(RESULT_SAVE, intent)
        finish()
    }

    private fun validate(): Boolean =
        when {
            firstNameText.text.isBlank() -> {
                showAlert("Invalid first name")
                firstNameText.requestFocus()
                false
            }
            lastNameText.text.isBlank() -> {
                showAlert("Invalid last name")
                lastNameText.requestFocus()
                false
            }
            else -> true
        }

    private fun showAlert(msg: String) {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setMessage(msg)
                setPositiveButton(R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
            }.show()
        }
    }
}