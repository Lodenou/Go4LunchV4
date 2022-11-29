package com.lodenou.go4lunchv4.ui.fragment.map;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.fragment.listview.ViewModelListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng currentLatLng;
    ViewModelMap mViewModelMap;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
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

//        if ()
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

    //TODO MAKE THE GREEN MARKERS WHEN USER CHOSE A RESTAURANT
    private void createRestaurantsMarkers(List<Result> results, GoogleMap googleMap) {
        googleMap.clear();
        int resultSize = results.size();
        final int height = 100;
        final int width = 70;

        // Resize the orange icon
        @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapDrawOrange = (BitmapDrawable) getResources()
                .getDrawable(R.drawable
                        .ic_marker_orange, getActivity().getTheme());
        Bitmap bOrange = bitmapDrawOrange.getBitmap();
        Bitmap smallMarkerOrange = Bitmap.createScaledBitmap(bOrange, width, height, false);

        // Resize the green icon
        @SuppressLint("UseCompatLoadingForDrawables") BitmapDrawable bitmapDrawGreen = (BitmapDrawable) getResources()
                .getDrawable(R.drawable
                        .ic_marker_green, getActivity().getTheme());
        Bitmap bGreen = bitmapDrawGreen.getBitmap();
        Bitmap smallMarkerGreen = Bitmap.createScaledBitmap(bGreen, width, height, false);

        for (int i = 0; i <= resultSize - 1; i++) {
            Double lng = results.get(i).getGeometry().getLocation().getLng();
            Double lat = results.get(i).getGeometry().getLocation().getLat();
            LatLng currentLatLong = new LatLng(lat, lng);
            String markerTitle = results.get(i).getName();

            // Add green marker if it's restaurant place id is different from the
            // SelectedRestaurant database
            if (Objects.equals(results.get(i).getPlaceId(),
                    getSelectedRestaurantInfo(i).getIdRestaurant())) {
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(currentLatLong)
                                .title(markerTitle)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerGreen))
                );
            }
            // Else add orange markers for all others restaurants
            else {
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(currentLatLong)
                                .title(markerTitle)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerOrange))
                );
            }
        }
    }

    private SelectedRestaurant getSelectedRestaurantInfo(int i) {
        final SelectedRestaurant[] mSelectedRestaurant = {new SelectedRestaurant()};
        // SelectedRestaurant mSelectedRestaurant = new SelectedRestaurant();

        // Logic here should be the same as initViewModel but for a method
        // mViewModelMap.getRestaurantRepository()
        mViewModelMap.getSelectedRestaurantInfo().observe(this, new Observer<List<SelectedRestaurant>>() {
            @Override
            public void onChanged(List<SelectedRestaurant> selectedRestaurants) {
                mSelectedRestaurant[0] = selectedRestaurants.get(i);
            }
        });

        //FIXME This should not be update because of the outscope ==> outside the onChanged()
        return mSelectedRestaurant[0];
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
}