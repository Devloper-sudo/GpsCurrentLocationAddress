package abhishek.com.gpslocationaddress;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LocationResult, ActivityCompat.OnRequestPermissionsResultCallback

{


    private static final int PERMISSION_REQUEST_LOCATION = 1002;
    private EditText current_location;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int INITIAL_REQUEST = 13;

    MyLocation myLocation;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current_location = findViewById(R.id.current_location);
        myLocation = new MyLocation();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();



                boolean networkPresent = myLocation.getLocation(MainActivity.this, this);
                if (!networkPresent) {
                    showSettingsAlert();
                }


            } else {
                // Permission is missing and must be requested.
                requestCameraPermission();
            }


//        if (!canAccessLocation() || !canAccessCoreLocation()) {
//            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
//
//        } else {
//            boolean networkPresent = myLocation.getLocation(MainActivity.this, MainActivity.this);
//            if (!networkPresent) {
//                showSettingsAlert();
//            }
//        }

        }


    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {


            Toast.makeText(this, "Love", Toast.LENGTH_SHORT).show();

//            boolean networkPresent = myLocation.getLocation(MainActivity.this, this);
//            if (!networkPresent) {
//                showSettingsAlert();
//            }


            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);



        } else

        {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.

                    boolean networkPresent = myLocation.getLocation(MainActivity.this, this);
                    if (!networkPresent) {
                        showSettingsAlert();
                    }


                Toast.makeText(this, "Permission Granted 2", Toast.LENGTH_SHORT).show();
            } else {
                // Permission request was denied.
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCoreLocation() {
        return (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean hasPermission(String perm) {

        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, perm));
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        switch (requestCode) {
//            case INITIAL_REQUEST:
//                if (canAccessLocation() && canAccessCoreLocation()) {
//                    boolean networkPresent = myLocation.getLocation(MainActivity.this, this);
//                    if (!networkPresent) {
//                        showSettingsAlert();
//                    }
//                } else {
//                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
//                }
//
//                break;
//
//
//        }
//    }


    @Override
    public void gotLocation(Location location) {


        if (location != null) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            final String result = "Latitude: " + location.getLatitude() +
                    " Longitude: " + location.getLongitude();


            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {

//                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
//                editText.setText(result);
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());
                }
            });
        }
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Toast.makeText(MainActivity.this, locationAddress, Toast.LENGTH_SHORT).show();
            current_location.setText(locationAddress);

        }
    }

}
