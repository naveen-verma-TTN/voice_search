package com.example.voicesearch.app.di

import com.example.voicesearch.viewmodel.VoiceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { VoiceViewModel() }
}