package com.example.voicesearch

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var sheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val bottomSheet = findViewById<View>(R.id.bottom_layout)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        sheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> { }
                    BottomSheetBehavior.STATE_EXPANDED -> { }
                    BottomSheetBehavior.STATE_COLLAPSED -> { }
                    BottomSheetBehavior.STATE_DRAGGING -> { }
                    BottomSheetBehavior.STATE_SETTLING -> { }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> { }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        toggleBottomSheet()
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
        sheetBehavior?.removeBottomSheetCallback(object: BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }
}