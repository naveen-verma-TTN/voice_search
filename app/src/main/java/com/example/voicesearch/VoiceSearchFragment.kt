package com.example.voicesearch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.voicesearch.app.App
import com.example.voicesearch.controller.ControllerListener
import com.example.voicesearch.databinding.FragmentVoiceSearchBinding
import com.example.voicesearch.helper.AppConstants
import com.example.voicesearch.viewmodel.VoiceViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_voice_search.*
import kotlinx.android.synthetic.main.fragment_voice_search.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class VoiceSearchFragment : Fragment(), ControllerListener.Listener {

    private val userViewModel by viewModel<VoiceViewModel>()

    interface VoiceData {
        fun sendData(speaker: CharSequence, text: String)
    }

    var stateTV: TextView? = null
    var displayTV: TextView? = null
    private lateinit var mBinding: FragmentVoiceSearchBinding
    private lateinit var mSpeechRecognizer: SpeechRecognizer
    private var mSpeechRecognizerIntent: Intent? = null
    private var controller: ControllerListener? = null
    private lateinit var webView: WebView
    private var textToSpeech: TextToSpeech? = null
    private lateinit var voiceData: VoiceData
    private var chip: Chip? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            voiceData = activity as VoiceData
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement onSomeEventListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_voice_search,
            container,
            false
        )
        stateTV = mBinding.stateTv
        displayTV = mBinding.displayTv

        controller = ControllerListener(this)
        textToSpeech = TextToSpeech(App.getContext().applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                val voice = Voice(
                    "voice",
                    Locale.getDefault(),
                    Voice.QUALITY_VERY_HIGH,
                    Voice.LATENCY_NORMAL,
                    false,
                    null
                )
                textToSpeech?.voice = voice
                textToSpeech?.language =
                    Locale.forLanguageTag(AppConstants.TEXT_TO_SPEECH_LANG_CODE)
            }
        }

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        mSpeechRecognizer.setRecognitionListener(Recogniser())

        createRecognizerIntent()

        mBinding.executePendingBindings()
        return mBinding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.animation_webView) as WebView
        webView.settings.javaScriptEnabled = true
        // disable scroll on touch
        // disable scroll on touch
        webView.setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }
        webView.isScrollContainer = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        initChipView(view)

        view.mic_view.setOnClickListener {
            startListening()
        }

        startListening()
    }

    private fun initChipView(view: View) {
        val chipGroup: ChipGroup = view.findViewById(R.id.chip_group)
        mBinding.chipMale.isChecked = true
        chipGroup.setOnCheckedChangeListener { cg, i ->
            chip = cg.findViewById(i)
            userViewModel.readFromDataBase(chip?.text?.toString() ?: "MALE")
        }
    }

    private fun createRecognizerIntent() {
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        mSpeechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            AppConstants.SPEECH_TO_TEXT_LANG_CODE
        )
        mSpeechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
            AppConstants.SPEECH_TO_TEXT_LANG_CODE
        )
        mSpeechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            65000
        )
        mSpeechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
            65000
        )
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        mSpeechRecognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            requireActivity().packageName
        )
    }

    override fun onStartListen() {
        startListening()
    }

    override fun showRecodingWave(location: String, isListening: Boolean) {
        webView.loadUrl(location)
        toggleMic(isListening)
    }

    private fun toggleMic(isListening: Boolean) {
        if(mic_view != null) {
            if (isListening) {
                mic_view.visibility = View.GONE
            } else {
                displayTV!!.text = App.getContext().resources.getString(R.string.tap_on_mic)
                displayTV!!.visibility = View.VISIBLE
                mic_view.visibility = View.VISIBLE
            }
        }
    }

    private fun startListening() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
        controller?.startRecording()
        webView.visibility = View.VISIBLE
        stateTV?.text = getString(R.string.listening)
        displayTV?.text = App.getContext().resources.getString(R.string.try_saying_bottom)
    }

/*    override fun onCancel() {
        try {
            mSpeechRecognizer.cancel()
            mSpeechRecognizer.destroy()
        } catch (ignored: Exception) {
        }
        try {
            controller?.stopRecording()
        } catch (ignored: Exception) {
        }
    }*/

    fun onFailure() {
        controller?.stopRecording()
        stateTV!!.text = App.getContext().resources.getString(R.string.try_saying_bottom)
        displayTV!!.text = App.getContext().resources.getString(R.string.tap_on_mic)
        displayTV!!.visibility = View.VISIBLE
    }

    internal inner class Recogniser : RecognitionListener {
        override fun onPartialResults(partialResults: Bundle) {
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?: return
            var textFromVoice: String? = ""
            for ((i, s) in matches.withIndex()) {
                if (i == 0) {
                    textFromVoice = s
                }
            }
            stateTV!!.text = textFromVoice
            displayTV!!.visibility = View.INVISIBLE
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return
            var voiceSearchResult: String? = ""
            for ((i, s) in matches.withIndex()) {
                if (i == 0) {
                    voiceSearchResult = s
                }
            }
            stateTV!!.text = voiceSearchResult

            voiceData.sendData(chip?.text ?: "MALE", voiceSearchResult!!)

            Handler(Looper.getMainLooper()).postDelayed({
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech?.speak(voiceSearchResult, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    textToSpeech?.speak(voiceSearchResult, TextToSpeech.QUEUE_FLUSH, null)
                }
                controller?.stopRecording()
                toggleMic(false)
            }, 500)
        }

        override fun onReadyForSpeech(params: Bundle) {}
        override fun onRmsChanged(v: Float) {}
        override fun onBeginningOfSpeech() {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            onFailure()
        }

        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}

