package com.example.voicesearch

import android.app.Application
import android.content.Context


/**
 * Created by Naveen Verma on 9/3/21.
 * To The New
 * naveen.verma@tothenew.com
 */

class App : Application() {
    init {
        instance = this
    }

    companion object{
        private lateinit var instance: App

        fun getContext(): Context {
            return instance
        }
    }

}