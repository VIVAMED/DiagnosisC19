package com.opensooq.supernova.gligar

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.opensooq.supernova.gligar.ui.ImagePickerActivity.Companion.EXTRA_CAMERA_DIRECT
import com.opensooq.supernova.gligar.ui.ImagePickerActivity.Companion.EXTRA_DISABLE_CAMERA
import com.opensooq.supernova.gligar.ui.ImagePickerActivity.Companion.EXTRA_FOLDER_POSITION
import com.opensooq.supernova.gligar.ui.ImagePickerActivity.Companion.EXTRA_LIMIT
import com.opensooq.supernova.gligar.ui.ImagePickerActivity.Companion.EXTRA_ITEM_POSITION
import com.opensooq.supernova.gligar.ui.ImagePickerActivity.Companion.startActivityForResult
import java.lang.IllegalStateException

/**
 * Created by Hani AlMomani on 30,November,2019
 */


class GligarPicker {

    companion object {
        const val IMAGES_RESULT = "images"
        const val CURRENT_ALBUM_POSITION_RESULT = "folder_position"
        const val CURRENT_ITEM_POSITION_RESULT = "item_position"
    }

    private var withActivity: Activity? = null
    private var withFragment: Fragment? = null
    private var requestCode: Int = 0
    private var limit: Int = 10
    private var folderPosition: Int = 0
    private var itemPosition: Int = 0
    private var disableCamera: Boolean = false
    private var cameraDirect: Boolean = false


    fun requestCode(requestCode: Int) = apply { this.requestCode = requestCode }
    fun limit(limit: Int) = apply { this.limit = limit }
    fun folderPosition(folderPosition: Int) = apply { this.folderPosition = folderPosition }
    fun itemPosition(itemPosition: Int) = apply { this.itemPosition = itemPosition }
    fun disableCamera(disableCamera: Boolean) = apply { this.disableCamera = disableCamera }
    fun cameraDirect(cameraDirect: Boolean) = apply { this.cameraDirect = cameraDirect }
    fun withActivity(activity: Activity) = apply { this.withActivity = activity }
    fun withFragment(fragment: Fragment) = apply { this.withFragment = fragment }


    fun show() {
        if (withActivity == null && withFragment == null) {
            throw IllegalStateException("Activity or fragment should be passed, use withActivity(activity) or withFragment(fragment) to set any.")
        }

        val intent = Intent()
        intent.putExtra(EXTRA_LIMIT, limit)
        intent.putExtra(EXTRA_CAMERA_DIRECT, cameraDirect)
        intent.putExtra(EXTRA_FOLDER_POSITION, folderPosition)
        intent.putExtra(EXTRA_ITEM_POSITION, itemPosition)

        if (!cameraDirect) {
            intent.putExtra(EXTRA_DISABLE_CAMERA, disableCamera)
        }

        if (withActivity != null) {
            startActivityForResult(withActivity!!, requestCode, intent)
        } else {
            startActivityForResult(withFragment!!, requestCode, intent)
        }
    }
}
