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
import android.widget.Spinner;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.List;


public class SecurityManagement extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Connection connection;

    private boolean dataSelector = false;

    private boolean processFinished = false;

    private boolean printerConncted = false;

    private Spinner passKeyData, bltMinSecurityData;

    private Switch SMTP_switchState, UDP_switchState, HTTP_switchState, HTTPS_switchState, FTP_switchState, LPD_switchState;

    private String passKey, bltMinSecurity, SMTP_connection, UDP_connection, HTTP_connection, HTTPS_connection, FTP_connection, LPD_connection;

    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String tcpAddressKey = "ZEBRA_DEMO_TCP_ADDRESS";
    private static final String tcpPortKey = "ZEBRA_DEMO_TCP_PORT";
    private static final String PREFS_NAME = "OurSavedAddress";
    private UIHelper helper = new UIHelper(this);

    private String macAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_management);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        Intent intent = getIntent();
        macAddress = intent.getStringExtra("macAddress");

        TextView macAddressEditText = (TextView) this.findViewById(R.id.idMacAdressInput);
        macAddress = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(macAddress);

        //  Set the text fields to the correct IDs
        TextView getDataButton = (TextView) findViewById(R.id.button_get_data);
        TextView setDataButton = (TextView) findViewById(R.id.button_set_data);

        //  Set the switches to the correct IDs
        SMTP_switchState = (Switch) findViewById(R.id.id_SMPT_Input);
        UDP_switchState = (Switch) findViewById(R.id.id_UDP_Input);
        HTTP_switchState = (Switch) findViewById(R.id.id_HTTP_Input);
        HTTPS_switchState = (Switch) findViewById(R.id.id_HTTPS_Input);
        FTP_switchState = (Switch) findViewById(R.id.id_FTP_Input);
        LPD_switchState = (Switch) findViewById(R.id.id_LPD_Input);

        // Spinner elements
        passKeyData = (Spinner) findViewById(R.id.idPassKeyInput);
        bltMinSecurityData = (Spinner) findViewById(R.id.idMinSecurityTextInput);

        // Spinner click listener
        passKeyData.setOnItemSelectedListener(this);
        bltMinSecurityData.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> securityMode_Options = new ArrayList<String>();
        securityMode_Options.add("----");
        securityMode_Options.add("1");
        securityMode_Options.add("2");
        securityMode_Options.add("3");
        securityMode_Options.add("4");

        List<String> bltSecurity_Options = new ArrayList<String>();
        bltSecurity_Options.add("----");
        bltSecurity_Options.add("none");
        bltSecurity_Options.add("unauth_key_encrypt");
        bltSecurity_Options.add("auth_key_encrypt");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter_securityMode = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, securityMode_Options);
        ArrayAdapter<String> dataAdapter_bltSecurity = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bltSecurity_Options);


        // Drop down layout style - list view with radio button
        dataAdapter_securityMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter_bltSecurity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        passKeyData.setAdapter(dataAdapter_securityMode);
        bltMinSecurityData.setAdapter(dataAdapter_bltSecurity);


        helper.dialog(SecurityManagement.this, "Retrieving Data...");

        getDataManagement();

        processingDone();


        /************************************************************************************************************************/
        getDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                if (!printerConncted) {
                    processingDone();
                    Toast.makeText(SecurityManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        passKeyData.setSelection(getIndex(passKeyData, passKey));
                        bltMinSecurityData.setSelection(getIndex(bltMinSecurityData, bltMinSecurity));

                        if (SMTP_connection.equals("on")) {
                            SMTP_switchState.setChecked(true);
                        } else
                            SMTP_switchState.setChecked(false);
                        if (UDP_connection.equals("on")) {
                            UDP_switchState.setChecked(true);
                        } else
                            UDP_switchState.setChecked(false);

                        if (HTTP_connection.equals("on")) {
                            HTTP_switchState.setChecked(true);
                        } else
                            HTTP_switchState.setChecked(false);

                        if (HTTPS_connection.equals("on")) {
                            HTTPS_switchState.setChecked(true);
                        } else
                            HTTPS_switchState.setChecked(false);

                        if (FTP_connection.equals("on")) {
                            FTP_switchState.setChecked(true);
                        } else
                            FTP_switchState.setChecked(false);

                        if (LPD_connection.equals("on")) {
                            LPD_switchState.setChecked(true);
                        } else
                            LPD_switchState.setChecked(false);
                    } catch (NullPointerException e) {
                        processingDone();
                        Toast.makeText(SecurityManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        /************************************************************************************************************************/
        setDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View parent) {

                // to do
                if (printerConncted) {

                    dataSelector = true;
                    processFinished = false;

                    if (SMTP_switchState.isChecked()) {
                        SMTP_switchState.setChecked(true);
                        SMTP_connection = "on";
                    } else {
                        SMTP_switchState.setChecked(false);
                        SMTP_connection = "off";
                    }
                    if (UDP_switchState.isChecked()) {
                        UDP_switchState.setChecked(true);
                        UDP_connection = "on";
                    } else {
                        UDP_switchState.setChecked(false);
                        UDP_connection = "off";
                    }
                    if (HTTP_switchState.isChecked()) {
                        HTTP_switchState.setChecked(true);
                        HTTP_connection = "on";
                    } else {
                        HTTP_switchState.setChecked(false);
                        HTTP_connection = "off";
                    }
                    if (HTTPS_switchState.isChecked()) {
                        HTTPS_switchState.setChecked(true);
                        HTTPS_connection = "on";
                    } else {
                        HTTPS_switchState.setChecked(false);
                        HTTPS_connection = "off";
                    }

                    if (FTP_switchState.isChecked()) {
                        FTP_switchState.setChecked(true);
                        FTP_connection = "on";
                    } else {
                        FTP_switchState.setChecked(false);
                        FTP_connection = "off";
                    }

                    if (LPD_switchState.isChecked()) {
                        LPD_switchState.setChecked(true);
                        LPD_connection = "on";
                    } else {
                        LPD_switchState.setChecked(false);
                        LPD_connection = "off";
                    }

                    //helper.showLoadingDialog("Setting & Saving Data ");
                    helper.dialog(SecurityManagement.this, "Saving Configuration...");
                    getDataManagement();
                    processingDone();
                    if (processFinished == true) {
                        finish();
                    }
                } else {
                    Toast.makeText(SecurityManagement.this, "No Connection.", Toast.LENGTH_SHORT).show();
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
                        connection = helper.getBTLEPrinterConn(macAddress, SecurityManagement.this);
                        connection.open();
                    } catch (ConnectionException ex) {

                        try {
                            connection = helper.getClassicConn(macAddress);
                            connection.open();
                        } catch (ConnectionException cx) {
                            Toast.makeText(SecurityManagement.this, "Error in initial connection.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                    if (printer.getCurrentStatus().isReadyToPrint) {

                        printerConncted = true;

                        try {
                            if (dataSelector == false) {
                                passKey = SGD.GET("bluetooth.minimum_security_mode", connection);   //  Returns the current security level
                                bltMinSecurity = SGD.GET("bluetooth.le.minimum_security", connection); //  Returns the minimum security mode of the device
                                SMTP_connection = SGD.GET("ip.smtp.enable", connection);            //  Returns the current SMTP status
                                UDP_connection = SGD.GET("ip.udp.enable", connection);              //  Returns the current UDP status
                                HTTP_connection = SGD.GET("ip.http.enable", connection);            //  Returns the current http status
                                HTTPS_connection = SGD.GET("ip.https.enable", connection);          //  "ip.https.enable" "value"  values: "on" (default) or "off"
                                FTP_connection = SGD.GET("ip.ftp.enable", connection);              //  Returns the current FTP status
                                LPD_connection = SGD.GET("ip.lpd.enable", connection);              //  Returns the current LPD status

                            } else {
                                SGD.SET("bluetooth.minimum_security_mode", passKey, connection);
                                SGD.SET("bluetooth.le.minimum_security", bltMinSecurity, connection);
                                SGD.SET("ip.smtp.enable", SMTP_connection, connection);                        //  "ip.smtp.enable" "value -> "on" (default) or "off"
                                SGD.SET("ip.udp.enable", UDP_connection, connection);               //  "ip.udp.enable" "value" -> "on" (default) or "off"
                                SGD.SET("ip.http.enable", HTTP_connection, connection);             //  "ip.http.enable" "value" -> "on" (default) or "off"
                                SGD.SET("ip.https.enable", HTTPS_connection, connection);           //  "ip.https.enable" "value" -> "on" (default) or "off"
                                SGD.SET("ip.ftp.enable", FTP_connection, connection);               //  "ip.ftp.enable" "value" -> "on" (default) or "off"
                                SGD.SET("ip.lpd.enable", LPD_connection, connection);               //  "ip.ftp.enable" "value" -> "on" (default) or "off"
                            }

                        } catch (Exception e) {
                            Toast.makeText(SecurityManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (printer.getCurrentStatus().isHeadOpen) {
                        Toast.makeText(SecurityManagement.this, "Please Close Printer Head to Print.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaused) {
                        Toast.makeText(SecurityManagement.this, "Error: Printer Paused.", Toast.LENGTH_SHORT).show();
                    } else if (printer.getCurrentStatus().isPaperOut) {
                        Toast.makeText(SecurityManagement.this, "Please Re-load Media to Print.\"", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SecurityManagement.this, "Please check the Connection of the Printer.", Toast.LENGTH_SHORT).show();
                    }


                    connection.close();
                    processFinished = true;

//                    try {
//                        Thread.sleep(6000);
//                    } catch (Exception e) {
//                    }


                } catch (ConnectionException e) {
                    Toast.makeText(SecurityManagement.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                } catch (ZebraPrinterLanguageUnknownException e) {
                    Toast.makeText(SecurityManagement.this, "Language unknown.", Toast.LENGTH_SHORT).show();


                } finally {
                    try {
                        connection.close();
                    } catch (ConnectionException e) {
                        Toast.makeText(SecurityManagement.this, "Error while closing connection.", Toast.LENGTH_SHORT).show();

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SMTP_switchState.setChecked(false);
                UDP_switchState.setChecked(false);
                HTTP_switchState.setChecked(false);
                HTTPS_switchState.setChecked(false);
                FTP_switchState.setChecked(false);
                LPD_switchState.setChecked(false);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // On selecting a spinner item
        if (parent == passKeyData) {
            passKey = parent.getItemAtPosition(position).toString();
        } else {
            bltMinSecurity = parent.getItemAtPosition(position).toString();
        }
    }
}