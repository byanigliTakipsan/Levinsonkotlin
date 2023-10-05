package com.takipsan.levinson.alert

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.takipsan.levinson.R


class MLoadingAlerts(private val context: Context, private val message: String?) {

    private val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_loading, null)

    init {
        if (message != null) {
            val Text: TextView = dialogView.findViewById<TextView>(R.id.loadingMessage) as TextView
            Text.text = message
        }
    }

    private val alertDialog = MaterialAlertDialogBuilder(context)
        .setView(dialogView)
        .setCancelable(false)
        .create()

    fun goster() {
        alertDialog.show()
    }

    fun kapat() {
        alertDialog.dismiss()
    }
}