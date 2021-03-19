package com.example.voicesearch.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.voicesearch.helper.AppConstants
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase


/**
 * Created by Naveen Verma on 19/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

class VoiceViewModel() : ViewModel() {

    companion object {
        private val TAG: String = VoiceViewModel::class.java.simpleName
    }

    fun sendDataToServer(speaker: String, text: String): LiveData<Boolean> {
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("""${speaker}_${AppConstants.TEXT_TO_SPEECH_LANG_CODE}""")
        Log.i(TAG, "sendDataToServer: $text")
        myRef.push().setValue(text).addOnSuccessListener {
            isSuccess.value = true
        }.addOnFailureListener {
            isSuccess.value = false
            Log.e(TAG, "sendDataToServer: $text", it)
        }
        return isSuccess
    }
}