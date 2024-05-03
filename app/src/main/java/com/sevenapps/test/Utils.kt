package com.sevenapps.test

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.material3.AlertDialog
import androidx.core.content.ContextCompat


//ToDo aca deberian ir en realidad los permisos y cuando se piden deberia llamar aca
fun Context.isPermissionGrante(permission : String) : Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/*
inline fun Context.cameraPermissionRequest(crossinline positive: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle()
        .setMessage()
        .setPositiveButton(){ dialog, which ->
            positive.invoke()
    }.setNegativeButton(){dialog, which ->


        }.show()
}
*/