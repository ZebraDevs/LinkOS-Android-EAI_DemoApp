/*************************************************************************************************************************
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
 *
 *  NOTES: This class is called when the user chooses to explore their database.  It creates the buttons, recycler view, and
 *      adapter. It controls retrieving new data to be saved, creating a .csv file to send to external storage, and controls
 *      sending the .csv file via email, or bluetooth.
 *
 *************************************************************************************************************************/


package com.zebra.EAI_BLE_demo.DatabaseClasses;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.zebra.EAI_BLE_demo._Main.MainActivity;
import com.zebra.EAI_BLE_demo.R;
import com.zebra.EAI_BLE_demo.HelperClasses.UIHelper;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.FileOutputStream;

import static android.os.Environment.getExternalStorageDirectory;

public class macActivity_database extends AppCompatActivity {

    private Connection connection;

    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String PREFS_NAME = "OurSavedAddress";
    public UIHelper helper = new UIHelper(this);

    private String filepath;
    private String newFileName = "data.csv";
    public String macAddress = "";

    public File folder;
    private DatabaseHelper db;
    private com.zebra.EAI_BLE_demo.DatabaseClasses.macAdapter macAdapter;

    public ArrayList<Note> notesList = new ArrayList<>();
    private ArrayList<String> macList = new ArrayList<String>();

    public AsyncTaskRunner myTask;

