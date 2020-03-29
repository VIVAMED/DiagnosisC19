package co.covid19.diagnosis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.opensooq.supernova.gligar.GligarPicker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory = MainViewModelFactory(this.application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.bitmap.observe(this, Observer {
            image_view.setImageBitmap(it)
        })

        viewModel.result.observe(this, Observer {
            result_view.text = it
        })

        fab_pick_photo.setOnClickListener {
            GligarPicker().limit(1).disableCamera(false).cameraDirect(false)
                .requestCode(PICKER_REQUEST_CODE)
                .withActivity(this)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            PICKER_REQUEST_CODE -> {
                val imagesList = data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)
                if (!imagesList.isNullOrEmpty()) {
                    viewModel.setImagePath(imagesList[0])
                }
            }
        }
    }

    companion object {
        private const val PICKER_REQUEST_CODE = 30
        private const val TAG = "MainActivity"
    }
}
