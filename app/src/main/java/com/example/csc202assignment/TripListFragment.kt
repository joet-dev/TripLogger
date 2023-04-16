package com.example.csc202assignment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "MMM dd, yyyy"

/**
 * TripListFragment is used to display the list of trips to the user.
 *
 * @author Joseph Thurlow
 */
class TripListFragment : Fragment() {

    // Gives the ability for tripListFragment to call function on its hosting activity.
    interface Callbacks {
        fun onTripSelected(tripId: UUID, newTrip: Boolean)
    }

    private var callbacks: Callbacks? = null

    private var numRecords: Int = 0

    private lateinit var tripRecyclerView: RecyclerView
    private var adapter: TripAdapter? = TripAdapter(emptyList())

    // TripListViewModel is initialized to the variable using the supplied lambda.
    private val tripListViewModel: TripListViewModel by lazy {
        ViewModelProvider(this).get(TripListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        // The fragment.onAttach(Context) lifecycle function is called when a fragment is attached
        // to an activity.
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Loads the fragment_trip_list.xml layout.
    // Finds the recycler view in the layout file.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        val view = inflater.inflate(R.layout.fragment_trip_list, container, false)

        tripRecyclerView = view.findViewById(R.id.trip_recycler_view) as RecyclerView
        // LayoutManager is required for RecyclerView to work.
        // The layout manager positions items and defines how scrolling works.
        tripRecyclerView.layoutManager = LinearLayoutManager(context)
        tripRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The observer function is used to register an observer on the LiveData instance
        // and tie the life of the observation to the life of another component.
        tripListViewModel.tripListLiveData.observe(
            viewLifecycleOwner,
            { //This code block is executed whenever the LiveData's list of trips is updated.
                trips -> trips?.let {
                    numRecords = trips.size
                    updateUI(trips)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_trip_list, menu)
    }

    // Defines the functions called when menu items are pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_trip -> {
                val trip = Trip()
                tripListViewModel.addTrip(trip)
                callbacks?.onTripSelected(trip.id, true)
                true
            }

            R.id.trip_reset -> {
                val builder = AlertDialog.Builder(view?.context)
                builder.setTitle("Delete Trips")
                builder.setMessage("Are you sure you want to delete all trip records?")

                builder.setPositiveButton(
                    "Yes") { _, _ ->
                    // Or other conditions that need to be met for reset.
                    if (numRecords > 0) {
                        tripListViewModel.deleteAllTrips()
                        Toast.makeText(activity?.applicationContext, "All past records have been cleared",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity?.applicationContext, "Reset Failed",Toast.LENGTH_SHORT).show()
                    }
                }

                builder.setNegativeButton("No") { _, _ -> }
                builder.show()
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(trips: List<Trip>) {
        adapter = TripAdapter(trips)
        tripRecyclerView.adapter = adapter
    }

    // ViewHolder that extends from RecyclerView.ViewHolder.
    // The recyclerView expects an item view to be wrapped in an instance of ViewHolder.
    // ViewHolder stores a reference to an item's view.
    private inner class TripHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var trip: Trip
        // Finds title, destination and date text views in itemView's hierarchy when an instance is created.
        private val titleTextView: TextView = itemView.findViewById(R.id.trip_title)
        private val destinationTextView: TextView = itemView.findViewById(R.id.trip_destination)
        private val dateTextView: TextView = itemView.findViewById(R.id.trip_date)

        init {
            itemView.setOnClickListener(this)
        }

        // Binds the trip holder to a trip.
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat(DATE_FORMAT)
        fun bind(trip: Trip) {
            this.trip = trip
            titleTextView.text = this.trip.title
            destinationTextView.text = this.trip.destination
            dateTextView.text = format.format(this.trip.date)
        }

        // Pressing a trip notifies the hosting activity via the Callbacks interface.
        override fun onClick(v: View?) {
            callbacks?.onTripSelected(trip.id, false)
        }
    }

    // TripAdapter is used to create viewHolders when requested as the recyclerView cannot create
    // them on its own. It also binds viewHolder's data from the model layer.
    private inner class TripAdapter(var trips: List<Trip>)
        : RecyclerView.Adapter<TripHolder>() {

        // Creates the view, wraps it in a view holder and returns the result.
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : TripHolder {
            val view = layoutInflater.inflate(R.layout.list_item_trip, parent, false)
            return TripHolder(view)
        }

        // Returns the size of trips
        override fun getItemCount() = trips.size

        // Populates the given holder with the trips from a certain position.
        override fun onBindViewHolder(holder: TripHolder, position: Int) {
            val trip = trips[position]

            val typedVal = TypedValue()
            val theme = context?.theme
            theme?.resolveAttribute(R.attr.listTripColor, typedVal, true)
            @ColorInt
            val listItemColor = typedVal.data

            if(position%2 == 0) {

                holder.itemView.setBackgroundColor(listItemColor)
            }
            holder.bind(trip)
        }
    }

    // Activities can call the newInstance function to get an instance of this fragment.
    companion object {
        fun newInstance(): TripListFragment {
            return TripListFragment()
        }
    }
}