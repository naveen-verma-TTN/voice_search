package com.example.voicesearch.viewmodel

import android.R
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicesearch.app.App
import com.example.voicesearch.helper.AppConstants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


/**
 * Created by Naveen Verma on 19/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

class VoiceViewModel : ViewModel() {

    companion object {
        private val TAG: String = VoiceViewModel::class.java.simpleName
    }

    val status: MutableLiveData<ProgressStatus> = MutableLiveData()
    val response: MutableLiveData<List<String>> = MutableLiveData()

    fun sendDataToServer(speaker: String, text: String): LiveData<Boolean> {
        status.value = ProgressStatus.SHOW_PROGRESS
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("${speaker}-${AppConstants.TEXT_TO_SPEECH_LANG_CODE}".toUpperCase(
            Locale.getDefault()))
        Log.i(TAG, "sendDataToServer: $text")
        myRef.push().setValue(text).addOnSuccessListener {
            isSuccess.value = true
            readFromDataBase(speaker)
        }.addOnFailureListener {
            Log.e(TAG, "sendDataToServer: $text", it)
            isSuccess.value = false
        }
        return isSuccess
    }

    fun readFromDataBase(speaker: String) {
        status.value = ProgressStatus.SHOW_PROGRESS
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("${speaker}-${AppConstants.TEXT_TO_SPEECH_LANG_CODE}".toUpperCase(
            Locale.getDefault()
        )
        )
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list: MutableList<String> = ArrayList()
                for (ds in dataSnapshot.children) {
                    val title = ds.getValue(String::class.java)
                    title?.let {
                        list.add(it)
                    }
                }
                viewModelScope.launch {
                    async {
                        delay(1000)
                        response.value = list
                        status.value = ProgressStatus.HIDE_PROGRESS
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
                viewModelScope.launch {
                    async {
                        delay(1000)
                        response.value = mutableListOf()
                        status.value = ProgressStatus.HIDE_PROGRESS
                    }
                }
            }
        })
    }
}

/**
 * enum class for ProgressStatus
 */
enum class ProgressStatus {
    SHOW_PROGRESS,
    HIDE_PROGRESS,
}