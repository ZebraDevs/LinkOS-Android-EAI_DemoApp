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


package com.zebra.EAI_BLE_demo._Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.EAI_BLE_demo.HelperClasses.SettingsHelper;
import com.zebra.EAI_BLE_demo.HelperClasses.UIHelper;
import com.zebra.EAI_BLE_demo.ManagementClasses.BatteryManagement;
import com.zebra.EAI_BLE_demo.ManagementClasses.DeviceManagement;
import com.zebra.EAI_BLE_demo.ManagementClasses.MediaManagement;
import com.zebra.EAI_BLE_demo.ManagementClasses.SecurityManagement;
import com.zebra.EAI_BLE_demo.ManagementClasses._QuickSave;

import com.zebra.EAI_BLE_demo.DatabaseClasses.macActivity_database;
import com.zebra.EAI_BLE_demo.R;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class MainActivity extends AppCompatActivity {

    private Connection connection;

    private EditText macAddressEditText;

    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String PREFS_NAME = "OurSavedAddress";
    private UIHelper helper = new UIHelper(this);

    private boolean isConnected = false;

    /************************************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        macAddressEditText = (EditText) this.findViewById(R.id.macInput);
        String mac = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(mac);

        RadioButton btRadioButton = (RadioButton) this.findViewById(R.id.bluetoothRadio);

        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bluetoothRadio) {
                    helper.toggleEditField(macAddressEditText, true);

                } else {
                    helper.toggleEditField(macAddressEditText, false);
                }
            }
        });


        helper.toggleEditField(macAddressEditText, false);

        final TextView PrintConfigButton = (TextView) findViewById(R.id.button_config);
        PrintConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTaskRunner myTask = new AsyncTaskRunner();
                myTask.execute();


            }
        });

        final TextView batteryManagementButton = (TextView) findViewById(R.id.button_battery_management);
        batteryManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSaveSettings();
                Intent intent;
                intent = new Intent(MainActivity.this, BatteryManagement.class);
                startActivity(intent);
                intent.putExtra("macAddress", getMacAddressFieldText());

            }
        });
        final TextView deviceManagementButton = (TextView) findViewById(R.id.button_device_management);
        deviceManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSaveSettings();
                Intent intent;
                intent = new Intent(MainActivity.this, DeviceManagement.class);
                startActivity(intent);
                intent.putExtra("macAddress", getMacAddressFieldText());
            }
        });

        final TextView mediaManagementButton = (TextView) findViewById(R.id.button_media_management);
        mediaManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSaveSettings();
                Intent intent;
                intent = new Intent(MainActivity.this, MediaManagement.class);
                startActivity(intent);
                intent.putExtra("macAddress", getMacAddressFieldText());
            }
        });

        final TextView securityManagementButton = (TextView) findViewById(R.id.button_security_management);
        securityManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSaveSettings();
                Intent intent;
                intent = new Intent(MainActivity.this, SecurityManagement.class);
                startActivity(intent);
                intent.putExtra("macAddress", getMacAddressFieldText());
            }
        });

        final TextView databaseAccessButton = (TextView) findViewById(R.id.button_database_access);
        databaseAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSaveSettings();
                Intent intent;
                intent = new Intent(MainActivity.this, macActivity_database.class);
                startActivity(intent);
                intent.putExtra("macAddress", getMacAddressFieldText());
            }
        });

        final TextView saveAllChanges = (TextView) findViewById(R.id.button_save_data);
        saveAllChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getMacAddressFieldText().equals("")) {

                    Toast.makeText(MainActivity.this, "Connect a printer to save its data.", Toast.LENGTH_SHORT).show();
                } else {
                    getAndSaveSettings();

                    Intent intent;
                    intent = new Intent(MainActivity.this, _QuickSave.class);
                    startActivity(intent);

                }
            }
        });
    }


    /************************************************************************************************************************/
    private void printConfig() {

        try {

            try {
                connection = getBTLEPrinterConn();
                connection.open();
            } catch (ConnectionException ex) {

                try {
                    connection = getClassicConn();
                    connection.open();
                } catch (ConnectionException cx) {
                    Toast.makeText(MainActivity.this, "Error in initial connection.", Toast.LENGTH_SHORT).show();
                }
            }


            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
            getPrinterStatus();


            if (printer.getCurrentStatus().isReadyToPrint) {

                isConnected = true;

                try {


                    printer.printConfigurationLabel();

                } catch (Exception e) {
                    helper.showErrorDialogOnGuiThread(e.getMessage());
                }
            } else if (printer.getCurrentStatus().isHeadOpen) {
                Toast.makeText(MainActivity.this, "Please Close Printer Head to Print.", Toast.LENGTH_SHORT).show();
            } else if (printer.getCurrentStatus().isPaused) {
                Toast.makeText(MainActivity.this, "Error: Printer Paused.", Toast.LENGTH_SHORT).show();
            } else if (printer.getCurrentStatus().isPaperOut) {
                Toast.makeText(MainActivity.this, "Please Re-load Media to Print.\"", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please check the Connection of the Printer.", Toast.LENGTH_SHORT).show();
            }

            connection.close();

        } catch (ConnectionException e) {
            Toast.makeText(MainActivity.this, "Connection Error.", Toast.LENGTH_SHORT).show();
        } catch (ZebraPrinterLanguageUnknownException e) {
            Toast.makeText(MainActivity.this, "Language unknown.", Toast.LENGTH_SHORT).show();

        } finally {

            try {
                connection.close();
            } catch (ConnectionException e) {
                Toast.makeText(MainActivity.this, "Error while closing connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /************************************************************************************************************************
     This method implements the best practices i.e., Checks the language of the printer and set the language of the printer to ZPL.
     ************************************************************************************************************************/
    private void getPrinterStatus() throws ConnectionException {

        final String printerLanguage = SGD.GET("device.languages", connection);

        final String displayPrinterLanguage = "Printer Language is " + printerLanguage;

        SGD.SET("device.languages", "zpl", connection);

    }


    /************************************************************************************************************************/
    private String getMacAddressFieldText() {
        return macAddressEditText.getText().toString();
    }

    /************************************************************************************************************************/
    private void getAndSaveSettings() {
        SettingsHelper.saveBluetoothAddress(MainActivity.this, getMacAddressFieldText());
    }

    /************************************************************************************************************************/
    private Connection getBTLEPrinterConn() {
        return new BluetoothLeConnection(getMacAddressFieldText(), this);
    }

    /************************************************************************************************************************/
    private Connection getClassicConn() {
        return new BluetoothConnection(getMacAddressFieldText());
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
    }

    /************************************************************************************************************************/
    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /************************************************************************************************************************
     SubClass which handles the UI thread.  It prevents the UI thread from continuing before data is collected from the printer,
     yet prevents blocking the UI thread, which allows the saving spinner to be displayed.  AsyncTask was designed for this type
     of implimentation.
     *************************************************************************************************************************/
    public class AsyncTaskRunner extends AsyncTask<String, String, String> {

        /************************************************************************************************************************/
        @Override
        protected void onPreExecute() {
            helper.dialog(MainActivity.this, "Printing Configuration Label...");
        }

        /************************************************************************************************************************/
        @Override
        protected String doInBackground(String... params) {
            printConfig();
            return "";
        }

        /************************************************************************************************************************/
        @Override
        protected void onProgressUpdate(String... text) {
        }

        /************************************************************************************************************************/
        @Override
        protected void onPostExecute(String result) {

            helper.dismissDialog();

            if (isConnected) {
                Toast.makeText(MainActivity.this, "Closing Connection...", Toast.LENGTH_LONG).show();
                isConnected = false;
            } else {
                Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_LONG).show();
            }
        }
    }
}
