package com.ccc.androidlibrary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cccis.sdk.android.common.activity.LogSupportActivity;
import com.cccis.sdk.android.common.callback.OnResult;
import com.cccis.sdk.android.common.helper.ActivityHelper;
import com.cccis.sdk.android.common.helper.MessageHelper;
import com.cccis.sdk.android.common.provider.RunTimeVariableProvider;
import com.cccis.sdk.android.domain.legacy.ExpressEstimateWorkflowState;
import com.cccis.sdk.android.photocapturelocalstorage.QELocalStorageCapturedPhotoService;
import com.cccis.sdk.android.services.callback.BaseCCCAPIRequestCallback;
import com.cccis.sdk.android.services.legacy.CapturedPhotoService;
import com.cccis.sdk.android.services.rest.context.ENVFactory;
import com.cccis.sdk.android.services.rest.request.VehicleServiceRequest;
import com.cccis.sdk.android.upload.MCEPClientService;
import com.cccis.sdk.android.vindecode.CCCAPIVinDecodeClientService;
import com.fasterxml.jackson.core.type.TypeReference;

public class SDKShowcaseActivity extends LogSupportActivity {
    private static final int DEMO_ACTIVITY_REQUEST_CODE = 100;

    private static final int LOGIN_REQUEST = 300;

    private MCEPClientService mcepClientService;
    private CapturedPhotoService capturedPhotoService;

    private ProgressBar spinner;
    private LinearLayout buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_sdk_showcase);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#d97a23"), android.graphics.PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.GONE);

        buttons = (LinearLayout)findViewById(R.id.buttons);

        RunTimeVariableProvider.setImageCollectionKey(DemoConstants.IMAGE_COLLECTION_KEY);
//        String vehicleType = getIntent().getStringExtra("vehicleType");
//        Log.i("vehicleType ", "vehicleType " + vehicleType);


        try {
            mcepClientService = new MCEPClientService(ENVFactory.getInstance(this).SHARED_ENV);
            capturedPhotoService = QELocalStorageCapturedPhotoService.getInstance(getApplicationContext(), DemoConstants.ESTIMATE_PDF_NAME, DemoConstants.IMAGE_COLLECTION_KEY);

            //Override to simulate different vehiche types
            //QEPhotoCaptureConfigurationFactory.getInstance(this).setVehicleType(QEPhotoCaptureConfigurationFactory.VEHICLE_TYPE.SUV);
        } catch (final Exception e){
            MessageHelper.showPopupError(this, e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RunTimeVariableProvider.setImageCollectionKey(DemoConstants.IMAGE_COLLECTION_KEY);
        cancel();
    }

//    public void logon(View view) {
//        Intent intent = new Intent(this, SDKShowcaseLoginActivity.class);
//        startActivityForResult(intent, LOGIN_REQUEST);
//    }

    public void photo(View view) {
        Intent intent = new Intent(this, PhotoCaptureShowcaseActivity.class);
        startActivity(intent);
    }

//    public void shareEstimate(View view) {
//        if(authenticated()) {
//            spinner.setVisibility(View.VISIBLE);
//            buttons.setVisibility(View.GONE);
//
//            capturedPhotoService.getWorkflowState(new OnResult<ExpressEstimateWorkflowState>() {
//                @Override
//                public void onResult(final ExpressEstimateWorkflowState result) {
//                    if (result == ExpressEstimateWorkflowState.ESTIMATE_AVAILABLE) {
//                        Intent intent = new Intent(getApplicationContext(), ShareEstimateActivity.class);
//                        startActivity(intent);
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                cancel();
//                                MessageHelper.showPopupError(SDKShowcaseActivity.this, "Estimate not yet available! Workflow state = " + result);
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    }
//
    public void workflowState(View view) {
        if(authenticated()) {
            capturedPhotoService.getWorkflowState(new OnResult<ExpressEstimateWorkflowState>() {
                @Override
                public void onResult(final ExpressEstimateWorkflowState result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityHelper.showMessage(SDKShowcaseActivity.this, result + "");
                        }
                    });
                }
            });
        }
    }
//
    public void vinScan(View view){
        if(authenticated()) {
            Intent intent = new Intent(this, SDKShowcaseVinScanActivity.class);
            startActivity(intent);
        }
    }

    public void vinDecode(View view) throws Exception {
        final String vin = "WP0AA2994VS320240";  //JHLRD68545C011932
        if(authenticated()) {
            CCCAPIVinDecodeClientService service = new CCCAPIVinDecodeClientService(ENVFactory.getInstance(this).SHARED_ENV);
            VehicleServiceRequest request = new VehicleServiceRequest();
            request.setVin(vin);
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
                }

                @Override
                public void onFailure(Object o, int statusCode, Throwable throwable) {
                    Log.i("onFailure of library", "Object: " + o);
                    Log.i("onFailure of library", "statusCode: " + statusCode);
                    Log.i("onFailure of library", "throwable: " + throwable);
                }

//                @Override
//                public void onSuccess(VinDecodeResponseDataCenter result) {
//                    //handle success
//                }
//                @Override
//                public void onFailure(RESTErrorResponse result, int statusCode, Throwable t) {
//                    //handle failure
//                }
            });
        }
    }

    private void cancel(){
        spinner.setVisibility(View.GONE);
        buttons.setVisibility(View.VISIBLE);
    }

    public boolean authenticated(){
        if (!mcepClientService.isAuthenticated()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageHelper.showPopupError(SDKShowcaseActivity.this, "Please authenticate yourself first..!!!");
                }
            });
            return false;
        }

        return true;
    }

}
