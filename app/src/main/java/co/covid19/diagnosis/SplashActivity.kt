package co.covid19.diagnosis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.covid19.diagnosis.MainActivity

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}