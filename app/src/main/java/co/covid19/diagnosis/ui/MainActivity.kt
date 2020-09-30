package co.covid19.diagnosis.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.covid19.diagnosis.R
import co.covid19.diagnosis.viewmodel.MainViewModel
import co.covid19.diagnosis.viewmodel.MainViewModelFactory
import com.opensooq.supernova.gligar.GligarPicker
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Activity for the Main Entry-Point.
 *
 * @author jaiber.yepes
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory =
            MainViewModelFactory(this.application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.bitmapLiveData.observe(this, Observer {
            imageView.setImageBitmap(it)
        })

        viewModel.imageNameLiveData.observe(this, Observer {
            imageNameView.text = it
        })

        viewModel.resultLiveData.observe(this, Observer {
            resultView.text = it
        })

        viewModel.isProcessingLiveData.observe(this, Observer {
            if(it == true){
                pickPhotoButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

            } else {
                pickPhotoButton.isEnabled = true
                progressBar.visibility = View.GONE
            }
        })

        pickPhotoButton.setOnClickListener {
            imageView.setImageResource(android.R.color.transparent);
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
                    viewModel.runModel()
                }
            }
        }
    }

    companion object {
        private const val PICKER_REQUEST_CODE = 30
        private const val TAG = "MainActivity"
    }
}
