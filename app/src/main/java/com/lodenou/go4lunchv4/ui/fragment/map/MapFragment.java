package com.lodenou.go4lunchv4.ui.fragment.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lodenou.go4lunchv4.BuildConfig;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.data.UserCallData;
import com.lodenou.go4lunchv4.model.SelectedRestaurant;
import com.lodenou.go4lunchv4.model.User;
import com.lodenou.go4lunchv4.model.nearbysearch.Result;
import com.lodenou.go4lunchv4.ui.activities.DetailActivity;
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
        googleMap.clear();
        mViewModelMap.getRestaurantChosenId().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                for (int i = 0; i <= resultSize - 1; i++) {
                    Double lng = results.get(i).getGeometry().getLocation().getLng();
                    Double lat = results.get(i).getGeometry().getLocation().getLat();
                    LatLng currentLatLong = new LatLng(lat, lng);
                    String markerTitle = results.get(i).getName();
                    Log.d("123", "" + results.get(i).getPlaceId());
                    if (strings.contains(results.get(i).getPlaceId())) {
                        Marker greenMarker = googleMap.addMarker(
                                new MarkerOptions()
                                        .position(currentLatLong)
                                        .title(markerTitle)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerGreen))
                        );


                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                for (int k = 0; k <= results.size() - 1; k++) {
                                    if (Objects.equals(marker.getTitle(), results.get(k).getName())) {
                                        startDetailActivity(results.get(k).getPlaceId());
                                    }
                                }
                                return false;
                            }
                        });
                    }

                    // Else add orange markers for all others restaurants
                    else if (!strings.contains(results.get(i).getPlaceId())) {
                        googleMap.addMarker(
                                new MarkerOptions()
                                        .position(currentLatLong)
                                        .title(markerTitle)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerOrange))
                        );
                    }
                }
            }
        });
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

        // refresh the map and readd markers
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
}