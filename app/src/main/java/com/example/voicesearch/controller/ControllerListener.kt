package com.example.voicesearch.controller


/**
 * Created by Naveen Verma on 11/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

class ControllerListener(private val listener: Listener?) {

    fun startRecording() {
        listener?.showRecodingWave("file:///android_asset/px_wave.html", true)
    }

    fun stopRecording() {
        listener?.showRecodingWave("file:///android_asset/sin_wave/sin_wave.html", false);
    }

    interface Listener {
        fun onStartListen()
        fun showRecodingWave(location: String, isListening: Boolean)
    }
}