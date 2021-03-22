package com.example.voicesearch.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.example.voicesearch.MainActivity
import com.example.voicesearch.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


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

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(listOf(viewModelModule))
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}