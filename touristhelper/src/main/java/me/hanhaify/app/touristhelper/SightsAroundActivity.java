package me.hanhaify.app.touristhelper;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import it.sephiroth.android.library.widget.HListView;

public class SightsAroundActivity extends FragmentActivity {

    public static final int DEFAULT_ZOOM_LEVEL = 16;
    private GoogleMap googleMap;
    private LocationClient locationClient;
    private HListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sights_around);

        initGoogleMap();
        initLocationClient();
        initListView();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.disconnect();
    }

    private void initListView() {
        listView = (HListView) findViewById(R.id.horizontal_list_view);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setImageDrawable(parent.getResources().getDrawable(R.drawable.demo_image));
                imageView.setLayoutParams(new HListView.LayoutParams(HListView.LayoutParams.WRAP_CONTENT, HListView.LayoutParams.WRAP_CONTENT));
                return imageView;
            }
        });
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
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
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(target, DEFAULT_ZOOM_LEVEL);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
