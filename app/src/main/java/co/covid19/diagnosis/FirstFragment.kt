package co.covid19.diagnosis

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.covid19.diagnosis.databinding.FragmentFirstBinding
import java.io.IOException


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_first,
            container,
            false
        )

        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//            TedImagePicker.with(activity!!).start { uri -> showSingleImage(uri) }
        }

        return binding.root
    }

    private fun showSingleImage(imageUri: Uri) {
        binding.ivImage.visibility = View.VISIBLE
//        Glide.with(activity!!).asBitmap().load(imageUri).into(binding.ivImage)

//        val futureTarget: FutureTarget<Bitmap> = Glide.with(context!!)
//            .asBitmap()
//            .load(imageUri)
//            .submit()
////            .submit(400, 600)
//
//        var bitmap: Bitmap? = null
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                bitmap = futureTarget.get()
//                bitmap
//            }
//        }
//
//        val bitmap2: LiveData<Bitmap> = futureTarget.get()
//
//        binding.ivImage.setImageBitmap(bitmap)

//        val bitmap = Glide.with(activity!!)
//            .asBitmap()
//            .load(imageUri)
//            .centerCrop()
//            .into(400,400)
//            .get()


        var bitmap: Bitmap? = null
//        var module: Module? = null
        try {
            // creating bitmap from packaged into app android asset 'image.jpg',
            // app/src/main/assets/image.jpg
//            bitmap = BitmapFactory.decodeStream(getAssets().open("normal_original_IM-0466-0001.jpeg"));
//            bitmap = BitmapFactory.decodeStream(activity!!.assets.open(uri.toString()))
            bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imageUri)
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
//            module = Module.load(MainActivity.assetFilePath(this, "chk_resnet_50_epoch_14.pt"))
        } catch (e: IOException) {
            Log.e("FirstFragment", "Error reading assets", e)
        }

        binding.ivImage.setImageBitmap(bitmap)

//        Log.d("FirstFragment", "bitmap ${bitmap.toString()}")
    }
}
