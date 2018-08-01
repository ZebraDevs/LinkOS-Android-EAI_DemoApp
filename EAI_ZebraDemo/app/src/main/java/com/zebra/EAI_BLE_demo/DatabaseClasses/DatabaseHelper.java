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
 *  NOTES:  This class supports all SQLite manipulations
 *
 *          Creates the SQLite database
 *          Pulls and displays all saved data
 *          Adds new data
 *          Deletes data
 *          Refines the list of printers
 *          Creates the string list of data used to send as .csv
 *
 *          It is called from the macActivity_database class
 *
 *************************************************************************************************************************/


package com.zebra.EAI_BLE_demo.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "printer_db";

    public String macAddress;
    public String voltage, batteryCharge, status, health, recharge, sleep;
    public String mediaSpeed, ribbon, mediaType, marker, printedLabels, medialength;
    public String friendlyName, btController, model, language, firmwareVersion, linkOs;
    public String securityMode, minimumSecurity, smpt, udp, http, https, ftp, lpd;

    public ArrayList<String> macList, refinedMacList, toCSV;

    private String tableName;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /************************************************************************************************************************
     OverRide method for the SQLiteOpenHelper class which is extended by this class.  CREATE_TABLE is defined in the Note class
     and contains all of the column keys.  CREATE_TABLE defines how many, and what the columns are named, in order to find later
     and manipulate data to be placed in the correct rows.
     *************************************************************************************************************************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);
    }


    /************************************************************************************************************************
     OverRide method for the SQLiteOpenHelper class which is extended by this class.
     *************************************************************************************************************************/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        this.tableName = Note.TABLE_NAME;

        onCreate(db);
    }


    /************************************************************************************************************************
     Creates a new row (new set of data) in the SQLite database.  It contains all of the new information which has been gathered
     from the printer when the user decides to save.  It is called from the createNote method in the macActivity_database.
     *************************************************************************************************************************/
    public long insertNote() {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(Note.COLUMN_NOTE, this.macAddress);

        values.put(Note.COLUMN_VOLTAGE, this.voltage);
        values.put(Note.COLUMN_STATUS, this.status);
        values.put(Note.COLUMN_HEALTH, this.health);
        values.put(Note.COLUMN_RECHARGE, this.recharge);
        values.put(Note.COLUMN_BATTERYCHARGE, this.batteryCharge);
        values.put(Note.COLUMN_SLEEPMODE, this.sleep);

        values.put(Note.COLUMN_MEDIASPEED, this.mediaSpeed);
        values.put(Note.COLUMN_RIBBON, this.ribbon);
        values.put(Note.COLUMN_MEDIATYPE, this.mediaType);
        values.put(Note.COLUMN_MARKER, this.marker);
        values.put(Note.COLUMN_PRINTEDLABELS, this.printedLabels);
        values.put(Note.COLUMN_MEDIALENGTH, this.medialength);

        values.put(Note.COLUMN_FRIENDLYNAME, this.friendlyName);
        values.put(Note.COLUMN_BTCONTROLLER, this.btController);
        values.put(Note.COLUMN_MODEL, this.model);
        values.put(Note.COLUMN_LANGUAGE, this.language);
        values.put(Note.COLUMN_FIRMWAREVERSION, this.firmwareVersion);
        values.put(Note.COLUMN_LINKOSVERSION, this.linkOs);

        values.put(Note.COLUMN_SECURITYMODE, this.securityMode);
        values.put(Note.COLUMN_MINIMUMSECURITY, this.minimumSecurity);
        values.put(Note.COLUMN_SMTP, this.smpt);
        values.put(Note.COLUMN_UDP, this.udp);
        values.put(Note.COLUMN_HTTP, this.http);
        values.put(Note.COLUMN_HTTPS, this.https);
        values.put(Note.COLUMN_FTP, this.ftp);
        values.put(Note.COLUMN_LPD, this.lpd);

        long id = db.insert(Note.TABLE_NAME, null, values);

        macList.add(this.macAddress);

        refineMacList(macList);

        db.close();

        return id;
    }


    /************************************************************************************************************************
     Called from the createNote() method in macActivity_database, this method will find the newly added note in the database
     by its id, create a note out of it, and send it back to the createNote() method where it will be added to the list of data
     to be shown on the screen.
     *************************************************************************************************************************/
    public Note getNote(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,

                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP
                        , Note.COLUMN_VOLTAGE, Note.COLUMN_STATUS, Note.COLUMN_HEALTH, Note.COLUMN_RECHARGE, Note.COLUMN_BATTERYCHARGE, Note.COLUMN_SLEEPMODE
                        , Note.COLUMN_MEDIASPEED, Note.COLUMN_RIBBON, Note.COLUMN_MEDIATYPE, Note.COLUMN_MARKER, Note.COLUMN_PRINTEDLABELS, Note.COLUMN_MEDIALENGTH
                        , Note.COLUMN_FRIENDLYNAME, Note.COLUMN_BTCONTROLLER, Note.COLUMN_MODEL, Note.COLUMN_LANGUAGE, Note.COLUMN_FIRMWAREVERSION, Note.COLUMN_LINKOSVERSION
                        , Note.COLUMN_SECURITYMODE, Note.COLUMN_MINIMUMSECURITY, Note.COLUMN_SMTP, Note.COLUMN_UDP, Note.COLUMN_HTTP, Note.COLUMN_HTTPS, Note.COLUMN_FTP, Note.COLUMN_LPD}, Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)),

                cursor.getString(cursor.getColumnIndex(Note.COLUMN_VOLTAGE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_STATUS)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_HEALTH)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_RECHARGE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_BATTERYCHARGE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_SLEEPMODE)),

                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIASPEED)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_RIBBON)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIATYPE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MARKER)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_PRINTEDLABELS)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIALENGTH)),

                cursor.getString(cursor.getColumnIndex(Note.COLUMN_FRIENDLYNAME)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_BTCONTROLLER)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MODEL)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_LANGUAGE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_FIRMWAREVERSION)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_LINKOSVERSION)),

                cursor.getString(cursor.getColumnIndex(Note.COLUMN_SECURITYMODE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MINIMUMSECURITY)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_SMTP)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_UDP)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_HTTP)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_HTTPS)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_FTP)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_LPD)));

        macList.add(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));

        cursor.close();

        return note;
    }


    /************************************************************************************************************************
     Collects all of the data from the database and puts it into a List to be displayed.  Also conactinates the data into a list
     for exporting to external storage.
     *************************************************************************************************************************/
    public ArrayList<Note> getAllNotes() {

        macList = new ArrayList<>();

        toCSV = new ArrayList<>();

        ArrayList<Note> notes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Note note = new Note();

                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                note.setBatteryVoltage(cursor.getString(cursor.getColumnIndex(Note.COLUMN_VOLTAGE)));
                note.setStatus(cursor.getString(cursor.getColumnIndex(Note.COLUMN_STATUS)));
                note.setHealth(cursor.getString(cursor.getColumnIndex(Note.COLUMN_HEALTH)));
                note.setRecharge(cursor.getString(cursor.getColumnIndex(Note.COLUMN_RECHARGE)));
                note.setBatteryCharge(cursor.getString(cursor.getColumnIndex(Note.COLUMN_BATTERYCHARGE)));
                note.setSleepMode(cursor.getString(cursor.getColumnIndex(Note.COLUMN_SLEEPMODE)));

                note.setMediaSpeed(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIASPEED)));
                note.setRibbon(cursor.getString(cursor.getColumnIndex(Note.COLUMN_RIBBON)));
                note.setMediaType(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIATYPE)));
                note.setMarker(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MARKER)));
                note.setPrintedLabels(cursor.getString(cursor.getColumnIndex(Note.COLUMN_PRINTEDLABELS)));
                note.setMediaLength(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIALENGTH)));

                note.setFriendlyName(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FRIENDLYNAME)));
                note.setBtController(cursor.getString(cursor.getColumnIndex(Note.COLUMN_BTCONTROLLER)));
                note.setModel(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MODEL)));
                note.setLanguage(cursor.getString(cursor.getColumnIndex(Note.COLUMN_LANGUAGE)));
                note.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FIRMWAREVERSION)));
                note.setLinkOs(cursor.getString(cursor.getColumnIndex(Note.COLUMN_LINKOSVERSION)));

                note.setSecurityMode(cursor.getString(cursor.getColumnIndex(Note.COLUMN_SECURITYMODE)));
                note.setMinimumSecurity(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MINIMUMSECURITY)));
                note.setSmtp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_SMTP)));
                note.setUdp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_UDP)));
                note.setHttp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_HTTP)));
                note.setHttps(cursor.getString(cursor.getColumnIndex(Note.COLUMN_HTTPS)));
                note.setFtp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FTP)));
                note.setLpd(cursor.getString(cursor.getColumnIndex(Note.COLUMN_LPD)));

                notes.add(note);

                macList.add(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));

                toCSV.add(note.note + "," + note.timestamp + "," + note.voltage + "," + note.status + "," + note.health + "," + note.recharge + "," +
                        note.batteryCharge + "," + note.sleep + "," + note.mediaSpeed + "," + note.ribbon + "," + note.mediaType + "," + note.marker + "," +
                        note.printedLabels + "," + note.medialength + "," + note.friendlyName + "," + note.btController + "," + note.model + "," +
                        note.language + "," + note.firmwareVersion + "," + note.linkOs + "," + note.securityMode + "," + note.minimumSecurity + "," +
                        note.smpt + "," + note.udp + "," + note.http + "," + note.https + "," + note.ftp + "," + note.lpd);

            } while (cursor.moveToNext());
        }

        db.close();
        refinedMacList = refineMacList(macList);
        return notes;
    }


    /************************************************************************************************************************
     Passed a macAddress from the macActivity_Database class, this method opens the database and deletes all datasets whose
     column, which contains the datasets macadress, matches the selected macAddress.
     *************************************************************************************************************************/
    public void deleteNote(String macToDelete) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("notes", "note = ?", new String[]{macToDelete});
        db.close();
    }


    /************************************************************************************************************************
     Passed a specific mac address when the user selects a macadress from the list of saved printers.  It will take the
     macAddress string and compare it to each macaddress in the database.  If they match, it will place the common datasets into
     a List.  This list will contain only data for that printer, and will be shown to the user on the "Printer Data" activity
     *************************************************************************************************************************/
    public List<Note> findCommonNotes(String scanForThisMac) {

        List<Note> notes = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                if ((cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)).equals(scanForThisMac))) {

                    Note note = new Note();

                    note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                    note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                    note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                    note.setBatteryVoltage(cursor.getString(cursor.getColumnIndex(Note.COLUMN_VOLTAGE)));
                    note.setStatus(cursor.getString(cursor.getColumnIndex(Note.COLUMN_STATUS)));
                    note.setHealth(cursor.getString(cursor.getColumnIndex(Note.COLUMN_HEALTH)));
                    note.setRecharge(cursor.getString(cursor.getColumnIndex(Note.COLUMN_RECHARGE)));
                    note.setBatteryCharge(cursor.getString(cursor.getColumnIndex(Note.COLUMN_BATTERYCHARGE)));
                    note.setSleepMode(cursor.getString(cursor.getColumnIndex(Note.COLUMN_SLEEPMODE)));

                    note.setMediaSpeed(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIASPEED)));
                    note.setRibbon(cursor.getString(cursor.getColumnIndex(Note.COLUMN_RIBBON)));
                    note.setMediaType(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIATYPE)));
                    note.setMarker(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MARKER)));
                    note.setPrintedLabels(cursor.getString(cursor.getColumnIndex(Note.COLUMN_PRINTEDLABELS)));
                    note.setMediaLength(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MEDIALENGTH)));

                    note.setFriendlyName(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FRIENDLYNAME)));
                    note.setBtController(cursor.getString(cursor.getColumnIndex(Note.COLUMN_BTCONTROLLER)));
                    note.setModel(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MODEL)));
                    note.setLanguage(cursor.getString(cursor.getColumnIndex(Note.COLUMN_LANGUAGE)));
                    note.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FIRMWAREVERSION)));
                    note.setLinkOs(cursor.getString(cursor.getColumnIndex(Note.COLUMN_LINKOSVERSION)));

                    note.setSecurityMode(cursor.getString(cursor.getColumnIndex(Note.COLUMN_SECURITYMODE)));
                    note.setMinimumSecurity(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MINIMUMSECURITY)));
                    note.setSmtp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_SMTP)));
                    note.setUdp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_UDP)));
                    note.setHttp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_HTTP)));
                    note.setHttps(cursor.getString(cursor.getColumnIndex(Note.COLUMN_HTTPS)));
                    note.setFtp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_FTP)));
                    note.setLpd(cursor.getString(cursor.getColumnIndex(Note.COLUMN_LPD)));

                    notes.add(note);
                }
            } while (cursor.moveToNext());
        }

        db.close();
        return notes;
    }


    /************************************************************************************************************************
     When a printer is added, this method will use the properties of the hashmap resource to display only one instance of a
     printers macaddress after pressing the "Explore Your Data" button.  This is due to a hashmap only allowing one specific
     key, (mac Addrees in this case) per hashmap, so duplicates are not accepted.
     *************************************************************************************************************************/
    public ArrayList<String> refineMacList(ArrayList macList) {

        Set<String> hs = new HashSet<>();
        hs.addAll(macList);

        macList.clear();
        macList.addAll(hs);

        return macList;
    }


    /************************************************************************************************************************
     When data is collected from the printer it is sent into the setters below.
     Each setter will call this method.

     A "?" is returned by the printer, if the data being pulled doesn't apply to that specific printer
     This method will then check for this condition and return to the setters either the data if valid or N/A if invalid
     *************************************************************************************************************************/
    public String ifApplicableToPrinter(String data) {
        if (data.equals("?")) {
            return "N/A";
        } else {
            return data;
        }
    }

    public void setBatteryVoltage(String voltage) {
        this.voltage = ifApplicableToPrinter(voltage);
    }

    public void setStatus(String status) {
        this.status = ifApplicableToPrinter(status);
    }

    public void setHealth(String health) {
        this.health = ifApplicableToPrinter(health);
    }

    public void setRecharge(String recharge) {
        this.recharge = ifApplicableToPrinter(recharge);
    }

    public void setBatteryCharge(String batteryCharge) {
        this.batteryCharge = ifApplicableToPrinter(batteryCharge);
    }

    public void setSleepMode(String sleep) {
        this.sleep = ifApplicableToPrinter(sleep);
    }

    public void setMediaSpeed(String mediaSpeed) {
        this.mediaSpeed = ifApplicableToPrinter(mediaSpeed);
    }

    public void setRibbon(String ribbon) {
        this.ribbon = ifApplicableToPrinter(ribbon);
    }

    public void setMediaType(String mediaType) {
        this.mediaType = ifApplicableToPrinter(mediaType);
    }

    public void setMarker(String marker) {
        this.marker = ifApplicableToPrinter(marker);
    }

    public void setPrintedLabels(String printedLabels) {
        this.printedLabels = ifApplicableToPrinter(printedLabels);
    }

    public void setMediaLength(String medialength) {
        this.medialength = ifApplicableToPrinter(medialength);
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = ifApplicableToPrinter(friendlyName);
    }

    public void setBtController(String btController) {
        this.btController = ifApplicableToPrinter(btController);
    }

    public void setModel(String model) {
        this.model = ifApplicableToPrinter(model);
    }

    public void setLanguage(String language) {
        this.language = ifApplicableToPrinter(language);
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = ifApplicableToPrinter(firmwareVersion);
    }

    public void setLinkOs(String linkOs) {
        this.linkOs = ifApplicableToPrinter(linkOs);
    }

    public void setSecurityMode(String securityMode) {
        this.securityMode = ifApplicableToPrinter(securityMode);
    }

    public void setMinimumSecurity(String minimumSecurity) {
        this.minimumSecurity = ifApplicableToPrinter(minimumSecurity);
    }

    public void setSmtp(String smpt) {
        this.smpt = ifApplicableToPrinter(smpt);
    }

    public void setUdp(String udp) {
        this.udp = ifApplicableToPrinter(udp);
    }

    public void setHttp(String http) {
        this.http = ifApplicableToPrinter(http);
    }

    public void setHttps(String https) {
        this.https = ifApplicableToPrinter(https);
    }

    public void setLpd(String lpd) {
        this.lpd = ifApplicableToPrinter(lpd);
    }

    public void setFtp(String ftp) {
        this.ftp = ifApplicableToPrinter(ftp);
    }


}









