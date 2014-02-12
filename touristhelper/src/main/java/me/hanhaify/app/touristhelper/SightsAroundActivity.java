package me.hanhaify.app.touristhelper;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;
import com.wuman.oauth.samples.flickr.api.model.Photo;

import java.util.List;

public class SightsAroundActivity extends FragmentActivity {

    private GoogleMap googleMap;
    private LocationClient locationClient;
    private PhotosGalleryFragment photoGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sights_around);

        initGoogleMap();
        initLocationClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGoogleMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLocationClient();
        locationClient.connect();
        initPhotoGallery();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.disconnect();
    }

    private void initPhotoGallery() {
        photoGallery = (PhotosGalleryFragment) getSupportFragmentManager().findFragmentById(R.id.photo_gallery);
        photoGallery.setOnPhotosLoadedListener(new PhotosGalleryFragment.OnPhotosChangedListener() {
            @Override
            public void onPhotosLoaded(ContactsPhotos contactsPhotos) {
                List<Photo> photoList = contactsPhotos.getPhotos().getPhotoList();
                for (Photo photo : photoList) {
                    LatLng markerPosition = new LatLng(photo.getLatitude(), photo.getLongitude());
                    MarkerOptions position = new MarkerOptions().position(markerPosition);
                    googleMap.addMarker(position);
                }
            }

            @Override
            public void onPhotosCleared() {
                googleMap.clear();
            }
        });
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void initLocationClient() {
        DefaultConnectionCallbacks connectionCallbacks = new DefaultConnectionCallbacks();
        locationClient = new LocationClient(this, connectionCallbacks, connectionCallbacks);
    }

    private class DefaultConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnected(Bundle bundle) {
            Location lastLocation = locationClient.getLastLocation();

            LatLng target = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(target, Constants.DEFAULT_ZOOM_LEVEL);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            photoGallery.startLoading(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        @Override
        public void onDisconnected() {
            longToast("Disconnected. Please re-connect.").show();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            longToast("Error: " + connectionResult.getErrorCode()).show();
        }
    }

    private Toast longToast(String message) {
        return Toast.makeText(SightsAroundActivity.this, message, Toast.LENGTH_SHORT);
    }
}
