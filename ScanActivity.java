package com.zebra.basicscanningtutorial;
/*Be sure to check out techdocs.zebra.com for all your Enterprise Needs :)*/

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;


public class ScanActivity extends AppCompatActivity implements EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener {
    private TextView statusView = null;
    private TextView dataView = null;

    private Scanner scanner = null;
    private BarcodeManager barcodeManager = null;
    private EMDKManager emdkManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = findViewById(R.id.statusView);
        dataView = findViewById(R.id.dataView);

        if (emdkManager == null) {
            EMDKResults results = EMDKManager.getEMDKManager(this, this);

            if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
                dataView.setText("EMDKManager Request Failed");
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;

        try {
            initializeScanner();
        } catch (ScannerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClosed() {
        if (this.emdkManager != null) {
            this.emdkManager.release();
            this.emdkManager = null;
        }
    }


    private void initializeScanner() throws ScannerException {
        if (scanner == null) {
            barcodeManager = (BarcodeManager) this.emdkManager
                    .getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            scanner.addDataListener(this);
            scanner.addStatusListener(this);
            scanner.triggerType = Scanner.TriggerType.HARD;
            scanner.enable();
            scanner.read();
        }
    }


    @Override
    public void onStatus(StatusData statusData) {

        new AsyncStatusUpdate().execute(statusData);
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        new AsyncDataUpdate().execute(scanDataCollection);

    }


    private class AsyncStatusUpdate extends AsyncTask<StatusData, Void, String> {

        @Override
        protected String doInBackground(StatusData... params) {
            String statusStr = "";
            StatusData statusData = params[0];
            StatusData.ScannerStates state = statusData.getState();
            // Different states of Scanner
            switch (state) {

                // Scanner is IDLE
                case IDLE:
                    statusStr = "The scanner enabled and its idle";
                    try {
                        scanner.read();
                    } catch (ScannerException e) {
                    }

                    break;
                case SCANNING:
                    statusStr = "Scanning..";
                    break;
                case WAITING:
                    statusStr = "Waiting for trigger press..";
                    break;
                case DISABLED:
                    statusStr = "Scanner is not enabled";
                    break;
                default:
                    break;
            }
            return statusStr;
        }

        @Override
        protected void onPostExecute(String result) {
            statusView.setText(result);
        }


    }


    private class AsyncDataUpdate extends AsyncTask<ScanDataCollection, Void, String> {
        @Override
        protected String doInBackground(ScanDataCollection... params) {

            String statusStr = "";

            ScanDataCollection scanDataCollection = params[0];

            if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
                ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//                scanData.get(0).getLabelType();
//                scanData.get(0).getTimeStamp()

                statusStr = scanData.get(0).getData();
            }
            return statusStr;
        }

        @Override
        protected void onPostExecute(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataView.setText(result);
                }
            });
        }


    }


}