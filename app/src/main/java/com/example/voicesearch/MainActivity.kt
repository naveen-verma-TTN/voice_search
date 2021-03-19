package com.example.voicesearch

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.voicesearch.adapter.TextAdapter
import com.example.voicesearch.helper.Utility
import com.example.voicesearch.permission_handler.PermissionHandler
import com.example.voicesearch.permission_handler.PermissionListener
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback


class MainActivity : AppCompatActivity(), PermissionListener, VoiceSearchFragment.VoiceData {

    companion object{
        private const val MY_PERMISSIONS_RECORD_AUDIO = 1
    }

    private var sheetBehavior: BottomSheetBehavior<*>? = null
    private lateinit var textAdapter: TextAdapter
    private lateinit var permissionHandler: PermissionHandler
    private val dataList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        permissionHandler = PermissionHandler(this, this)

        val bottomSheet = findViewById<View>(R.id.bottom_layout)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetCallback()
    }

    override fun loadVoiceSearch() {
        toggleBottomSheet()
        initRecyclerView()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.voice_fragment, VoiceSearchFragment())
        ft.commit()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        textAdapter = TextAdapter(dataList)

        val manager = FlexboxLayoutManager(this)

        manager.flexDirection = FlexDirection.ROW
        manager.justifyContent = JustifyContent.FLEX_START

        recyclerView.layoutManager = manager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = textAdapter
    }

    private fun bottomSheetCallback() {
        sheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        permissionHandler.requestAudioPermissions()
    }

    private fun toggleBottomSheet() {
        if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sheetBehavior?.removeBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    loadVoiceSearch()
                } else {
                    Utility.showToast(this, "Permissions Denied to record audio")
                }
                return
            }
        }
    }

    override fun sendData(speaker: String?, text: String?) {
            text?.let {
                dataList.add(it)
                textAdapter.setList(dataList)
            }
        }
}