package com.example.csc202assignment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.text.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_TRIP_ID = "trip_id"
private const val ARG_TRIP_NEW = "trip_new"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val ARG_REQUEST_DATE_CODE = "requestDate"
private const val ARG_REQUEST_TIME_CODE = "requestTime"
private const val RESULT_TIME_KEY = "resultTime"
private const val RESULT_DATE_KEY = "resultDate"
private const val REQUEST_PHOTO = 2
private const val DIALOG_IMAGE = "DialogImage"
private const val DATE_FORMAT = "d MMM yyyy 'at' hh:mma z"
private const val SIMPLE_DATE_FORMAT = "MMM dd, yyyy"

/**
 * TripFragment is used to display the details of the trip and allows the user to make changes
 * to or add and remove from the database through text fields and buttons.
 *
 * This fragment's view is used to display the title, destination, duration, image and date of the trip.
 * It also includes functionality buttons such as share or delete.
 *
 * @author Joseph Thurlow
 */
class TripFragment : Fragment(), FragmentResultListener {

    private lateinit var trip: Trip
    private lateinit var imageFile: File
    private lateinit var imageUri: Uri
    private lateinit var titleField: EditText
    private lateinit var destinationField: EditText
    private lateinit var durationField: EditText
    private lateinit var dateButton: Button
    private lateinit var imageButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var shareButton: Button
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private var newTrip: Boolean = false

    private val tripDetailViewModel: TripDetailViewModel by lazy {
        ViewModelProvider(this).get(TripDetailViewModel::class.java)
    }

    // Bundle to which the fragment saves and retrieves its state.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trip = Trip()
        val tripId: UUID = arguments?.getSerializable(ARG_TRIP_ID) as UUID
        newTrip = arguments?.getSerializable(ARG_TRIP_NEW) as Boolean

        tripDetailViewModel.loadTrip(tripId)

        setHasOptionsMenu(true)

        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> processActivityResult(REQUEST_PHOTO, result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Used to inflate fragment_trip.
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        // Include text fields in the view.
        titleField = view.findViewById(R.id.trip_title) as EditText
        destinationField = view.findViewById(R.id.trip_destination) as EditText
        durationField = view.findViewById(R.id.trip_duration) as EditText

        // Include buttons in the view.
        dateButton = view.findViewById(R.id.trip_date) as Button
        imageButton = view.findViewById(R.id.trip_image_button) as ImageButton
        imageView = view.findViewById(R.id.trip_image) as ImageView
        shareButton = view.findViewById(R.id.trip_share) as Button

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.setFragmentResultListener(ARG_REQUEST_DATE_CODE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(ARG_REQUEST_TIME_CODE, viewLifecycleOwner, this)

        // Observe tripLiveData and update the UI any time new data is published.
        tripDetailViewModel.tripLiveData.observe(
            viewLifecycleOwner,
            { trip ->
                trip?.let {
                    this.trip = trip
                    // Stashes the location of the photo file.
                    imageFile = tripDetailViewModel.getPhotoFile(trip)
                    // The uri points to where the file should be saved.
                    imageUri = FileProvider.getUriForFile(requireActivity(),
                        "com.example.android.csc202assignment.fileprovider",
                        imageFile)
                    updateUI()
                }
            }
        )
    }

    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when(requestCode) {
            ARG_REQUEST_TIME_CODE -> {
                trip.date = result.getSerializable(RESULT_TIME_KEY) as Date
                updateUI()
            }
            ARG_REQUEST_DATE_CODE -> {
                trip.date = result.getSerializable(RESULT_DATE_KEY) as Date
                TimePickerFragment.newInstance(trip.date).apply {
                    show(this@TripFragment.childFragmentManager, DIALOG_TIME)
                }
                updateUI()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onStart() {
        super.onStart()

        val titleWatcher = object: TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {
            }

            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int
            ) {
                trip.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val destinationWatcher = object: TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {
            }

            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int
            ) {
                trip.destination = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val durationWatcher = object: TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {
            }

            override fun onTextChanged(sequence: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int
            ) {
                trip.duration = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        // Checks whether the text has been changed.
        titleField.addTextChangedListener(titleWatcher)
        destinationField.addTextChangedListener(destinationWatcher)
        durationField.addTextChangedListener(durationWatcher)

        dateButton.setOnClickListener {

            DatePickerFragment.newInstance(trip.date).apply {
                show(this@TripFragment.childFragmentManager, DIALOG_DATE)
            }
        }

        shareButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getTripReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.trip_subject))

            }.also { intent ->
                startActivity(intent)
            }
        }

        imageButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                cameraResultLauncher.launch(captureImage)
            }
        }

        imageView.setOnClickListener {
            if (imageFile.exists()) {
                val dialog = ImageDialogFragment.newInstance(imageFile)
                dialog.show(childFragmentManager, DIALOG_IMAGE)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_trip, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (newTrip) {
            val deleteItem = menu.findItem(R.id.trip_delete)
            deleteItem.isEnabled = false
            deleteItem.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.trip_delete -> {
                tripDetailViewModel.deleteTrip(trip)
                activity?.onBackPressed()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun processActivityResult(requestCode: Int, result: ActivityResult) {
        when (requestCode) {
            REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updateImageView()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        tripDetailViewModel.saveTrip(trip)
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateUI() {
        titleField.setText(trip.title)
        destinationField.setText(trip.destination)
        durationField.setText(trip.duration)
        val format = SimpleDateFormat(DATE_FORMAT)
        dateButton.text = format.format(trip.date)
        updateImageView()
    }

    private fun updateImageView() {
        if (imageFile.exists()) {
            val bitmap = getScaledBitmap(imageFile.path, requireActivity())
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageDrawable(null)
        }
    }

    // Function to retrieve trip summary.
    @SuppressLint("SimpleDateFormat")
    private fun getTripReport(): String {
        val format = SimpleDateFormat(SIMPLE_DATE_FORMAT)

        return getString(R.string.trip_details,
            trip.title, trip.destination, trip.duration, format.format(trip.date))
    }

    companion object {
        fun newInstance(tripId: UUID, newTrip:Boolean): TripFragment {
            // Adds arguments to the bundle
            val args = Bundle().apply {
                putSerializable(ARG_TRIP_ID, tripId)
                putSerializable(ARG_TRIP_NEW, newTrip)
            }
            // Returns the fragment with bundled args
            return TripFragment().apply {
                arguments = args
            }
        }
    }
}