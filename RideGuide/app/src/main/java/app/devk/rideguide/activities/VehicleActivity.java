package app.devk.rideguide.activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import app.devk.rideguide.R;
import app.devk.rideguide.fragments.LocateFragment;
import app.devk.rideguide.fragments.VehicleFragment;

public class VehicleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LocateFragment.OnFragmentInteractionListener {



    private static final String GET_VEHICLE_INFO_URL="http://ec2-54-169-71-234.ap-southeast-1.compute.amazonaws.com:9001/api/VehiclesMaster?vehicleModelBrand=Bajaj%20Pulsar%20180";


    private static final String TAG = "Vehicles";

    private String[] drawerItems;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private  Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_vehicle);






        navigationView= (NavigationView) findViewById(R.id.navigationview);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);








    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {






        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager().beginTransaction().add(R.id.mainlayout,new VehicleFragment()).commit();




    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_locate:
//                Intent i= new Intent(this,LocaterActivity.class);
//                startActivity(i);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainlayout,new LocateFragment()).commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.nav_vehicles:
//                Intent i2= new Intent(this,VehicleActivity.class);
//                startActivity(i2);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainlayout,new VehicleFragment()).commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
        }
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
