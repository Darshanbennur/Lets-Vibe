package com.example.letsvibe

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog(var activity : Activity) {
    private lateinit var dial : AlertDialog

    fun LoaderInitiate(){
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_loader,null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dial = builder.create()
        dial.show()
    }

    fun dismiss(){
        dial.dismiss()
    }

}