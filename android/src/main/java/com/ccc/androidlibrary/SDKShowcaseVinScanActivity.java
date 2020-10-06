package com.ccc.androidlibrary;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cccis.sdk.android.vindecoding.VinDecodingActivity;
import com.cccis.sdk.android.vindecoding.ex.EnterVINManuallyActivity;

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
    }

    @Override
    protected void onInvalidVin(String decoded, String imagePath) {
        Toast.makeText(this, "Scanned invalid VIN: " + decoded + " - Image Path=" + imagePath, Toast.LENGTH_LONG).show();
        log.w("VIN", "Invalid VIN" + decoded);
    }
}
