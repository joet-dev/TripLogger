package com.example.csc202assignment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File
import android.graphics.Bitmap
import android.view.View

private const val ARG_IMAGE_FILE = "imageFile"

/**
 *  ImageDialogFragment is used to allow the user to open the image thumbnail in a dialog fragment.
 *
 *  @author Joseph Thurlow
 */
class ImageDialogFragment : DialogFragment() {

    private lateinit var imageView: ImageView
    private lateinit var imageFile: File

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        imageFile = arguments?.getSerializable(ARG_IMAGE_FILE) as File

        val view = inflater.inflate(R.layout.dialog_image, container, false)
        imageView = view.findViewById(R.id.image_view_dialog)

        if (!imageFile.exists()) {
            imageView.setImageDrawable(null)
        } else {
            val bitmap: Bitmap = getScaledBitmap(imageFile.path, requireActivity())
            imageView.setImageBitmap(bitmap)
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    companion object {
        fun newInstance(imageFile: File): ImageDialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_IMAGE_FILE, imageFile)
            }
            return ImageDialogFragment().apply {
                arguments = args
            }
        }
    }


}