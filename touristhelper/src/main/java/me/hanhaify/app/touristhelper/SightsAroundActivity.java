package me.hanhaify.app.touristhelper;

import android.graphics.Color;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.wuman.oauth.samples.flickr.api.model.ContactsPhotos;
import com.wuman.oauth.samples.flickr.api.model.Photo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.hanhaify.app.touristhelper.routes.Greedy2OptRoutesFinder;
import me.hanhaify.app.touristhelper.routes.GreedyRoutesFinder;

public class SightsAroundActivity extends FragmentActivity {

    private Map<LatLng, Marker> markers = new HashMap<LatLng, Marker>();
    private GoogleMap googleMap;
    private LocationClient locationClient;
    private PhotosGalleryFragment photoGallery;
    private Polyline existingPolyline;
    private ClusterManager<DefaultClusterItem> clusterManager;

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
                    DefaultClusterItem clusterItem = new DefaultClusterItem(photo);
                    LatLng markerPosition = clusterItem.getPosition();
                    if (markers.containsKey(markerPosition)) continue;

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(markerPosition)
                            .title(getString(R.string.marker_title))
                            .snippet(getString(R.string.marker_info));
                    Marker marker = googleMap.addMarker(markerOptions);
                    clusterManager.addItem(clusterItem);
                    markers.put(markerPosition, marker);
                }
            }

            @Override
            public void onPhotosCleared() {
                googleMap.clear();
                markers.clear();
                clusterManager.clearItems();
            }
        });
        photoGallery.setOnPhotoSelectedListener(new PhotosGalleryFragment.OnPhotoSelectedListener() {
            @Override
            public void selectedPhoto(Photo photo) {
                LatLng markerPosition = new LatLng(photo.getLatitude(), photo.getLongitude());
                Marker marker = markers.get(markerPosition);

                marker.showInfoWindow();
            }
        });
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                List<LatLng> route;

                route = new Greedy2OptRoutesFinder().findRoute(markers.keySet(), marker.getPosition());

                if (existingPolyline != null) {
                    existingPolyline.remove();
                    existingPolyline = null;
                }
                existingPolyline = googleMap.addPolyline(new PolylineOptions()
                        .addAll(route)
                        .geodesic(true)
                        .color(Color.RED)
                        .width(5));
            }
        });

        clusterManager = new ClusterManager<DefaultClusterItem>(this, googleMap);
        googleMap.setOnCameraChangeListener(clusterManager);
    }

    public static class DefaultClusterItem implements ClusterItem {

        public Photo photo;

        public DefaultClusterItem(Photo photo) {
            this.photo = photo;
        }

        @Override
        public LatLng getPosition() {
            return new LatLng(photo.getLatitude(), photo.getLongitude());
        }
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
