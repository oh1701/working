package com.working.working_project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class alarmrecive: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var intent = Intent(context, alarmservice::class.java)

        if (Build.VERSION.SDK_INT >= 26)
            context?.startForegroundService(intent)
        else
            context?.startService(intent)
    }

}