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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
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
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.activities.DetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener {

    private GoogleMap mMap;
    LatLng currentLatLng;
    ViewModelMap mViewModelMap;
    private List<Marker> markers = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapPermission();
        setHasOptionsMenu(true);
        MapFragment mapFragment = null;
        if (getFragmentManager() != null) {
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        }
        if (mapFragment != null) {
            mMap = mapFragment.getMap();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);


    }

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        //PERMISSION
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        mMap.setMyLocationEnabled(true);
        Task<Location> task = getFusedLocation().getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @SuppressLint("CheckResult")
            @Override
            public void onSuccess(Location location) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    currentLatLng = new LatLng(lat, lng);
                    String loc = lat + "," + lng;
                    initViewModel(loc, googleMap);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));


                } else {
                    onMapReady(mMap);
                }
            }
        });


    }


    private void initViewModel(String location, GoogleMap googleMap) {
        mViewModelMap = new ViewModelProvider(this).get(ViewModelMap.class);
        mViewModelMap.init(location);
        mViewModelMap.getNearbyRestaurants().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                createRestaurantsMarkers(results, googleMap);

            }
        });
    }

    private void createRestaurantsMarkers(List<Result> results, GoogleMap googleMap) {
        googleMap.clear();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                for (int k = 0; k <= results.size() - 1; k++) {
                    if (Objects.equals(marker.getTitle(), results.get(k).getName())) {
                        startDetailActivity(results.get(k).getPlaceId());
                        return true;
                    }
                }
                return false;
            }
        });

        googleMap.clear();
        addAllMarkers();
    }

    private void startDetailActivity(String restaurantId) {

        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("idrestaurant", restaurantId);
        getContext().startActivity(intent);
    }

    private FusedLocationProviderClient getFusedLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient;
    }

    private void mapPermission() {
        Dexter.withContext(requireContext())
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("Dexter", "There was an error: " + error.toString());
                    }
                }).check();
    }

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

    @Override
    public void onResume() {
        super.onResume();

        // refresh the map and add markers
        if (mMap != null) {
            mMap.clear();

            mViewModelMap.getNearbyRestaurants().observe(this, new Observer<List<Result>>() {
                @Override
                public void onChanged(List<Result> results) {
                    initMarkers();
                    createRestaurantsMarkers(results, mMap);
                }
            });
        }
    }

    private Boolean getPermission() {

        Boolean isPermission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        Boolean isPermissionOk = !(isPermission);
        return isPermissionOk;
    }

    private Task getTask() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient.getLastLocation();
    }


    //SEARCHVIEW
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.searchview_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        ArrayList<Result> restaurantToFetch = new ArrayList<>();
        List<Result> resultList = mViewModelMap.getNearbyRestaurants().getValue();
        if (s.length() > 2) {
            for (int i = 0; i <= Objects.requireNonNull(mViewModelMap.getNearbyRestaurants().getValue()).size() - 1; i++) {
                String restaurantName = Objects.requireNonNull(resultList).get(i).getName();
                if (restaurantName.contains(s)) {
                    restaurantToFetch.add(resultList.get(i));
                }
                updateMapMarkers(s);
            }
        } else {
            addAllMarkers();
        }
        return true;
    }

    private void updateMapMarkers(String query) {
        // delete all markers
        GoogleMap map = getMap();
        map.clear();
        // add filtered markers
        addFilteredMarkers(query);
    }

    private void addFilteredMarkers(String searchResult) {
        List<Result> results = mViewModelMap.getNearbyRestaurants().getValue();

        for (int i = 0; i <= results.size() - 1; i++) {
            Double lng = results.get(i).getGeometry().getLocation().getLng();
            Double lat = results.get(i).getGeometry().getLocation().getLat();
            LatLng currentLatLong = new LatLng(lat, lng);
            // Resize the green icon
            final int height = 100;
            final int width = 70;
            @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapDrawGreen = (BitmapDrawable) getResources()
                    .getDrawable(R.drawable
                            .ic_marker_green, getActivity().getTheme());
            Bitmap bGreen = bitmapDrawGreen.getBitmap();
            Bitmap smallMarkerGreen = Bitmap.createScaledBitmap(bGreen, width, height, false);
            Result result = results.get(i);
            GoogleMap map = getMap();
            if (result.getName().contains(searchResult)) {
                map.addMarker(new MarkerOptions()
                        .position(currentLatLong)
                        .title(result.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerGreen))
                );
            }
        }
    }

    private void addAllMarkers() {
        List<Result> results = mViewModelMap.getNearbyRestaurants().getValue();


        mViewModelMap.getRestaurantChosenId().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                for (int i = 0; i <= results.size() - 1; i++) {
                    Double lng = results.get(i).getGeometry().getLocation().getLng();
                    Double lat = results.get(i).getGeometry().getLocation().getLat();
                    LatLng currentLatLong = new LatLng(lat, lng);
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
                    Result result = results.get(i);
                    GoogleMap map = getMap();
                    if (strings.contains(results.get(i).getPlaceId())) {
                        map.addMarker(
                                new MarkerOptions()
                                        .position(currentLatLong)
                                        .title(result.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerGreen))
                        );
                    }

                    // Else add orange markers for all others restaurants
                    else if (!strings.contains(results.get(i).getPlaceId())) {
                        map.addMarker(
                                new MarkerOptions()
                                        .position(currentLatLong)
                                        .title(result.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerOrange))
                        );
                    }
                }
            }
        });


    }
}