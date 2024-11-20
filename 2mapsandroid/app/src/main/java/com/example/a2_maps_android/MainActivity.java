package com.example.a2_maps_android;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button go = findViewById(R.id.idGo);
        Button cari = findViewById(R.id.idCari);

        go.setOnClickListener(view -> {
            hideKeyboard(view);
            gotoLokasi();
        });

        cari.setOnClickListener(view -> {
            hideKeyboard(view);
            goCari();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        this.googleMap = gMap;
        LatLng location = new LatLng(-7.2819705, 112.795323);
        googleMap.addMarker(new MarkerOptions().position(location).title("ITS"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }

    private void gotoLokasi() {
        EditText lat = findViewById(R.id.idLokasiLat);
        EditText lng = findViewById(R.id.idLokasiLng);

        String latText = lat.getText().toString();
        String lngText = lng.getText().toString();

        if (latText.isEmpty() || lngText.isEmpty()) {
            Toast.makeText(this, "Please enter valid coordinates!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            double dbllat = Double.parseDouble(latText);
            double dbllng = Double.parseDouble(lngText);
            Toast.makeText(this, "Move to Lat: " + dbllat + " Long: " + dbllng, Toast.LENGTH_LONG).show();
            gotoPeta(dbllat, dbllng, 12); // Default zoom level
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void gotoPeta(double lat, double lng, float zoom) {
        if (googleMap != null) {
            LatLng newLocation = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(newLocation).title("Marker at " + lat + ", " + lng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoom));
        }
    }

    private void goCari() {
        EditText tempat = findViewById(R.id.idDaerah);
        String locationName = tempat.getText().toString();

        if (locationName.isEmpty()) {
            Toast.makeText(this, "Please enter a location name!", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String foundAddress = address.getAddressLine(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                Toast.makeText(this, "Found: " + foundAddress, Toast.LENGTH_LONG).show();
                gotoPeta(latitude, longitude, 15); // Closer zoom level for specific locations

                EditText lat = findViewById(R.id.idLokasiLat);
                EditText lng = findViewById(R.id.idLokasiLng);
                lat.setText(String.valueOf(latitude));
                lng.setText(String.valueOf(longitude));
            } else {
                Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error during geocoding!", Toast.LENGTH_SHORT).show();
        }
    }
}
