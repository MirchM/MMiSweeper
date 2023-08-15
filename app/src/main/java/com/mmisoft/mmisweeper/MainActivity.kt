package com.mmisoft.mmisweeper

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemBars()
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun changeMainFragment(fragment: Fragment?, tag: String?) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
        ft.replace(R.id.fragmentContainerView, fragment!!, tag)
                .setReorderingAllowed(true)
                .addToBackStack(tag)
                .commit()
    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
                ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    fun backBtn() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (supportFragmentManager.backStackEntryCount == 0 && keyCode == KeyEvent.KEYCODE_BACK) {
            count++
            if (count > 1) {
                moveTaskToBack(true)
            } else {
                Toast.makeText(this, "Press back again to Leave!", Toast.LENGTH_SHORT).show()
                // resetting the counter in 2s
                val handler = Handler()
                handler.postDelayed({ count = 0 }, 2000)
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
