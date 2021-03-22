package com.example.voicesearch

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.voicesearch.adapter.TextAdapter
import com.example.voicesearch.app.App
import com.example.voicesearch.helper.Utility
import com.example.voicesearch.permission_handler.PermissionHandler
import com.example.voicesearch.permission_handler.PermissionListener
import com.example.voicesearch.viewmodel.ProgressStatus
import com.example.voicesearch.viewmodel.VoiceViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.magicgoop.tagsphere.item.TextTagItem
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), PermissionListener, VoiceSearchFragment.VoiceData {

    private val userViewModel by viewModel<VoiceViewModel>()

    companion object {
        private const val MY_PERMISSIONS_RECORD_AUDIO = 1
    }

    private var sheetBehavior: BottomSheetBehavior<*>? = null
    private lateinit var textAdapter: TextAdapter
    private lateinit var permissionHandler: PermissionHandler
    private val dataList: ArrayList<String> = ArrayList()
    private var speaker: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        permissionHandler = PermissionHandler(this, this)

        val bottomSheet = findViewById<View>(R.id.bottom_layout)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetCallback()

        addListeners()

        tagView.setRadius(1f)
        tagView.setTextPaint(
            TextPaint().apply {
                isAntiAlias = true
                textSize = resources.getDimension(R.dimen.text_size_12)
                color = Color.WHITE
            }
        )
    }

    private fun addListeners() {
        userViewModel.status.observe(this, { status ->
            status?.let {
                when (it) {
                    ProgressStatus.SHOW_PROGRESS -> {
                        avi.smoothToShow()
                    }
                    ProgressStatus.HIDE_PROGRESS -> {
                        avi.smoothToHide()
                    }
                }
            }
        })

        userViewModel.readFromDataBase("MALE")

        userViewModel.response.observe(this, { response ->
            if (response.isNotEmpty()) {
                tagView.clearAllTags()
                response.map {
                    TextTagItem(text = it)
                }.toList().let { tagView.addTagList(it) }
            }
            else {
                tagView.clearAllTags()
            }
        })
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
                        supportActionBar?.hide()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        supportActionBar?.show()
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    override fun sendData(speaker: CharSequence, text: String) {
        this.speaker = speaker.toString()
        dataList.add(text)
        tagView.addTag(TextTagItem(text))
        textAdapter.setList(text)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_upload) {
            speaker?.let { s ->
                dataList.forEach { text ->
                    userViewModel.sendDataToServer(s, text).observe(this, { isSuccess ->
                        if (isSuccess) {
                            dataList.clear()
                            Utility.showToast(App.getContext(), "Data uploaded to the cloud server")
                        } else {
                            Utility.showToast(App.getContext(), "Failed to save data")
                        }
                    })
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}