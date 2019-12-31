package com.umalt.searchedittextsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.umalt.searchedittext.OnDrawableStartTouchListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnDrawableStartTouchListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        set_address.onDrawableStartTouch = this
        set_address.setOnFocusChangeListener { _, hasFocus ->
            set_address.drawableStart = ContextCompat.getDrawable(
                this,
                if (hasFocus) {
                    R.drawable.ic_vector_back
                } else {
                    R.drawable.ic_vector_search
                }
            )
        }
    }

    override fun onDrawableStartTouch() {
        finish()
    }
}
