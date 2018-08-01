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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.zebra.EAI_BLE_demo.R;
import com.zebra.EAI_BLE_demo.HelperClasses.UIHelper;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class DeviceManagement extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Connection connection;

    private String friendlyName, btController, modelPrinter, deviceLanguage, firmwareVersion, linkosVersion;

    private EditText friendlyNameData, modelPrinterData, deviceLanguageData, firmwareVersionData, linkosVersionData;

    private Spinner btControllerData;

    private boolean processFinished = false;
    private boolean dataSelector = false;
    private boolean printerConncted = false;

    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String PREFS_NAME = "OurSavedAddress";
    private UIHelper helper = new UIHelper(this);

    private String macAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        Intent intent = getIntent();
        macAddress = intent.getStringExtra("macAddress");

        TextView macAddressEditText = (TextView) this.findViewById(R.id.idMacAdressInput);
        macAddress = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(macAddress);

        TextView getDataButton = (TextView) findViewById(R.id.button_get_data);

        TextView setDataButton = (TextView) findViewById(R.id.button_set_data);

        friendlyNameData = (EditText) this.findViewById(R.id.idFriendlyNameInput);

        btControllerData = (Spinner) this.findViewById(R.id.idBtControllerInput);

        modelPrinterData = (EditText) this.findViewById(R.id.idModelInput);

        deviceLanguageData = (EditText) this.findViewById(R.id.idDeviceLanguageInput);

        firmwareVersionData = (EditText) this.findViewById(R.id.idFirmwareInput);

        linkosVersionData = (EditText) this.findViewById(R.id.idLink_osInput);

        // Spinner click listener
        btControllerData.setOnItemSelectedListener(this);

        List<String> bluetoothModes = new ArrayList<String>();

        bluetoothModes.add("both");
        bluetoothModes.add("le");
        bluetoothModes.add("classic");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter_bluetoothModes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bluetoothModes);

        // Drop down layout style - list view with radio button
        dataAdapter_bluetoothModes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        btControllerData.setAdapter(dataAdapter_bluetoothModes);


        friendlyNameData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        helper.toggleEditField(friendlyNameData, false);
        helper.dialog(DeviceManagement.this, "Retrieving Data...");

        getDataManagement();
        processingDone();

        /************************************************************************************************************************/
        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (!printerConncted) {
                    processingDone();
                    Toast.makeText(DeviceManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        helper.toggleEditField(friendlyNameData, true);

                        dataSelector = false;

                        friendlyNameData.setText(friendlyName);

                        btControllerData.setSelection(getIndex(btControllerData, btController));

                        modelPrinterData.setText(modelPrinter);

                        deviceLanguageData.setText(deviceLanguage);

                        firmwareVersionData.setText(firmwareVersion);

                        linkosVersionData.setText(linkosVersion);

                    } catch (NullPointerException e) {
                        processingDone();
                        Toast.makeText(DeviceManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        /************************************************************************************************************************/
        setDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (printerConncted) {

                    helper.toggleEditField(friendlyNameData, false);

                    dataSelector = true;
                    friendlyName = friendlyNameData.getText().toString();
                    processFinished = false;
                    helper.dialog(DeviceManagement.this, "Saving Configuration...");
                    getDataManagement();
                    processingDone();
                    if (processFinished == true) {
                        finish();
                    }

                } else {
                    processingDone();
                    Toast.makeText(DeviceManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
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
                        connection = helper.getBTLEPrinterConn(macAddress, DeviceManagement.this);
                        connection.open();
                    } catch (ConnectionException ex) {

                        try {
                            connection = helper.getClassicConn(macAddress);
                            connection.open();
                        } catch (ConnectionException cx) {
                            Toast.makeText(DeviceManagement.this, "Error in initial connection.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                    if (printer.getCurrentStatus().isReadyToPrint) {

                        printerConncted = true;

                        try {
                            if (!dataSelector) {
                                friendlyName = SGD.GET("device.friendly_name", connection);
                                btController = SGD.GET("bluetooth.le.controller_mode", connection);
                                modelPrinter = SGD.GET("device.product_name", connection);
                                deviceLanguage = SGD.GET("device.languages", connection);
                                firmwareVersion = SGD.GET("appl.name", connection);
                                linkosVersion = SGD.GET("appl.link_os_version", connection);
                            } else {
                                SGD.SET("device.friendly_name", friendlyName, connection);
                                SGD.SET("bluetooth.le.controller_mode", btController, connection);
                            }

                        } catch (Exception e) {
                            Toast.makeText(DeviceManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (printer.getCurrentStatus().isHeadOpen) {
                        Toast.makeText(DeviceManagement.this, "Please Close Printer Head to Print.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaused) {
                        Toast.makeText(DeviceManagement.this, "Error: Printer Paused.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaperOut) {
                        Toast.makeText(DeviceManagement.this, "Please Re-load Media to Print.\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DeviceManagement.this, "Please check the Connection of the Printer.", Toast.LENGTH_SHORT).show();
                    }


                    connection.close();
                    processFinished = true;

//                    try {
//                        Thread.sleep(6000);
//                    } catch (Exception e) {
//                    }

                } catch (ConnectionException e) {
                    Toast.makeText(DeviceManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                } catch (ZebraPrinterLanguageUnknownException e) {
                    Toast.makeText(DeviceManagement.this, "Language unknown.", Toast.LENGTH_SHORT).show();


                } finally {

                    try {
                        connection.close();
                    } catch (ConnectionException e) {
                        Toast.makeText(DeviceManagement.this, "Error while closing connection.", Toast.LENGTH_SHORT).show();

                    }
                    helper.dismissDialog();
                    Looper.loop();
                    Looper.myLooper().quit();

                }
            }

        }).start();
    }


    /************************************************************************************************************************/
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    /************************************************************************************************************************/
    private void processingDone() {

        friendlyNameData.setText("-----------");
        modelPrinterData.setText("-----------");
        deviceLanguageData.setText("-----------");
        firmwareVersionData.setText("-----------");
        linkosVersionData.setText("-----------");
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
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    @Override
    protected void onPause() {
        super.onPause();
    }


    /************************************************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /************************************************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
        }
    }


    /************************************************************************************************************************/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        btController = parent.getItemAtPosition(position).toString();
    }

}
