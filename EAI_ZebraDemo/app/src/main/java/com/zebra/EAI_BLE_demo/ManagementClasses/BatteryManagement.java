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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.EAI_BLE_demo.R;
import com.zebra.EAI_BLE_demo.HelperClasses.UIHelper;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class BatteryManagement extends AppCompatActivity {

    private Connection connection;

    private String batteryVoltage, batteryPercentFull, batteryStatus, batteryHealth, batteryRechargeCls, powerSleepEnabled;

    private EditText batteryVoltageData, batteryPercentData, batteryStatusData, batteryHealthData, batteryRechargeClsData;

    private Switch powerSleep_state;

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
        setContentView(R.layout.activity_battery_management);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        Intent intent = getIntent();
        macAddress = intent.getStringExtra("macAddress");


        TextView macAddressEditText = (TextView) this.findViewById(R.id.idMacAdressInput);
        macAddress = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(macAddress);

        TextView getDataButton = (TextView) findViewById(R.id.button_get_data);
        TextView setDataButton = (TextView) findViewById(R.id.button_set_data);

        batteryVoltageData = (EditText) this.findViewById(R.id.idPowerVoltageInput);
        batteryPercentData = (EditText) this.findViewById(R.id.idPowerPorcentInput);
        batteryStatusData = (EditText) this.findViewById(R.id.idPowerStatusInput);
        batteryHealthData = (EditText) this.findViewById(R.id.idPowerHealthInput);
        batteryRechargeClsData = (EditText) this.findViewById(R.id.idPowerCycleCountInput);
        powerSleep_state = (Switch) this.findViewById(R.id.idPowerSleepEnabledInput);

        helper.dialog(BatteryManagement.this, "Retrieving Data...");
        getDataManagement();
        processingDone();


        /************************************************************************************************************************/
        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (!printerConncted) {
                    processingDone();
                    Toast.makeText(BatteryManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        dataSelector = false;

                        if (batteryVoltage.equals("?")) {
                            batteryVoltageData.setText("N/A");
                        } else {
                            batteryVoltageData.setText(batteryVoltage);
                        }

                        if (batteryPercentFull.equals("?")) {
                            batteryPercentData.setText("N/A");
                        } else {
                            batteryPercentData.setText(batteryPercentFull);
                        }


                        if (batteryStatus.equals("?")) {
                            batteryStatusData.setText("N/A");
                        } else {
                            batteryStatusData.setText(batteryStatus);
                        }


                        if (batteryHealth.equals("?")) {
                            batteryHealthData.setText("N/A");
                        } else {
                            batteryHealthData.setText(batteryHealth);
                        }


                        if (batteryRechargeCls.equals("?")) {
                            batteryRechargeClsData.setText("N/A");
                        } else {
                            batteryRechargeClsData.setText(batteryRechargeCls);
                        }


                        if (powerSleepEnabled.equals("on")) {
                            powerSleep_state.setChecked(true);
                        } else
                            powerSleep_state.setChecked(false);
                    } catch (NullPointerException e) {

                        processingDone();
                        Toast.makeText(BatteryManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        /************************************************************************************************************************/
        setDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (printerConncted) {

                    dataSelector = true;

                    if (powerSleep_state.isChecked()) {
                        powerSleep_state.setChecked(true);
                        powerSleepEnabled = "on";
                    } else {
                        powerSleep_state.setChecked(false);
                        powerSleepEnabled = "off";
                    }

                    processFinished = false;
                    helper.dialog(BatteryManagement.this, "Saving Configuration...");
                    getDataManagement();
                    processingDone();

                    if (processFinished == true) {
                        finish();
                    }
                } else {
                    Toast.makeText(BatteryManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
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
                        connection = getBTLEPrinterConn();
                        connection.open();
                    } catch (ConnectionException ex) {

                        try {
                            connection = getClassicConn();
                            connection.open();
                        } catch (ConnectionException cx) {
                            Toast.makeText(BatteryManagement.this, "Error in initial connection.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                    if (printer.getCurrentStatus().isReadyToPrint) {

                        printerConncted = true;

                        try {
                            if (!dataSelector) {
                                batteryVoltage = SGD.GET("power.voltage", connection);
                                batteryPercentFull = SGD.GET("power.percent_full", connection);
                                batteryStatus = SGD.GET("power.status", connection);
                                batteryHealth = SGD.GET("power.health", connection);
                                batteryRechargeCls = SGD.GET("power.cycle_count", connection);
                                powerSleepEnabled = SGD.GET("power.sleep.enable", connection);
                            } else {
                                SGD.SET("power.sleep.enable", powerSleepEnabled, connection);
                            }

                            //checkIfApplies();

                        } catch (Exception e) {
                            Toast.makeText(BatteryManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (printer.getCurrentStatus().isHeadOpen) {
                        Toast.makeText(BatteryManagement.this, "Please Close Printer Head to Print.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaused) {
                        Toast.makeText(BatteryManagement.this, "Error: Printer Paused.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaperOut) {
                        Toast.makeText(BatteryManagement.this, "Please Re-load Media to Print.\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BatteryManagement.this, "Please check the Connection of the Printer.", Toast.LENGTH_SHORT).show();
                    }


                    connection.close();
                    processFinished = true;

//                    try {
//                        Thread.sleep(6000);
//                    } catch (Exception e) {
//                    }

                } catch (ConnectionException e) {
                    Toast.makeText(BatteryManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                } catch (ZebraPrinterLanguageUnknownException e) {
                    Toast.makeText(BatteryManagement.this, "Language unknown.", Toast.LENGTH_SHORT).show();


                } finally {

                    try {
                        connection.close();
                    } catch (ConnectionException e) {
                        Toast.makeText(BatteryManagement.this, "Error while closing connection.", Toast.LENGTH_SHORT).show();

                    }
                    helper.dismissDialog();
                    Looper.loop();
                    Looper.myLooper().quit();

                }
            }

        }).start();

    }


    /************************************************************************************************************************/
    private void checkIfApplies() {
        if (batteryVoltage.equals("?")) {
            batteryVoltageData.setText("N/A");
        }

        if (batteryPercentFull.equals("?")) {
            batteryPercentData.setText("N/A");
        }


//        batteryPercentData.setText("N/A");
//        batteryStatusData.setText("N/A");
//        batteryHealthData.setText("N/A");
//        batteryRechargeClsData.setText("N/A");
//        powerSleep_state.setChecked(false);
    }


    /************************************************************************************************************************/
    private void processingDone() {
        batteryVoltageData.setText("-----------");
        batteryPercentData.setText("-----------");
        batteryStatusData.setText("-----------");
        batteryHealthData.setText("-----------");
        batteryRechargeClsData.setText("-----------");
        powerSleep_state.setChecked(false);
    }


    /************************************************************************************************************************/
    private Connection getBTLEPrinterConn() {
        return new BluetoothLeConnection(macAddress, this);
    }

    /************************************************************************************************************************/
    private Connection getClassicConn() {
        return new BluetoothConnection(macAddress);
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

}


