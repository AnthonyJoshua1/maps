package app.o3interfaces.maps;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener,GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private GoogleApiClient client;
    private Location lastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Marker currentLocationMarker;
    Dialog s;
    public static final int PERMISSION_REQUEST_CODE = 99;
    public boolean ispopUpLoaded = false;
    public boolean isGoPressed = false;
    int i = 0;
    LatLng latLng;
    boolean isMarkerRotating = false;
    RotatingLinearLayout rotatingLinearLayout;
    Location myLocation;
    SupportMapFragment mapFragment;
    TextView loading;
    CameraUpdate cu;
    FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //rotatingLinearLayout=findViewById(R.id.rotatingview);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        final Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               move(button);
            }
        });


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
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnMapLoadedCallback(this);

        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);


            if (lm != null) {
                myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (lm == null) {
                Log.d("MapsActivity", "" + lm);
            }
            if (myLocation == null) {
                criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = null;
                if (lm != null) {
                    provider = lm.getBestProvider(criteria, true);
                }
                if (lm != null) {
                    myLocation = lm.getLastKnownLocation(provider);
                }
            }
            if (myLocation != null) {

            }


        }
        // for ActivityCompat#requestPermissions for more details.


        //LatLng latLng = new LatLng(mMap.getMyLocation().getLatitude(),32.223);
        // We will provide our own zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            try {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this, R.raw.style));
                latLng = new LatLng(33.693124, 73.059133);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                if (!success) {
                    // Handle map style load failure
                }
            } catch (Resources.NotFoundException e) {
                // Oops, looks like the map style resource couldn't be found!
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        // onMapLoaded()
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(14.4f),Math.max(2000,1),null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void animateLatLngZoom(LatLng latlng, int reqZoom, int offsetX, int offsetY) {

        // Save current zoom
        float originalZoom = mMap.getCameraPosition().zoom;

        // Move temporarily camera zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(reqZoom));

        Point pointInScreen = mMap.getProjection().toScreenLocation(latlng);

        Point newPoint = new Point();
        newPoint.x = pointInScreen.x + offsetX;
        newPoint.y = pointInScreen.y + offsetY;

        LatLng newCenterLatLng = mMap.getProjection().fromScreenLocation(newPoint);

        // Restore original zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom));


        // Animate a camera with new latlng center and required zoom.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, reqZoom));

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        myLocation = location;

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();

        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        animateLatLngZoom(latLng, 17, 10, 100);
        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
            return false;

        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void animateMyCamera(final CameraPosition position, final int j) {
        final int speed = 4;

        ValueAnimator va = ValueAnimator.ofFloat(1f, 2f);
        int mDuration = 2000; //in millis
        va.setDuration(mDuration);
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(position).bearing(mMap.getCameraPosition().bearing+(float)animation.getAnimatedValue()+1).build()),speed,null);
                Log.d("AnimatedValue",""+animation.getAnimatedValue());
            if (((float) animation.getAnimatedValue())==2){
                mMap.animateCamera(CameraUpdateFactory.zoomOut(), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        Button Next=findViewById(R.id.btn1);
                        Next.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);
                        root.setBackgroundColor(getResources().getColor(R.color.transparent));
                        root.setAlpha(1f);
                        // root.setBackgroundColor(getResources().getColor(Andrroi));
                    }

                    @Override
                    public void onCancel() {

                    }
                });


            }

        }
        });

        va.start();


    }   // end of animateMyCamera

    @Override
    public void onMapLoaded() {
        Toast.makeText(MapsActivity.this, "map loaded", Toast.LENGTH_SHORT).show();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.4f), Math.max(3000, 1),null);

        if (!ispopUpLoaded) {
            ispopUpLoaded = true;

            final LinearLayout layout = findViewById(R.id.Dialog);
            layout.setVisibility(View.VISIBLE);
            Button go = findViewById(R.id.btnGo);
            root = findViewById(R.id.root);
            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // s.dismiss();
                    isGoPressed = true;
                    layout.setVisibility(View.GONE);
                    //root.setBackgroundColor(getResources().getColor(R.color.black));
                   // root.setAlpha(0.3f);
                    //Interpolator interpolator=new LinearInterpolator();
                    //final int duration=(int) interpolator.getInterpolation(3000);
                    loading = findViewById(R.id.btn2);

                    loading.setVisibility(View.VISIBLE);

                    final CameraPosition position = new CameraPosition.Builder()        // set bearing 120 (1st animation + fall down + tilt)
                            .bearing(0)
                            .zoom(18f)
                            .target(latLng)
                            .tilt(65)
                            .build();

                    final CameraUpdate cu = CameraUpdateFactory.newCameraPosition(position);
                    mMap.animateCamera(cu,2000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            animateMyCamera(position, 1);

                                }

                        @Override
                        public void onCancel() {

                        }
                    });


                }
            });
        }
    }

    public static void move(final Button view){
        ValueAnimator va = ValueAnimator.ofFloat(0f, 70f);
        int mDuration = 3000; //in millis
        va.setDuration(mDuration);
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((float)animation.getAnimatedValue());
                view.setRotation((float)animation.getAnimatedValue());
            }
        });
        va.setRepeatCount(5);
        va.start();
    }



    @Override
    public void onCameraIdle() {
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14.4f),Math.max(2000,1),null);
        //Toast.makeText(MapsActivity.this,"cam idle",Toast.LENGTH_SHORT).show();
//        if (isGoPressed) {
//            CameraPosition position = new CameraPosition.Builder()
//                    .bearing(mMap.getCameraPosition().bearing+120)
//                    .zoom(19f)
//                    .target(latLng)
//                    .tilt(65)
//                    .build();
//            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(position);
//            mMap.animateCamera(cu,Math.max(3000,1),null);
//            Toast.makeText(MapsActivity.this,"bearing"+mMap.getCameraPosition().bearing+"and loop is"+i,Toast.LENGTH_SHORT).show();
//
//            i=i+1;
//            if (i==4){
//                Toast.makeText(this, "i is "+i, Toast.LENGTH_SHORT).show();
//                isGoPressed=!isGoPressed;
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position.target,15));
//            }
//
//        }
        // mMap.moveCamera(cu);


    }

    @Override
    public void onCameraMoveStarted(int i) {

    }


    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

//    private void animateTo(double lat, double lon, double zoom, double bearing, double tilt, final int milliseconds) {
//
//        if (mMapInstance==null) return;
//        mMapInstance.setMapType(paramMapMode);
//        mCurrentPosition=new LatLng(lat,lon);
//
//        // animate camera jumps too much
//        // so we set the camera instantly to the next point
//
//        mMapInstance.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mCurrentPosition,(float)zoom, (float)tilt, (float)bearing)));
//
//        // give Android a break so it can load tiles. If I start the animation
//        // without pause, no tile loading is done
//
//        mMap.postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                // keeping numbers small you get a nice scrolling effect
//                mMapInstance.animateCamera(CameraUpdateFactory.scrollBy(250-(float)Math.random()*500-250, 250-(float)Math.random()*500),milliseconds,null);
//
//            }},500);
//
//    }
}