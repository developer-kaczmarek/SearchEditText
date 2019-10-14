package com.umalt.searchedittextsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umalt.searchedittext.OnDrawableStartTouchListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnDrawableStartTouchListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        set_address.onDrawableStartTouch = this
    }

    override fun onStartIconClick() {
        finish()
    }
}
