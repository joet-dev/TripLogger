# Trip Logger 
*`Created 2021`*

## Description 
Android app used to log trips. Users can input a title, destination, duration, date, and include an image.

## Technical Details

![Model View Controller UML for List UI](https://user-images.githubusercontent.com/69287038/232345601-fb528efd-ed40-4cfc-b950-b3bcd713d7fd.png)

`The image above displays the model-view-controller (MVC) for the list user interface. The TripListFragment (TLF) contains some logic, the view that will be displayed to the user, and the events required by the user interface. The TLF inflates the fragment_trip_list.xml file and returns the view to the MainActivity to be hosted (see onCreateView). TLF also inflates menu items and defines the functions that are called in response to them being pressed (see onOptionsItemSelected). These functions utilise TripListViewModel which manages (through database queries) and stores UI related data. Two inner classes are used (TripHolder & TripAdapter) to create ViewHolders and wrap trip list items (stores a reference to an item’s view) in them for RecyclerView. The callbacks interface is used to delegate onClick events back to MainActivity through giving TripListFragment the ability to call a function on its hosting activity.`

![Model View Controller UML for Item UI](https://user-images.githubusercontent.com/69287038/232345650-0edc70e9-eef0-463b-9ff9-6b50d6bdebdd.png)

`The image above shows the MVC for the item UI. The TripFragment hosts all the elements or components for the fragment_trip.xml. This includes Buttons, EditText fields and an ImageView. Much like the TLF, the TripFragment inflates the xml file and returns a view for the MainActivity to host. OnStart of the fragment, all user inputs are setup with TextWatchers and OnClickListeners which update the database when any values are changed. The TripFragment uses an instance of TripDetailViewModel (TDVM) to populate the view with data found in the database. The Trip Fragment also observes TDVM’s tripLiveData so that the UI can be updated whenever new data is published.`

![Database Schema](https://user-images.githubusercontent.com/69287038/232346055-795664e1-ee47-423d-b3c7-0465180a551d.png)

`This schema represents the structure of the database and the tables in it. For each trip, the id, title, destination, and duration are stored as text while the date is stored as an Integer.`

![Database UML](https://user-images.githubusercontent.com/69287038/232346036-d5f3a267-64ce-483d-908a-dec2d4a9aaa3.png)

`The TripDatabase class is annotated as the database (represents the database in the app) while the Trip entity class (not displayed) is used to manage and generate the tables. The TripTypeConverter class is annotated as the type-converter for the database. It defines how to convert specific data types into formats the database can store and how to convert them back to their original type on retrieval. Finally, TripDao is annotated as the data access object which allows it to define the functions used for querying the database.`
