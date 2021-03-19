package com.example.voicesearch.permission_handler

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicesearch.helper.Utility


/**
 * Created by Naveen Verma on 19/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

class PermissionHandler(private val context: Activity, private val permissionListener: PermissionListener) {

    companion object{
        private const val MY_PERMISSIONS_RECORD_AUDIO = 1
    }

    fun requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.RECORD_AUDIO
                )) {
                Utility.showToast(context, "Please grant permissions to record audio")

                ActivityCompat.requestPermissions(
                    context, arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            } else {
                ActivityCompat.requestPermissions(
                    context, arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            }
        } else if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            )
            == PackageManager.PERMISSION_GRANTED) {
            permissionListener.loadVoiceSearch()
        }
    }
}

interface PermissionListener {
    fun loadVoiceSearch();
}