    public boolean fromQuickSave = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_access);

        getPermissions(macActivity_database.this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        Intent intent = getIntent();
        db = new DatabaseHelper(this);


        macAddress = intent.getStringExtra("macAddress");
        TextView macAddressEditText = this.findViewById(R.id.idMacAdressInput);
        macAddress = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(macAddress);


        notesList.addAll(db.getAllNotes());
        macList = db.refinedMacList;


        macAdapter = new macAdapter(this, macList);

        RecyclerView macRecyclerView = findViewById(R.id.id_macsRecycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        macRecyclerView.setLayoutManager(mLayoutManager);
        macRecyclerView.setItemAnimator(new DefaultItemAnimator());
        macRecyclerView.setAdapter(macAdapter);

        /************************************************************************************************************************
         The "Add Printer" button can be found in the database_access activity
         A new AsyncTaskRunner(); object must be created everytime it is used
         Checks to ensure their was a MAC Address added on the home screen
         Yes --> showNoteDialog() method called --> proceeds to store the printer to the list and data to the SQLite database
         NO --> Toast displayed, process cancelled
         *************************************************************************************************************************/
        final Button addPrinter = (Button) findViewById(R.id.id_add_printer);
        addPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myTask = new AsyncTaskRunner();


                if (!macAddress.equals("")) {

                    showNoteDialog(false, null, -1);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                } else {
                    Toast.makeText(macActivity_database.this, "Connect a printer first.", Toast.LENGTH_LONG).show();
                }
            }
        });


        /************************************************************************************************************************
         The "Save As .CSV" button can be found in the databse_access activity

         Checks to ensure that the list of printers, macList,  is not empty (that data has been stored to the SQLite database)
         YES --> writeToExternalFile() method is called -->  proceeds to write the SQLite database to an external file
         NO -->  Toast Displayed, process cancelled

         Having a MAC Address entered in the home screen is never required in order to store the internal data to an external file
         *************************************************************************************************************************/
        final Button saveCSV = (Button) findViewById(R.id.saveCSVFile);
        saveCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (macList.size() == 0) {
                    Toast.makeText(macActivity_database.this, "No data exists to be stored.", Toast.LENGTH_LONG).show();
                } else {
                    writeToExternalFile();
                    Toast.makeText(macActivity_database.this, "Saved to folder EAI_BLE_DEMO on this device.", Toast.LENGTH_LONG).show();
                }

            }
        });


        /************************************************************************************************************************
         The Icon for sharing can be found in the databse_access activity

         writeToExternalFile() method is called to ensure all recently changed configurations gets saved to the internal database before
         exporting it to the external file, and finally what we see below --> sending it out to email or via bluetooth.

         Creates a new File object with the path to the external file to be sent
         Creates a new intent and displays the icons, of the modes of sharing, on the android screen
         *************************************************************************************************************************/
        final ImageButton sendCSV = (ImageButton) findViewById(R.id.sendCSVFile);
        sendCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subject = "Printer Data CSV File";

                writeToExternalFile();

                File file = new File(getFilePath());
                Uri u1;
                u1 = Uri.fromFile(file);

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
                sendIntent.setType("text/html");
                startActivity(Intent.createChooser(sendIntent, "Share using:"));
            }
        });


        /************************************************************************************************************************
         A new entriy will be added to the recyclerview list for every new printer added to the SQLite database
         Every new printer will become a button, because it is a clickable list

         Button short click --> Directed to the database_data_log activity displaying the printers data
         Button long click --> showActionsDialog(position) method is passed with the position of the element which has been clicked
         --> displays the action dialog box presenting two options:  Delete, or Cancel
         *************************************************************************************************************************/
        macRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, macRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String address = macList.get(position);

                Intent intent;
                intent = new Intent(macActivity_database.this, dataActivity_database.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

                showActionsDialog(position);
            }
        }));
    }


    /************************************************************************************************************************
     After selecting to add data to the database, and confirming the selection "Save" from the popup window, this method will
     be called from the showNoteDialog() method, in this class.  It will insert the data collected from the printer into the
     database ( via insertNote() ) and return the position in the database it was stored.  The position is used to relocate
     the information, and return a note which will be added to the noteList.   The list of data displayed on the screen is
     updated by calling --> mAdapter.notifyDataSetChanged();

     data will be added into the list into the first index, this is because it will show the cronological order of data added,
     newest ---> top    to    Oldest ---> bottom
     *************************************************************************************************************************/
    public void createNote() {

        long id = db.insertNote();

        Note n = db.getNote(id);

        notesList.add(0, n);

        macList = db.refineMacList(macList);

        macAdapter.notifyDataSetChanged();

    }

    /************************************************************************************************************************
     * Called only by the showActionsDialog(final int position) method
     * Is passed the position in the list of the data to be deleted.
     * Deletes note from SQLite by calling into the DataBaseHelper Class
     * and removing the item from the visible list by its position
     *************************************************************************************************************************/
    private void deleteNote(int position) {

        String macToDelete = macList.get(position);

        db.deleteNote(macToDelete);

        macList.remove(position);

        db.refinedMacList = macList;

        macAdapter.notifyItemRemoved(position);

    }

    /************************************************************************************************************************
     Opens dialog with Edit - Delete options --> Edit - 0 --> Delete - 0
     *************************************************************************************************************************/
    private void showActionsDialog(final int position) {

        CharSequence colors[] = new CharSequence[]{"                         Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("  Delete this printer and its data?");


        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteNote(position);
            }
        });

        builder.show();
    }


    /*************************************************************************************************************************
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     *************************************************************************************************************************/
    public void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.database_note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);

        alertDialogBuilderUserInput.setView(view);

        TextView inputNote = view.findViewById(R.id.note);

        inputNote.setText(this.macAddress);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setView(view)

                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                helper.dismissLoadingDialog();
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();

        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (macAddress == null) {
                    Toast.makeText(macActivity_database.this, "Need to connect a printer to save data.", Toast.LENGTH_LONG).show();

                    Intent intent;
                    intent = new Intent(macActivity_database.this, MainActivity.class);
                    startActivity(intent);

                } else {

                    alertDialog.dismiss();
                    myTask.execute();

                }
            }
        });
    }


    /************************************************************************************************************************
     Called when the user wants to add a printer or data to the SQLite database.  Creates a new thread to handle accessing
     the printers data.  The GUI thread will not continue until this thread is complete, because we MUST collect all of the data
     before trying to store the data into the SQLite database.  It accepts to different forms of bluetooth with the printer.
     It will first try to connect via BTLE.  If no connection is found, then it will try to connect via classic mode.
     *************************************************************************************************************************/
    public void getDataManagement() {

        try {
            try {
                connection = helper.getBTLEPrinterConn(macAddress, macActivity_database.this);
                connection.open();
            } catch (ConnectionException ex) {

                try {
                    connection = helper.getClassicConn(macAddress);
                    connection.open();

                } catch (ConnectionException cx) {
                    Toast.makeText(macActivity_database.this, " Error", Toast.LENGTH_SHORT).show();
                }
            }

            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

            if (printer.getCurrentStatus().isReadyToPrint) {
                try {


                    db.macAddress = this.macAddress;

                    db.setBatteryVoltage(SGD.GET("power.voltage", connection));
                    db.setStatus(SGD.GET("power.status", connection));
                    db.setHealth(SGD.GET("power.health", connection));
                    db.setRecharge(SGD.GET("power.cycle_count", connection));
                    db.setBatteryCharge(SGD.GET("power.percent_full", connection));
                    db.setSleepMode(SGD.GET("power.sleep.enable", connection));

                    db.setMediaSpeed(SGD.GET("media.speed", connection));
                    db.setRibbon(SGD.GET("ezpl.print_method", connection));
                    db.setMediaType(SGD.GET("media.type", connection));
                    db.setMarker(SGD.GET("odometer.media_marker_count", connection));
                    db.setPrintedLabels(SGD.GET("odometer.total_label_count", connection));
                    db.setMediaLength(SGD.GET("odometer.total_print_length", connection));

                    db.setFriendlyName(SGD.GET("device.friendly_name", connection));
                    db.setBtController(SGD.GET("bluetooth.le.controller_mode", connection));
                    db.setModel(SGD.GET("device.product_name", connection));
                    db.setLanguage(SGD.GET("device.languages", connection));
                    db.setFirmwareVersion(SGD.GET("appl.name", connection));
                    db.setLinkOs(SGD.GET("appl.link_os_version", connection));

                    db.setSecurityMode(SGD.GET("bluetooth.minimum_security_mode", connection));
                    db.setMinimumSecurity(SGD.GET("bluetooth.le.minimum_security", connection));
                    db.setSmtp(SGD.GET("ip.smtp.enable", connection));
                    db.setUdp(SGD.GET("ip.udp.enable", connection));
                    db.setHttp(SGD.GET("ip.http.enable", connection));
                    db.setHttps(SGD.GET("ip.https.enable", connection));
                    db.setFtp(SGD.GET("ip.ftp.enable", connection));
                    db.setLpd(SGD.GET("ip.lpd.enable", connection));


                } catch (Exception e) {
                    Toast.makeText(macActivity_database.this, "Connection Error.", Toast.LENGTH_SHORT).show();
                }

            } else if (printer.getCurrentStatus().isHeadOpen) {
                Toast.makeText(macActivity_database.this, "Please Close Printer Head to Print.", Toast.LENGTH_SHORT).show();
            } else if (printer.getCurrentStatus().isPaused) {
                Toast.makeText(macActivity_database.this, "Error: Printer Paused.", Toast.LENGTH_SHORT).show();
            } else if (printer.getCurrentStatus().isPaperOut) {
                Toast.makeText(macActivity_database.this, "Please Re-load Media to Print.\"", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(macActivity_database.this, "Please check the Connection of the Printer.", Toast.LENGTH_SHORT).show();
            }

            connection.close();


        } catch (ConnectionException e) {
            Toast.makeText(macActivity_database.this, "Connection Error.", Toast.LENGTH_SHORT).show();
        } catch (ZebraPrinterLanguageUnknownException e) {
            Toast.makeText(macActivity_database.this, "Language unknown.", Toast.LENGTH_SHORT).show();
        } finally {

            try {
                connection.close();
            } catch (ConnectionException e) {
                Toast.makeText(macActivity_database.this, "Error while closing connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /************************************************************************************************************************
     Retrieves the string list of data that was created when the getAllData() method, in the dataBaseHelper class.  Converting
     the data into this comma seperated string list is an easy way to push all of the saved data to an external file (.csv).

     Creates the file name and folder name

     If the folder doesnt exist it will create it.

     Assigning false to the second argument of the fos object will allow us to write over the old data in this file with the new file
     This mitigates excessive file buildup in the android device.
     *************************************************************************************************************************/
    private void writeToExternalFile() {

        db.getAllNotes();

        ArrayList<String> data;
        data = db.toCSV;

        String folderName = "EAI_BLE_DEMO";
        folder = new File(getExternalStorageDirectory(), folderName);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        filepath = folder.getAbsolutePath() + "/" + newFileName;
        setFilePath(filepath);

        FileOutputStream fos;

        try {
            fos = new FileOutputStream(filepath, false);

            FileWriter fWriter;

            try {
                fWriter = new FileWriter(fos.getFD());

                for (String line : data) {
                    fWriter.write(line + "\n\n");
                }

                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************************************************************************************************************************
     Getter and setter methods for the string containing the file path
     Used when the share data icon is selected
     *************************************************************************************************************************/
    private void setFilePath(String file) {
        filepath = file;
    }

    private String getFilePath() {
        return filepath;
    }


    /************************************************************************************************************************
     Launches the Andoird dialog to request the read/write permissions, required as of Android 6.0.
     *************************************************************************************************************************/
    private static void getPermissions(Activity activity) {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /************************************************************************************************************************
     A subClass which handles the UI thread.  It prevents the UI thread from continuing before data is collected from the printer,
     yet prevents blocking the UI thread, which allows the "saving spinner" to be displayed.  AsyncTask was designed for this type
     of implimentation, but requires a new object of its class to be created everytime it is desired to be used.  Similar to
     a thread, for the the exact same thread can never be reused.

     myTask.excecute(); will set off this class and must be called from the main GUI thread
     Once called ^  the class will exucute all processes within automatically

     When finished, it will return control to the main GUI thread.

     Called from the Quick Save button on the home screen and from the "Add printer data" button.
     *************************************************************************************************************************/
    public class AsyncTaskRunner extends AsyncTask<String, String, String> {


        /************************************************************************************************************************
         On pre-exicute it will decide where it was called from and display an appropriate message
         Generally this method is used as below to display some sort of dialog to the GUI thread
         *************************************************************************************************************************/
        @Override
        protected void onPreExecute() {
            if (fromQuickSave) {
                helper.dialog(macActivity_database.this, "Saving to internal database...");
            } else {
                helper.dialog(macActivity_database.this, "Adding printer's data to internal storage...");
            }
        }


        /************************************************************************************************************************
         The process of retrieving the data from the printer will be done in the background --> getDataManagement();

         getDataManagement(); --> Make connection --> get and set data in the DataBaseHelper class --> close connection
         *************************************************************************************************************************/
        @Override
        protected String doInBackground(String... params) {
            getDataManagement();
            return "";
        }


        /************************************************************************************************************************/
        @Override
        protected void onProgressUpdate(String... text) {
            // Not Utalized
        }


        /************************************************************************************************************************
         When the background task is complete, the program will return here

         Calls the createNote(); method --> calls db.insertNote which creates a new Note and --> stores it in SQLite database
         --> exits db.insertNote returning the location to createNote() method

         Upon return it will dismiss the saving screen
         If it wwas called from quick save it will display a Toast to the user showing the completion
         *************************************************************************************************************************/
        @Override
        protected void onPostExecute(String result) {
            createNote();
            helper.dismissDialog();

            if (fromQuickSave) {
                fromQuickSave = false;
                Toast.makeText(macActivity_database.this, "Quick save complete.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}