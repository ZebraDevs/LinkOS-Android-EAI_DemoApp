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
 *  NOTES:   Contains all of the attributes that each dataset will contian a dataset is refered to as a Note
 *
 *************************************************************************************************************************/


package com.zebra.EAI_BLE_demo.DatabaseClasses;


public class Note {


    public static final String TABLE_NAME = "notes", COLUMN_ID = "id", COLUMN_NOTE = "note", COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_VOLTAGE = "voltage", COLUMN_STATUS = "status", COLUMN_HEALTH = "health",
            COLUMN_RECHARGE = "recharge", COLUMN_BATTERYCHARGE = "batteryCharge", COLUMN_SLEEPMODE = "sleep";
    public static final String COLUMN_MEDIASPEED = "mediaSpeed", COLUMN_RIBBON = "ribbon", COLUMN_MEDIATYPE = "mediaType",
            COLUMN_MARKER = "marker", COLUMN_PRINTEDLABELS = "printedLabels", COLUMN_MEDIALENGTH = "medialength";
    public static final String COLUMN_FRIENDLYNAME = "friendlyName", COLUMN_BTCONTROLLER = "btController", COLUMN_MODEL = "model",
            COLUMN_LANGUAGE = "language", COLUMN_FIRMWAREVERSION = "firmwareVersion", COLUMN_LINKOSVERSION = "linkOs";
    public static final String COLUMN_SECURITYMODE = "securitymode", COLUMN_MINIMUMSECURITY = "minimumSecurity", COLUMN_SMTP = "smpt",
            COLUMN_UDP = "udp", COLUMN_HTTP = "http", COLUMN_HTTPS = "https", COLUMN_FTP = "ftp", COLUMN_LPD = "lpd";


    public int id;
    public String note;
    public String timestamp;
    public String voltage, batteryCharge, status, health, recharge, sleep;
    public String mediaSpeed, ribbon, mediaType, marker, printedLabels, medialength;
    public String friendlyName, btController, model, language, firmwareVersion, linkOs;
    public String securityMode, minimumSecurity, smpt, udp, http, https, ftp, lpd;


    /************************************************************************************************************************
     Identifying the columns which the database will contain.
     *************************************************************************************************************************/
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOTE + " TEXT, "
            + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "

            + COLUMN_VOLTAGE + " TEXT, "
            + COLUMN_STATUS + " TEXT, "
            + COLUMN_HEALTH + " TEXT,"
            + COLUMN_RECHARGE + " TEXT, "
            + COLUMN_BATTERYCHARGE + " TEXT, "
            + COLUMN_SLEEPMODE + " TEXT, "

            + COLUMN_MEDIASPEED + " TEXT, "
            + COLUMN_RIBBON + " TEXT, "
            + COLUMN_MEDIATYPE + " TEXT, "
            + COLUMN_MARKER + " TEXT, "
            + COLUMN_PRINTEDLABELS + " TEXT, "
            + COLUMN_MEDIALENGTH + " TEXT, "

            + COLUMN_FRIENDLYNAME + " TEXT, "
            + COLUMN_BTCONTROLLER + " TEXT, "
            + COLUMN_MODEL + " TEXT, "
            + COLUMN_LANGUAGE + " TEXT, "
            + COLUMN_FIRMWAREVERSION + " TEXT, "
            + COLUMN_LINKOSVERSION + " TEXT, "

            + COLUMN_SECURITYMODE + " TEXT, "
            + COLUMN_MINIMUMSECURITY + " TEXT, "
            + COLUMN_SMTP + " TEXT, "
            + COLUMN_UDP + " TEXT, "
            + COLUMN_HTTP + " TEXT, "
            + COLUMN_HTTPS + " TEXT, "
            + COLUMN_FTP + " TEXT, "
            + COLUMN_LPD + " TEXT "
            + ")";


    /************************************************************************************************************************
     Empty contstructor to access methods in this class
     *************************************************************************************************************************/
    public Note() {
    }


