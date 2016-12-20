package app.devk.rideguide.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import app.devk.rideguide.R;
import app.devk.rideguide.utils.GeofenceService;


import static android.content.Context.LOCATION_SERVICE;
import static app.devk.rideguide.utils.Constants.ANDROID_BUILDING_ID;
import static app.devk.rideguide.utils.Constants.ANDROID_BUILDING_LATITUDE;
import static app.devk.rideguide.utils.Constants.ANDROID_BUILDING_LONGITUDE;
import static app.devk.rideguide.utils.Constants.ANDROID_BUILDING_RADIUS_METERS;
import static app.devk.rideguide.utils.Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static app.devk.rideguide.utils.Constants.GEOFENCE_EXPIRATION_TIME;
import static app.devk.rideguide.utils.Constants.TAG;
import static app.devk.rideguide.utils.Constants.YERBA_BUENA_ID;
import static app.devk.rideguide.utils.Constants.YERBA_BUENA_LATITUDE;
import static app.devk.rideguide.utils.Constants.YERBA_BUENA_LONGITUDE;
import static app.devk.rideguide.utils.Constants.YERBA_BUENA_RADIUS_METERS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocateFragment extends Fragment implements OnMapReadyCallback, ResultCallback<Status>, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GoogleMap googleMap;

    private GoogleApiClient googleApiClient;
    private static final String GEOFENCE_ID = "mygeofence";
    private List<Geofence> myFences = new ArrayList<>();

    List<Geofence> mGeofenceList;


    private PendingIntent mGeofenceRequestIntent;


    private View v;
    MapView mapView;

    MapFragment mapFragment;

    private Context ctx;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public LocateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocateFragment newInstance(String param1, String param2) {
        LocateFragment fragment = new LocateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        v = inflater.inflate(R.layout.fragment_locate, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);


        createGoogleApi();


        // Instantiate a new geofence storage area.
//        mGeofenceStorage = new SimpleGeofenceStore(getActivity());
//        // Instantiate the current List of geofences.
//        mGeofenceList = new ArrayList<Geofence>();
//        createGeofences();
//
//        startGeofence();

        return v;
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            //   gps functions.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
        }
    }

    private void startGeofenceMonitoring() {
        Log.d(TAG, "Geofencing monotoring");
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(getLatitude(), getLongitude(), (float) 1)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        myFences.add(geofence);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(getPosition());
        circleOptions.fillColor(Color.argb(0x55, 0x00, 0x00, 0xff));
        circleOptions.strokeColor(Color.argb(0xaa, 0x00, 0x00, 0xff));
        circleOptions.radius(5);
        googleMap.addCircle(circleOptions);

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build();

        Intent i = new Intent(getActivity(), GeofenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

//        if (!googleApiClient.isConnected()) {
//            Log.d(TAG, "not connected to play services");
//        } else {
//            Log.d(TAG, "connected to play services in geofences");
//            marshmallowGPSPremissionCheck();
////            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                // TODO: Consider calling
////                //    ActivityCompat#requestPermissions
////                // here to request the missing permissions, and then overriding
////                  // public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                    //                                      int[] grantResults)
////                // to handle the case where the user grants the permission. See the documentation
////                // for ActivityCompat#requestPermissions for more details.
////
////                return;
////            }
//            try {
//                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
//                        .setResultCallback(new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(@NonNull Status status) {
//                                if (status.isSuccess()) {
//                                    Log.d(TAG, "geofence successfully added");
//                                } else {
//                                    Log.d(TAG, "geofence failed to add " + status.getStatus());
//                                }
//                            }
//                        });
//            } catch (SecurityException e) {
//                Log.d(TAG, "Security exception:" + e);
//            }
//        }
    }




    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        ctx = context;
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocation();
        mapView.onResume();
        //googleApiClient.connect();

        int resp = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if (resp != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), resp, 1).show();
        } else {
            Log.d(TAG, "google play services available");
        }


    }

    @Override
    public void onMapReady(GoogleMap map) {


        googleMap = map;


        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getLatitude(), getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(17)
                    .bearing(90)
                    .tilt(40)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(getLatitude(), getLongitude()))
                    .title("Myself"));



        }


    }


    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(getActivity(), "Lat" + location.getLatitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "on connected play services");

        Toast.makeText(getActivity(), "GoogleApiClient Connected", Toast.LENGTH_SHORT).show();
        startGeofenceMonitoring();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        String lastLocationMessage;
//        if (lastLocation == null) {
//            lastLocationMessage = "Last Location is NULL";
//            moveToLocation(home);
//        } else {
//            lastLocationMessage = String.format("Last Location (%1$s, %2$s)", lastLocation.getLatitude(), lastLocation.getLongitude());
//            moveToLocation(new MyPlaces("Last Location", "I am here.", new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 0, 13, 0));
//        }

        // PRES 3
        mGeofenceRequestIntent = getRequestPendingIntent();
        PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(googleApiClient, myFences, mGeofenceRequestIntent);
        result.setResultCallback(LocateFragment.this);



    }

    private PendingIntent getRequestPendingIntent() {
      return   createRequestPendingIntent();
    }

    private PendingIntent createRequestPendingIntent() {
        if (mGeofenceRequestIntent != null) {
            return mGeofenceRequestIntent;
        } else {
            Intent intent = new Intent(getActivity(), GeofenceService.class);
            intent.setAction("geofence_transition_action");
            return PendingIntent.getBroadcast(getActivity(), 222, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, mGeofenceRequestIntent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }



    public LatLng getPosition(){
        return new LatLng(getLatitude(),getLongitude());
    }

    @Override
    public void onResult(Status status) {
        String toastMessage;
        // PRES 4
        if (status.isSuccess()) {
            toastMessage = "Success: We Are Monitoring";
        } else {
            toastMessage = "Error: We Are NOT Monitoring";
        }
        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
    }





    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }





    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }



    public Location getLocation() {
        try {
            locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


            isNetworkEnabled = locationManager .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                showSettingsAlert();

            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


        public double getLatitude(){

            if(location != null){
                latitude = location.getLatitude();
            }


            return latitude;
        }


        public double getLongitude(){

            if(location != null){
                longitude = location.getLongitude();
            }


            return longitude;
        }



    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);


        alertDialog.setTitle("GPS is settings");


        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");


        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ctx.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}