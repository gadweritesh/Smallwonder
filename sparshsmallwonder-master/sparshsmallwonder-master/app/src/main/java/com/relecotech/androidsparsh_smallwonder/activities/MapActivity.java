package com.relecotech.androidsparsh_smallwonder.activities;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh_smallwonder.R;
import com.relecotech.androidsparsh_smallwonder.utils.Singleton;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String busIdToFetchData;
    private String busNo;
    private GoogleMap mMap;
    private Handler someHandler;
    private Runnable myRunnable;
    private MobileServiceClient mClient;
    private ArrayList<LatLng> points;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mClient = Singleton.Instance().mClientMethod(this);

        points = new ArrayList<LatLng>();

        Intent intent = getIntent();
        busIdToFetchData = intent.getStringExtra("BusId");
        busNo = intent.getStringExtra("BusNo");
        System.out.println(" busIdToFetchData" + busIdToFetchData);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getTrackingData() {

        JsonObject trackingDataItem = new JsonObject();
        trackingDataItem.addProperty("BusId", busIdToFetchData);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = mClient.invokeApi("busFetchTrackingData", trackingDataItem);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" addTrackingData exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" addTrackingData  response    " + response);
                parseJSONAndPopulate(response);
            }
        });
    }

    private void parseJSONAndPopulate(JsonElement busTrackerResponse) {

//        try {
        JsonArray getJsonTrackResponse = busTrackerResponse.getAsJsonArray();
        if (getJsonTrackResponse.size() != 0) {
            System.out.println("getJsonListResponse.size()" + getJsonTrackResponse.size());
            for (int loop = 0; loop < getJsonTrackResponse.size(); loop++) {

                JsonObject jsonObjectForIteration = getJsonTrackResponse.get(loop).getAsJsonObject();

//                    date_Time = jsonObjectForIteration.get("dateTime").toString().replace("\"", "");
                String bus_lati = jsonObjectForIteration.get("latitude").toString().replace("\"", "");
                String bus_longi = jsonObjectForIteration.get("longitude").toString().replace("\"", "");
//                    busId = jsonObjectForIteration.get("bus_id").toString().replace("\"", "");
                latLng = new LatLng(Double.parseDouble(bus_lati), Double.parseDouble(bus_longi));
//                    direction = jsonObjectForIteration.get("direction").toString().replace("\"", "");
//
//                points.add(loop, latLng);
                addMarker(latLng);
//                redrawLine();
            }
        }
//        } catch (Exception e) {
//            System.out.println(" getJsonTrackResponse exception " + e.getMessage());
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        System.out.println("INSIDE onMapReady ");

//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = mMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.style_map));
//
//            if (!success) {
//                System.out.println("Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            System.out.println("Can't find style. Error: " + e);
//        }


//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


//        drawMarker();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        mMap.getCameraPosition();
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

//        new GetDataFromServer().execute();
        System.out.println("INSIDE MAP READY");
    }

    private void addMarker(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(busNo).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_red_500_48dp)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f));

    }

    private void redrawLine() {

        points.add(0, new LatLng(21.131195, 79.098921));
        points.add(1, new LatLng(21.130852, 79.100276));
        points.add(2, new LatLng(21.130624, 79.101258));
        points.add(3, new LatLng(21.129673, 79.104933));
        points.add(4, new LatLng(21.129913, 79.106312));
        points.add(5, new LatLng(21.132475, 79.107701));

        mMap.clear();  //clears all Markers and Polyline

        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.GRAY).geodesic(true);
        for (int i = 0; i < points.size(); i++) {

            System.out.println(" point " + points.get(i));
            LatLng point = points.get(i);
            polylineOptions.add(point);
        }
//        polylineOptions.addAll(points);

        System.out.println("polylineOptions.getPoints() " + polylineOptions.getPoints());

        mMap.addPolyline(polylineOptions); //add Polyline
        addMarker(latLng); //add Marker in current position
    }

    @Override
    protected void onResume() {
        super.onResume();
        someHandler = new Handler(getMainLooper());
        myRunnable = new Runnable() {
            @Override
            public void run() {
                getTrackingData();
                someHandler.postDelayed(this, 10000);
            }
        };
        someHandler.postDelayed(myRunnable, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        someHandler.removeCallbacks(myRunnable);
    }
}