package com.lodenou.go4lunchv4.ui.fragment.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.Restaurant;
import com.lodenou.go4lunchv4.ui.Utils;
import com.lodenou.go4lunchv4.ui.activities.detail.DetailActivity;
import com.lodenou.go4lunchv4.ui.activities.main.ViewModelMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A Fragment for displaying a map and restaurant markers.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener, MultiplePermissionsListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment = null;
    LatLng currentLatLng;
    ViewModelMap mViewModelMap;
    private List<Marker> markers = new ArrayList<>();
    ViewModelMainActivity mViewModelMainActivity;
    List<Restaurant> restaurantList = new ArrayList<>();

    /**
     * Default constructor for the MapFragment.
     */
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLocationPermission();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * Gets the GoogleMap instance.
     *
     * @return The GoogleMap instance.
     */
    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Callback method invoked when the map is ready for use.
     *
     * @param googleMap The GoogleMap instance that is ready.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //PERMISSION
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            Task<Location> task = getFusedLocation().getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {

                @SuppressLint("CheckResult")
                @Override
                public void onSuccess(Location location) {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        currentLatLng = new LatLng(lat, lng);
                        String loc = lat + "," + lng;
                        initViewModel(loc);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                    } else {
                        // Restart onMapReady to get location
                        onMapReady(mMap);
                    }
                }
            });
        }

        // PERMISSION LOCATION

    /**
     * Checks for location permission and requests it if not granted.
     */
    private void checkLocationPermission() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Permission not granted, we ask the user
            Dexter.withActivity(getActivity())
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION ,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(this)
                    .check();
        } else {
            // Permission already granted, we can init the map
            initMap();
        }
    }


    /**
     * Called when permissions have been checked.
     *
     * @param multiplePermissionsReport The report containing the permission status.
     */
    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
        if (multiplePermissionsReport.areAllPermissionsGranted()){
            initMap();
        }
    }

    /**
     * Called when permission rationale should be shown.
     *
     * @param list            List of permission requests.
     * @param permissionToken Permission token.
     */
    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
    }

    /**
     * Initializes the map if location permission is granted.
     */
    private void initMap() {
        // Get the SupportMapFragment and register the fragment as a callback when the map is ready to be used
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
           mapFragment.getMapAsync(this);

        }
    }


    /**
     * Initializes the ViewModel and fetches restaurant data based on the current location.
     *
     * @param location   The current location in string format (latitude,longitude).
     */
    private void initViewModel(String location) {
        initMainViewModel();
        mViewModelMap = new ViewModelProvider(this).get(ViewModelMap.class);
        mViewModelMap.init(location);
        mViewModelMainActivity.fetchAllRestaurants(getTask(),getPermission(),getContext());
        observeRestaurants();
    }

    /**
     * Observes changes in the restaurant data and updates the map markers accordingly.
     */
    private void observeRestaurants(){
        mViewModelMainActivity.getAllRestaurants().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                initMarkers();
                createRestaurantsMarkers(restaurants, getMap());
                restaurantList = restaurants;
            }
        });
    }

    /**
     * Initializes the main ViewModel for the MainActivity.
     */
    private void initMainViewModel(){
        mViewModelMainActivity = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ViewModelMainActivity.class);
        mViewModelMainActivity.init();

    }

    /**
     * Clears the GoogleMap, sets marker click behavior, and adds all markers.
     *
     * @param restaurants The list of restaurants to create markers for.
     * @param googleMap   The GoogleMap instance.
     */
    private void createRestaurantsMarkers(List<Restaurant> restaurants, GoogleMap googleMap) {
        googleMap.clear();
        markersClickBehavior(restaurants,googleMap);
        googleMap.clear();
        addAllMarkers();
    }

    /**
     * Defines the click behavior for markers on the GoogleMap.
     *
     * @param restaurants The list of restaurants with markers.
     * @param googleMap   The GoogleMap instance.
     */
    private void markersClickBehavior(List<Restaurant> restaurants, GoogleMap googleMap){
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                for (int k = 0; k <= restaurants.size() - 1; k++) {
                    if (Objects.equals(marker.getTitle(), restaurants.get(k).getName())) {
                        startDetailActivity(restaurants.get(k).getPlaceId());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Starts the DetailActivity for a specific restaurant.
     *
     * @param restaurantId The unique ID of the restaurant.
     */
    private void startDetailActivity(String restaurantId) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("idrestaurant", restaurantId);
        getContext().startActivity(intent);
    }

    /**
     * Retrieves the FusedLocationProviderClient.
     *
     * @return The FusedLocationProviderClient instance.
     */
    private FusedLocationProviderClient getFusedLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient;
    }

    /**
     * Initializes the map markers.
     */
    private void initMarkers() {
        mViewModelMap.getLocation(getPermission(), getTask()).observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String loc = lat + "," + lng;
                currentLatLng = new LatLng(lat, lng);
                mViewModelMap.init(loc);
                // move back camera to the user position after refresh
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }


    /**
     * Resumes the fragment and refreshes the map and markers.
     */
    @Override
    public void onResume() {
        super.onResume();
        // refresh the map and add markers

        if (mMap != null) {
            if (mViewModelMap != null) {
                mMap.clear();
                mViewModelMainActivity.fetchAllRestaurants(getTask(), getPermission(), getContext());
            }
        }
    }


    /**
     * Checks if location permission is granted.
     *
     * @return True if location permission is granted, false otherwise.
     */
    private Boolean getPermission() {
        Boolean isPermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;

        Boolean isPermissionOk = !(isPermission);
        return isPermissionOk;
    }

    /**
     * Retrieves the last known location.
     *
     * @return The Task containing the last known location.
     */
    private Task getTask() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient.getLastLocation();
    }


    //SEARCHVIEW

    /**
     * Initializes the search view in the fragment's options menu.
     *
     * @param menu     The options menu.
     * @param inflater The menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.searchview_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnQueryTextListener(this);
    }

    /**
     * Called when the user submits a query in the search view.
     *
     * @param s The query text.
     * @return Always returns false.
     */
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    /**
     * Called when the user changes the text in the search view.
     *
     * @param s The new query text.
     * @return True if handled, false otherwise.
     */
    @Override
    public boolean onQueryTextChange(String s) {

        if (s.length() > 2) {
            for (int i = 0; i <= Objects.requireNonNull(restaurantList).size() - 1; i++) {
                String restaurantName = Objects.requireNonNull(restaurantList).get(i).getName().toLowerCase(Locale.ROOT);
                if (restaurantName.contains(s.toLowerCase(Locale.ROOT))) {

                    updateMapMarkers(s);
                }
            }
        } else {
            addAllMarkers();
        }
        return true;
    }

    /**
     * Updates the map markers based on a search query.
     *
     * @param query The search query.
     */
    private void updateMapMarkers(String query) {
        // delete all markers
        GoogleMap map = getMap();
        map.clear();
        // add filtered markers
        addFilteredMarkers(query);
    }

    /**
     * Adds filtered markers to the map based on a search result.
     *
     * @param searchResult The search result.
     */
    private void addFilteredMarkers(String searchResult) {
        List<Restaurant> restaurants = mViewModelMainActivity.getAllRestaurants().getValue();
        for (int i = 0; i <= restaurants.size() - 1; i++) {
            String latLngString = restaurants.get(i).getGeometry();
            LatLng currentLatLong = Utils.stringToLatLng(latLngString);
            // Resize the green icon
            final int height = 100;
            final int width = 70;
            @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapDrawGreen = (BitmapDrawable) getResources()
                    .getDrawable(R.drawable
                            .ic_marker_green, getActivity().getTheme());
            Bitmap bGreen = bitmapDrawGreen.getBitmap();
            Bitmap smallMarkerGreen = Bitmap.createScaledBitmap(bGreen, width, height, false);
            Restaurant restaurant = restaurants.get(i);
            GoogleMap map = getMap();
            if (restaurant.getName().toLowerCase(Locale.ROOT).contains(searchResult.toLowerCase(Locale.ROOT))) {
                map.addMarker(new MarkerOptions()
                        .position(currentLatLong)
                        .title(restaurant.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerGreen))
                );
            }
        }
    }

    /**
     * Adds all markers to the map.
     */
    private void addAllMarkers() {
        List<Restaurant> restaurants = mViewModelMainActivity.getAllRestaurants().getValue();
        mViewModelMap.getRestaurantChosenId().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                for (int i = 0; i <= restaurants.size() - 1; i++) {
                    String latLngString = restaurants.get(i).getGeometry();
                    LatLng currentLatLong = Utils.stringToLatLng(latLngString);
                    // Resize the green icon
                    final int height = 100;
                    final int width = 70;
                    // Resize the orange icon
                    @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapDrawOrange = (BitmapDrawable) getResources()
                            .getDrawable(R.drawable
                                    .ic_marker_orange, getActivity().getTheme());
                    Bitmap bOrange = bitmapDrawOrange.getBitmap();
                    Bitmap smallMarkerOrange = Bitmap.createScaledBitmap(bOrange, width, height, false);
                    @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapDrawGreen = (BitmapDrawable) getResources()
                            .getDrawable(R.drawable
                                    .ic_marker_green, getActivity().getTheme());
                    Bitmap bGreen = bitmapDrawGreen.getBitmap();
                    Bitmap smallMarkerGreen = Bitmap.createScaledBitmap(bGreen, width, height, false);
                    Restaurant restaurant = restaurants.get(i);
                    GoogleMap map = getMap();
                    if (strings.contains(restaurants.get(i).getPlaceId())) {
                        map.addMarker(
                                new MarkerOptions()
                                        .position(currentLatLong)
                                        .title(restaurant.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerGreen))
                        );
                    }

                    // Else add orange markers for all others restaurants
                    else if (!strings.contains(restaurants.get(i).getPlaceId())) {
                        map.addMarker(
                                new MarkerOptions()
                                        .position(currentLatLong)
                                        .title(restaurant.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerOrange))
                        );
                    }
                }
            }
        });
    }
}