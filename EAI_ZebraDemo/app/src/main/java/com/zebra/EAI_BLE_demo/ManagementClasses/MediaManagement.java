/**
 * ********************************************
 * CONFIDENTIAL AND PROPRIETARY
 * <p/>
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published,
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * <p/>
 * Copyright ZIH Corp. 2015
 * <p/>
 * ALL RIGHTS RESERVED
 * *********************************************
 */


package com.zebra.EAI_BLE_demo.ManagementClasses;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.EAI_BLE_demo.R;
import com.zebra.EAI_BLE_demo.HelperClasses.UIHelper;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import android.content.res.Resources;


public class MediaManagement extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Connection connection;

    private String mediaType;
    private String ribbonEnabled;
    private String mediaSpeed;
    private String printedMediaLength;
    private String printedLabels;
    private String odometerMarkerCount;

    private EditText mediaTypeData;
    private EditText ribbonEnabledData;
    private EditText printedMediaLengthData;
    private EditText printedLabelsData;
    private EditText odometerMarkerCountData;

    private Spinner mediaSpeedData;

    private boolean dataSelector = false;
    private boolean processFinished = false;
    private boolean printerConncted = false;


    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String PREFS_NAME = "OurSavedAddress";
    private UIHelper helper = new UIHelper(this);

    private String macAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_management);
        Resources res = getResources();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        Intent intent = getIntent();
        macAddress = intent.getStringExtra("macAddress");

        TextView macAddressEditText = (TextView) this.findViewById(R.id.idMacAdressInput);
        macAddress = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(macAddress);

        TextView getDataButton = (TextView) findViewById(R.id.button_get_data);

        TextView setDataButton = (TextView) findViewById(R.id.button_set_data);

        mediaTypeData = (EditText) this.findViewById(R.id.idMediaTypeInput);

        ribbonEnabledData = (EditText) this.findViewById(R.id.idRibbonEnabledInput);

        printedMediaLengthData = (EditText) this.findViewById(R.id.idOdometerTotalLengthInput);

        printedLabelsData = (EditText) this.findViewById(R.id.idOdometerTotalLabelInput);

        odometerMarkerCountData = (EditText) this.findViewById(R.id.idOdometerMarketCountInput);

        mediaSpeedData = (Spinner) findViewById(R.id.idMediaSpeedInput);

        // Spinner click listener
        mediaSpeedData.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        String[] mediaSpeed_Options = res.getStringArray(R.array.speedOptions);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter_mediaSpeeds = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mediaSpeed_Options);

        // Drop down layout style - list view with radio button
        dataAdapter_mediaSpeeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mediaSpeedData.setAdapter(dataAdapter_mediaSpeeds);

        //helper.showLoadingDialog("loading data");
        helper.dialog(MediaManagement.this, "Retrieving Data...");
        getDataManagement();
        processingDone();


        /************************************************************************************************************************/
        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (!printerConncted) {
                    processingDone();
                    Toast.makeText(MediaManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                } else {

                    try {

                        helper.toggleEditField(mediaTypeData, false);

                        mediaSpeedData.setSelection(getIndex(mediaSpeedData, mediaSpeed));

                        mediaTypeData.setText(mediaType);

                        ribbonEnabledData.setText(ribbonEnabled);

                        printedMediaLengthData.setText(printedMediaLength);

                        printedLabelsData.setText(printedLabels);

                        odometerMarkerCountData.setText(odometerMarkerCount);
                    } catch (NullPointerException e) {
                        processingDone();
                        Toast.makeText(MediaManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        /************************************************************************************************************************/
        setDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (printerConncted) {

                    helper.toggleEditField(mediaTypeData, false);
                    dataSelector = true;
                    processFinished = false;
                    //helper.showLoadingDialog("Setting & Saving Data ");

                    helper.dialog(MediaManagement.this, "Saving Configuration...");
                    processingDone();
                    getDataManagement();

                    if (processFinished == true) {
                        finish();
                    }
                } else {
                    Toast.makeText(MediaManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /************************************************************************************************************************/
    private void getDataManagement() {

        new Thread(new Runnable() {
            public void run() {

                try {
                    Looper.prepare();
                    try {
                        connection = helper.getBTLEPrinterConn(macAddress, MediaManagement.this);
                        connection.open();
                    } catch (ConnectionException ex) {

                        try {
                            connection = helper.getClassicConn(macAddress);
                            connection.open();
                        } catch (ConnectionException cx) {
                            Toast.makeText(MediaManagement.this, "Error in initial connection.", Toast.LENGTH_SHORT).show();

                        }
                    }
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                    //     getPrinterStatus();

                    if (printer.getCurrentStatus().isReadyToPrint) {
                        try {

                            printerConncted = true;

                            if (dataSelector == false) {
                                mediaType = SGD.GET("media.type", connection);
                                ribbonEnabled = SGD.GET("ezpl.print_method", connection);
                                mediaSpeed = SGD.GET("media.speed", connection);
                                printedMediaLength = SGD.GET("odometer.total_print_length", connection);
                                printedLabels = SGD.GET("odometer.total_label_count", connection);
                                odometerMarkerCount = SGD.GET("odometer.media_marker_count", connection);
                            } else {
                                SGD.SET("media.speed", mediaSpeed, connection);
                            }
                        } catch (Exception e) {
                            Toast.makeText(MediaManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (printer.getCurrentStatus().isHeadOpen) {
                        Toast.makeText(MediaManagement.this, "Please Close Printer Head to Print.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaused) {
                        Toast.makeText(MediaManagement.this, "Error: Printer Paused.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaperOut) {
                        Toast.makeText(MediaManagement.this, "Please Re-load Media to Print.\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MediaManagement.this, "Please check the Connection of the Printer.", Toast.LENGTH_SHORT).show();
                    }


                    connection.close();
                    processFinished = true;

//                    try {
//                        Thread.sleep(6000);
//                    } catch (Exception e) {
//                    }

                } catch (ConnectionException e) {
                    Toast.makeText(MediaManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                } catch (ZebraPrinterLanguageUnknownException e) {
                    Toast.makeText(MediaManagement.this, "Language unknown.", Toast.LENGTH_SHORT).show();


                } finally {
                    try {
                        connection.close();
                    } catch (ConnectionException e) {
                        Toast.makeText(MediaManagement.this, "Error while closing connection.", Toast.LENGTH_SHORT).show();

                    }
                    helper.dismissDialog();
                    Looper.loop();
                    Looper.myLooper().quit();

                }
            }

        }).start();

    }

    /************************************************************************************************************************/
    private void processingDone() {

        mediaTypeData.setText("-----------");
        ribbonEnabledData.setText("-----------");
        printedMediaLengthData.setText("");
        printedLabelsData.setText("-----------");
        odometerMarkerCountData.setText("-----------");
    }

    /************************************************************************************************************************/
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    /************************************************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /************************************************************************************************************************/
    @Override
    protected void onPause() {
        super.onPause();
    }


    /************************************************************************************************************************/
    @Override
    protected void onResume() {
        super.onResume();
        processingDone();
    }


    /************************************************************************************************************************/
    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }


    /************************************************************************************************************************/
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    /************************************************************************************************************************/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mediaSpeed = parent.getItemAtPosition(position).toString();
    }


    /************************************************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
        }
    }
}
