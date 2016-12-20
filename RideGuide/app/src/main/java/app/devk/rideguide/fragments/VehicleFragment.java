package app.devk.rideguide.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import app.devk.rideguide.R;
import app.devk.rideguide.activities.VehicleActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VehicleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VehicleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="VehicleFragment" ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String GET_VEHICLE_INFO_URL="http://ec2-54-169-71-234.ap-southeast-1.compute.amazonaws.com:9001/api/VehiclesMaster?vehicleModelBrand=Bajaj%20Pulsar%20180";

    private OnFragmentInteractionListener mListener;

    private String vehicleType;
    private String vehicleModel;
    private String vehicleDissemblingQty;
    private String vehicleDrainingQty;
    private String vehicleQty;
    private String vehicleGrade;
    private String oilBrandName;
    private String oilPrice;
    private String vehicleBrand;

    private View v;

    private TextView txtVehicleType;
    private TextView txtVehicleModel;
    private TextView txtVehicleDissembleQty;
    private TextView txtVehicleDrainingQty;
    private TextView txtVehicleQty;
    private TextView txtVehicleGrade;
    private TextView txtVehicleBrand;



    private TextView txtOilName;
    private TextView txtOilPrice;
    ProgressDialog progressDialog;

    private GoogleApiClient mApiClient;




    public VehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VehicleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleFragment newInstance(String param1, String param2) {
        VehicleFragment fragment = new VehicleFragment();
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
        v= inflater.inflate(R.layout.fragment_vehicle, container, false);

        txtVehicleType= (TextView) v.findViewById(R.id.txtVehicleType);
        txtVehicleModel= (TextView) v.findViewById(R.id.txtVehicleModel);

        txtVehicleQty= (TextView) v.findViewById(R.id.txtVehicleQty);
        txtVehicleGrade= (TextView) v.findViewById(R.id.txtVehicleGrade);

        txtOilName= (TextView) v.findViewById(R.id.txtOilName);
        txtOilPrice= (TextView) v.findViewById(R.id.txtOilPrice);

        txtVehicleBrand= (TextView) v.findViewById(R.id.txtBrandName);

        txtVehicleDissembleQty= (TextView) v.findViewById(R.id.txtDissembleQty);
        txtVehicleDrainingQty= (TextView) v.findViewById(R.id.txtDrainingQty);



        return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    @Override
    public void onResume() {
        super.onResume();
        

        if(isNetworkAvailable()){
            new GetVehicleData().execute(GET_VEHICLE_INFO_URL);
        }else{
            createNetErrorDialog();
        }

    }

    private class GetVehicleData extends AsyncTask<String,String,String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialog=ProgressDialog.show(getActivity(),"","Please Wait",false);

        }

        @Override
        protected String doInBackground(String... strings) {

            Log.d(TAG,strings[0]);

            HttpURLConnection con=null;
            URL obj = null;

            try {
                obj = new URL(strings[0]);
                con = (HttpURLConnection) obj.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //add reuqest header
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }





            int responseCode = 0;
            try {
                responseCode = con.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("\nSending 'POST'URL: " ,":"+ strings[0]);

            Log.d("Response Code : ", ":" + responseCode);

            BufferedReader in = null;
            StringBuffer response=null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String response_data=response.toString();

            Log.d("Resp Res",":"+response.toString());

            return response_data;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject=new JSONObject(s);
                vehicleType=jsonObject.getString("VehicleType");
                vehicleModel=jsonObject.getString("VehicleModelBrand");
                vehicleDissemblingQty=jsonObject.getString("DisassemblingQty");
                vehicleDrainingQty=jsonObject.getString("DrainingQty");
                vehicleQty=jsonObject.getString("Quantity");
                vehicleGrade=jsonObject.getString("Grade");
                vehicleBrand=jsonObject.getString("VehicleBrand");
                JSONArray jsonArray=jsonObject.getJSONArray("OilBrandPrice");

                oilBrandName=jsonArray.getJSONObject(0).getString("OilBrandName");
                oilPrice=jsonArray.getJSONObject(0).getString("OilPrice");




                updateUI();

               //Toast.makeText(getActivity(), "resp "+vehicleType, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(progressDialog.isShowing())
            progressDialog.dismiss();;


        }
    }

    private void updateUI() {
        txtVehicleType.setText(vehicleType);
        txtVehicleModel.setText(vehicleModel);
        txtVehicleQty.setText("Quantity: "+vehicleQty);
        txtVehicleGrade.setText("Grade: "+vehicleGrade);
        txtOilName.setText(oilBrandName);
        txtOilPrice.setText(oilPrice);
        txtVehicleBrand.setText(vehicleBrand);
        txtVehicleDissembleQty.setText(vehicleDissemblingQty);
        txtVehicleDrainingQty.setText(vehicleDrainingQty);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity(). getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    protected void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

}
