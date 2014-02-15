package me.hanhaify.app.touristhelper.routes;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Set;

public interface RoutesFinder {
    List<LatLng> findRoute(Set<LatLng> allLocations, LatLng startLocation);
}
