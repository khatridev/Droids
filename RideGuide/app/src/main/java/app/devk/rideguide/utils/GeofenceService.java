package app.devk.rideguide.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static app.devk.rideguide.utils.Constants.TAG;

/**
 * Created by kumardev on 11/29/2016.
 */

public class GeofenceService extends IntentService {

    public static final String TAG="GeofenceServie";
    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent=GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){

        }else{
            int trans=geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences=geofencingEvent.getTriggeringGeofences();
            Geofence geofence=geofences.get(0);
            String reqId=geofence.getRequestId();


            if(trans==Geofence.GEOFENCE_TRANSITION_ENTER){
                Log.d(TAG,"Entering geofence "+reqId);
                Toast.makeText(getApplicationContext(),"Geofence Enter",Toast.LENGTH_LONG).show();
            }else if(trans==Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d(TAG,"Exiting geofence "+reqId);
                Toast.makeText(getApplicationContext(),"Geofence Exit",Toast.LENGTH_LONG).show();
            }




        }

    }
}
