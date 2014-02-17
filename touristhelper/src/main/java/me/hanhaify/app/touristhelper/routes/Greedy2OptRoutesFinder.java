package me.hanhaify.app.touristhelper.routes;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Greedy2OptRoutesFinder implements RoutesFinder {

    @Override
    public List<LatLng> findRoute(Set<LatLng> allLocations, LatLng startLocation) {
        long start = System.currentTimeMillis();
        List<LatLng> existingRoute = new GreedyRoutesFinder().findRoute(allLocations, startLocation);
//        preCalculateTotalDistance(existingRoute);

        long totalCalculateTime = 0;
        long totalSwapTime = 0;
        boolean improved;
        do {
            improved = false;
            long beginAt = System.currentTimeMillis();
            float existingDistance = calculateTotalDistance(existingRoute);
            totalCalculateTime += (System.currentTimeMillis() - beginAt);

            for (int i = 1; i < existingRoute.size() - 1; i++) {
                for (int j = i + 1; j < existingRoute.size() - 1; j++) {

                    long l = System.currentTimeMillis();
                    List<LatLng> newRoute = swap2Opt(existingRoute, i, j);
                    totalSwapTime += (System.currentTimeMillis() - l);

                    long l1 = System.currentTimeMillis();
                    float newDistance = calculateTotalDistance(newRoute);
                    totalCalculateTime += (System.currentTimeMillis() - l1);

                    if (newDistance < existingDistance) {
                        existingRoute = newRoute;
                        improved = true;
                    }
                }
            }
        } while (improved);

        System.out.println("Total time: " + (System.currentTimeMillis() - start));
        System.out.println("Total swap: " + (totalSwapTime));
        System.out.println("Total calc: " + (totalCalculateTime));
        return existingRoute;
    }

    private void preCalculateTotalDistance(List<LatLng> route) {

        for (int i = 0; i < route.size() - 1; i++) {
            LatLng firstLocation = route.get(i);
            LatLng secondLocation = route.get(i + 1);
            float[] result = new float[3];
            Location.distanceBetween(firstLocation.latitude, firstLocation.longitude, secondLocation.latitude, secondLocation.longitude, result);

        }
    }

    private List<LatLng> swap2Opt(List<LatLng> existingRoute, int firstNode, int secondNode) {
        List<LatLng> newRoute = new ArrayList<LatLng>();
        for (int i = 0; i < firstNode; i++) {
            newRoute.add(existingRoute.get(i));
        }
        for (int i = secondNode; i >= firstNode; i--) {
            newRoute.add(existingRoute.get(i));
        }
        for (int i = secondNode + 1; i < existingRoute.size(); i++) {
            newRoute.add(existingRoute.get(i));
        }
        return newRoute;
    }

    private float calculateTotalDistance(List<LatLng> route) {
        float sum = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            LatLng firstLocation = route.get(i);
            LatLng secondLocation = route.get(i + 1);

            float[] result = new float[3];
            Location.distanceBetween(firstLocation.latitude, firstLocation.longitude, secondLocation.latitude, secondLocation.longitude, result);

            sum += result[0];
        }
        return sum;
    }
}
