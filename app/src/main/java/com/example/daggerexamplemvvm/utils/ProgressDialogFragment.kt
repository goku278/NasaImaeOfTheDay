package com.example.daggerexamplemvvm.utils

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyProgressDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressDialog = ProgressDialog(activity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Loading...") // Set a message for your progress dialog
        progressDialog.setCancelable(false) // Set whether the user can cancel the dialog with back button

        return progressDialog
    }
}
