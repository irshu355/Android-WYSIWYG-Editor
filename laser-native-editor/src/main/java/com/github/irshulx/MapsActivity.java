package com.github.irshulx;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.github.irshulx.R;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private LatLng SelectedLatLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

    //    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpMapIfNeeded();
        buildGoogleApiClient();
        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMap();
            }
        });
    }
    private void insertMap() {
        if(SelectedLatLng!=null) {
            Intent returnIntent = new Intent();
            String result= String.valueOf(SelectedLatLng.latitude)+","+String.valueOf(SelectedLatLng.longitude);
            returnIntent.putExtra("cords",  result);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }
    protected synchronized void buildGoogleApiClient() {
        PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // Handle the selected Place
                Context context = getApplicationContext();
                CharSequence text = place.getName();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, "Location,"+text+" selected", duration);
                toast.show();
               SelectedLatLng = place.getLatLng();
                mMap.setTrafficEnabled(true);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(SelectedLatLng)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



                mMap.addMarker(new MarkerOptions()
                        .position(SelectedLatLng)
                        .title(text.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
            }

            @Override
            public void onError(Status status) {
                // Handle the error
            }
        });

    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                }
            });
            // Check if we were successful in obtaining the map.
            //  if (mMap != null) {
            //   setUpMap();
            //  }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public  class MapHelper
    {


        public  Location getLocation(Location location, int radius) {
            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double x0 = location.getLongitude() * 1E6;
            double y0 = location.getLatitude() * 1E6;
            double u = random.nextInt(1001) / 1000;
            double v = random.nextInt(1001) / 1000;
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            // Set the adjusted location
            Location newLocation = new Location("Loc in radius");
            newLocation.setLongitude(new_x + x0);
            newLocation.setLatitude(y + y0);

            return newLocation;
        }
    }
}
