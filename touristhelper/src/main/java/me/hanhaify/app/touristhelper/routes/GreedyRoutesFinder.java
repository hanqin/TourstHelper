package me.hanhaify.app.touristhelper.routes;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GreedyRoutesFinder implements RoutesFinder {

    @Override
    public List<LatLng> findRoute(Set<LatLng> allLocations, LatLng startLocation) {
        List<LatLng> route = new ArrayList<LatLng>();

        LatLng start = startLocation;
        List<LatLng> positions = new ArrayList<LatLng>();
        positions.addAll(allLocations);

        while (!positions.isEmpty()) {
            LatLng lastPosition = findNearestPosition(start, positions);
            positions.remove(lastPosition);
            start = lastPosition;
            route.add(lastPosition);
        }
        route.add(startLocation);
        return route;
    }

    private LatLng findNearestPosition(LatLng start, List<LatLng> positions) {
        float distance = Float.MAX_VALUE;
        LatLng lastPosition = null;
        for (LatLng latLng : positions) {
            float[] results = new float[3];
            Location.distanceBetween(start.latitude, start.longitude, latLng.latitude, latLng.longitude, results);
            if (distance > results[0]) {
                distance = results[0];
                lastPosition = latLng;
            }
        }
        return lastPosition;
    }
}
