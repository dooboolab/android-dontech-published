package org.hyochan.dontech.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hyochan.dontech.R;
import org.hyochan.dontech.utils.MyLog;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MapsActivity";

    private Activity activity;
    private Context context;

    @BindView(R.id.rel_back)
    RelativeLayout relBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.rel_check)
    RelativeLayout relCheck;

    private GoogleMap map;

    private Location myLocation;
    private LatLng myLatLng;

    @OnClick({R.id.rel_back, R.id.rel_check}) void onClick(View v){
            switch (v.getId()){
                case R.id.rel_back:
                    onBackPressed();
                    break;
                case R.id.rel_check:
                    if(myLocation != null) {
                        Intent intent = new Intent();
                        intent.putExtra("lon", myLocation.getLongitude());
                        intent.putExtra("lat", myLocation.getLatitude());
                        // 주소 가져오기
                        try {
                            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = geo.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                            if (addresses.size() > 0) {
                                intent.putExtra("address", addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare() + " " + addresses.get(0).getFeatureName() + ", " + addresses.get(0).getAdminArea());
                                //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e) {
                            MyLog.e(TAG, e.getMessage()); // getFromLocation() may sometimes fail
                        }
                        setResult(Activity.RESULT_OK, intent);
                    }
                    else setResult(Activity.RESULT_CANCELED);

                    onBackPressed();
                    break;
            }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        // 롤리팝 이상이면 status bar 색상 변경
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            this.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSubPrimary));
        }

        activity = this;
        context = this;

        txtTitle.setText(getString(R.string.save_location));

        myLocation = new Location("Location");
        myLocation .setTime(new java.util.Date().getTime());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(map != null)
            map.setMyLocationEnabled(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if(map != null)
                map.setMyLocationEnabled(false);
        } catch (SecurityException se){
            MyLog.d(TAG, se.getMessage());
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
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        //현재 위치로 가는 버튼 표시
        map.setMyLocationEnabled(true);
        myLatLng = new LatLng(getIntent().getDoubleExtra("lat", 0.0),getIntent().getDoubleExtra("lon", 0.0));
        if(myLatLng.latitude != 0.0){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatLng.latitude, myLatLng.longitude), 17));
            map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
            myLocation .setLatitude(myLatLng.latitude);
            myLocation .setLongitude(myLatLng.longitude);

        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
        drawMarker(myLocation);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                myLocation .setLatitude(cameraPosition.target.latitude);
                myLocation .setLongitude(cameraPosition.target.longitude);
                drawMarker(myLocation);
            }
        });

    }

    private void drawMarker(Location location) {
        //기존 마커 지우기
        map.clear();
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = location;

        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(myLatLng)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Chosen position")
                .draggable(true));
    }
}