    /************************************************************************************************************************
     Constructor to create a dataset ( "note" )
     *************************************************************************************************************************/
    public Note(int id, String note, String timestamp
            , String voltage, String status, String health, String recharge, String batteryCharge, String sleep
            , String mediaSpeed, String ribbon, String mediaType, String marker, String printedLabels, String medialength
            , String friendlyName, String btController, String model, String language, String firmwareVersion, String linkOs
            , String securityMode, String minimumSecurity, String smpt, String udp, String http, String https, String ftp, String lpd) {

        this.id = id;
        this.note = note;
        this.timestamp = timestamp;

        this.voltage = voltage;
        this.batteryCharge = batteryCharge;
        this.status = status;
        this.health = health;
        this.recharge = recharge;
        this.sleep = sleep;

        this.mediaSpeed = mediaSpeed;
        this.ribbon = ribbon;
        this.mediaType = mediaType;
        this.marker = marker;
        this.printedLabels = printedLabels;
        this.medialength = medialength;

        this.friendlyName = friendlyName;
        this.btController = btController;
        this.model = model;
        this.language = language;
        this.firmwareVersion = firmwareVersion;
        this.linkOs = linkOs;

        this.securityMode = securityMode;
        this.minimumSecurity = minimumSecurity;
        this.smpt = smpt;
        this.udp = udp;
        this.http = http;
        this.https = https;
        this.ftp = ftp;
        this.lpd = lpd;
    }


    /************************************************************************************************************************
     Setters and getters used to temporarily store data from the printer, until is is retrieved and placed into the database
     *************************************************************************************************************************/
    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public void setBatteryVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getBatteryVoltage() {
        return voltage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getHealth() {
        return health;
    }

    public void setRecharge(String recharge) {
        this.recharge = recharge;
    }

    public String getRecharge() {
        return recharge;
    }

    public void setBatteryCharge(String batteryCharge) {
        this.batteryCharge = batteryCharge;
    }

    public String getBatteryCharge() {
        return batteryCharge;
    }

    public void setSleepMode(String sleep) {
        this.sleep = sleep;
    }

    public String getSleepMode() {
        return sleep;
    }


    public void setMediaSpeed(String mediaSpeed) {
        this.mediaSpeed = mediaSpeed;
    }

    public String getMediaSpeed() {
        return mediaSpeed;
    }

    public void setRibbon(String ribbon) {
        this.ribbon = ribbon;
    }

    public String getRibbon() {
        return ribbon;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return marker;
    }

    public void setPrintedLabels(String printedLabels) {
        this.printedLabels = printedLabels;
    }

    public String getPrintedLabels() {
        return printedLabels;
    }

    public void setMediaLength(String medialength) {
        this.medialength = medialength;
    }

    public String getMediaLength() {
        return medialength;
    }


    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setBtController(String btController) {
        this.btController = btController;
    }

    public String getBtController() {
        return btController;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setLinkOs(String linkOs) {
        this.linkOs = linkOs;
    }

    public String getLinkOs() {
        return linkOs;
    }


    public void setSecurityMode(String securityMode) {
        this.securityMode = securityMode;
    }

    public String getSecurityMode() {
        return securityMode;
    }

    public void setMinimumSecurity(String minimumSecurity) {
        this.minimumSecurity = minimumSecurity;
    }

    public String getMinimumSecurity() {
        return minimumSecurity;
    }

    public void setSmtp(String smpt) {
        this.smpt = smpt;
    }

    public String getSmtp() {
        return smpt;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getUdp() {
        return udp;
    }

    public void setHttp(String http) {
        this.http = http;
    }

    public String getHttp() {
        return http;
    }

    public void setHttps(String https) {
        this.https = https;
    }

    public String getHttps() {
        return https;
    }

    public void setLpd(String lpd) {
        this.lpd = lpd;
    }

    public String getLpd() {
        return lpd;
    }

    public void setFtp(String ftp) {
        this.ftp = ftp;
    }

    public String getFtp() {
        return ftp;
    }

}

