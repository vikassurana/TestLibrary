package com.ccc.androidlibrary;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cccis.mobile.sdk.android.qephotocapture.utils.QEPhotoCaptureConfigurationFactory;
import com.cccis.sdk.android.services.callback.BaseCCCAPIRequestCallback;
import com.cccis.sdk.android.services.rest.context.ENVFactory;
import com.cccis.sdk.android.services.rest.request.VehicleServiceRequest;
import com.cccis.sdk.android.vindecode.CCCAPIVinDecodeClientService;
import com.cccis.sdk.android.vindecoding.VinDecodingActivity;
import com.cccis.sdk.android.vindecoding.ex.EnterVINManuallyActivity;
import com.fasterxml.jackson.core.type.TypeReference;

public class SDKShowcaseVinScanActivity extends VinDecodingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkshowcase_vin_scan);
    }

    public void scan(View view){
        initiateScan(EnterVINManuallyActivity.class);
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onValidVin(String decoded, String imagePath) {
        Toast.makeText(this, "Scanned valid VIN: " + decoded + " - Image Path=" + imagePath, Toast.LENGTH_LONG).show();

        //final String vin = "JHLRD68545C011932";
            CCCAPIVinDecodeClientService service = new CCCAPIVinDecodeClientService(ENVFactory.getInstance(this).SHARED_ENV);
            VehicleServiceRequest request = new VehicleServiceRequest();
            request.setVin(decoded);

        try {
            service.vindecodeDataCenter(request, new BaseCCCAPIRequestCallback(){
                @Override
                public TypeReference getSuccessTypeReference() {return new TypeReference<Object>() {};}

                @Override
                public void onSuccess(Object o) {
                    //ActivityHelper.showMessage(SDKShowcaseActivity.this, o + "");
                    Log.i("onSuccess of library", "Decode success! " + o);
                    int index = o.toString().indexOf("bodyTypeCode")+13;
                    Log.i("onSuccess of library", "Index is: " + index);
                    String subString = o.toString().substring(index);
                    Log.i("onSuccess of library", "SubString is: " + subString);
                    int secondIndex = subString.indexOf(",");
                    Log.i("onSuccess of library", "secondIndex is: " + secondIndex);
                    String vehicleType = subString.substring(0,secondIndex);
                    Log.i("onSuccess of library", "vehicleType is: " + vehicleType);
                    configurePcBasedOnBodyType(getApplicationContext(), vehicleType);
                    Intent intent = new Intent(getApplicationContext(), SDKShowcaseActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Object o, int statusCode, Throwable throwable) {
                    Log.i("onFailure of library", "Object: " + o);
                    Log.i("onFailure of library", "statusCode: " + statusCode);
                    Log.i("onFailure of library", "throwable: " + throwable);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception ", "Exception caught: " + e);
        }


        //Intent intent = new Intent(this, SDKShowcaseActivity.class);
        //intent.putExtra("vehicleType", vehicleType);
        //startActivity(intent);
    }

    @Override
    protected void onInvalidVin(String decoded, String imagePath) {
        Toast.makeText(this, "Scanned invalid VIN: " + decoded + " - Image Path=" + imagePath, Toast.LENGTH_LONG).show();
        log.w("VIN", "Invalid VIN" + decoded);
    }

    public static void configurePcBasedOnBodyType(Context context, String aBodyType) {
        if (aBodyType.equals(context.getString(R.string.body_type_code_coupe))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.COUPE);
        } else if (aBodyType.equals(context.getString(R.string.body_type_code_hatchback))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.HATCHBACK);
        } else if (aBodyType.equals(context.getString(R.string.body_type_code_sedan))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.SEDAN);
        } else if (aBodyType.equals(context.getString(R.string.body_type_code_suv))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.SUV);
        } else if (aBodyType.equals(context.getString(R.string.body_type_code_van))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.VAN);
        } else if (aBodyType.equals(context.getString(R.string.body_type_code_wagon))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.WAGON);
        } else if ((aBodyType.equals(context.getString(R.string.body_type_code_truck1))) || (aBodyType.equals(context.getString(R.string.body_type_code_truck2))) || (aBodyType.equals(context.getString(R.string.body_type_code_truck3)))) {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.TRUCK);
        } else {
            QEPhotoCaptureConfigurationFactory.getInstance(context, true).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.SEDAN);
        }
    }

}
