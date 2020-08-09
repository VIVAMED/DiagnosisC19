package co.covid19.diagnosis.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Activity for the Splash Entry-Point.
 *
 * @author jaiber.yepes
 */
class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
