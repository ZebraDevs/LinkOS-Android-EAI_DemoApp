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
 *  NOTES:  Contains the dataAdapter view elements and over-ride methods to display the data in the recycler view.
 *
 *************************************************************************************************************************/


package com.zebra.EAI_BLE_demo.DatabaseClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zebra.EAI_BLE_demo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class dataAdapter extends RecyclerView.Adapter<dataAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Note> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView timestamp, mac, voltage, status, health, recharge, batteryCharge, sleepMode,
                mediaSpeed, ribbon, mediaType, marker, printedLabels, mediaLength,
                friendlyName, btController, model, language, firmwareVersion, linkOs,
                securityMode, minimumSecurity, smtp, udp, http, https, ftp, lpd;


        /************************************************************************************************************************/
        public MyViewHolder(View view) {

            super(view);

            timestamp = view.findViewById(R.id.timestamp);
            mac = view.findViewById(R.id.id_mac);

            voltage = view.findViewById(R.id.id_voltage);
            status = view.findViewById(R.id.id_status);
            health = view.findViewById(R.id.id_health);
            recharge = view.findViewById(R.id.id_recharge);
            batteryCharge = view.findViewById(R.id.id_batteryCharge);
            sleepMode = view.findViewById(R.id.id_sleepMode);

            mediaSpeed = view.findViewById(R.id.id_speed);
            ribbon = view.findViewById(R.id.id_ribbon);
            mediaType = view.findViewById(R.id.id_type);
            marker = view.findViewById(R.id.id_marker);
            printedLabels = view.findViewById(R.id.id_labels);
            mediaLength = view.findViewById(R.id.id_length);

            friendlyName = view.findViewById(R.id.id_friendlyName);
            btController = view.findViewById(R.id.id_controller);
            model = view.findViewById(R.id.id_model);
            language = view.findViewById(R.id.id_language);
            firmwareVersion = view.findViewById(R.id.id_firmVersion);
            linkOs = view.findViewById(R.id.id_osVersion);

            securityMode = view.findViewById(R.id.id_securityMode);
            minimumSecurity = view.findViewById(R.id.id_minSecurity);
            smtp = view.findViewById(R.id.id_smpt);
            udp = view.findViewById(R.id.id_udp);
            http = view.findViewById(R.id.id_http);
            https = view.findViewById(R.id.id_https);
            ftp = view.findViewById(R.id.id_ftp);
            lpd = view.findViewById(R.id.id_lpd);
        }
    }


    /************************************************************************************************************************/
    @Override
    public int getItemCount() {
        return notesList.size();
    }


    /************************************************************************************************************************/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.database_data_log_layout, parent, false);

        return new MyViewHolder(itemView);
    }


    /************************************************************************************************************************/
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Note note = notesList.get(position);

        holder.mac.setText(note.getNote());
        holder.timestamp.setText(formatDate(note.getTimestamp()));

        holder.voltage.setText(note.getBatteryVoltage());
        holder.batteryCharge.setText(note.getBatteryCharge());
        holder.status.setText(note.getStatus());
        holder.health.setText(note.getHealth());
        holder.recharge.setText(note.getRecharge());
        holder.sleepMode.setText(note.getSleepMode());

        holder.mediaSpeed.setText(note.getMediaSpeed());
        holder.ribbon.setText(note.getRibbon());
        holder.mediaType.setText(note.getMediaType());
        holder.marker.setText(note.getMarker());
        holder.printedLabels.setText(note.getPrintedLabels());
        holder.mediaLength.setText(note.getMediaLength());

        holder.friendlyName.setText(note.getFriendlyName());
        holder.btController.setText(note.getBtController());
        holder.model.setText(note.getModel());
        holder.language.setText(note.getLanguage());
        holder.firmwareVersion.setText(note.getFirmwareVersion());
        holder.linkOs.setText(note.getLinkOs());

        holder.securityMode.setText(note.getSecurityMode());
        holder.minimumSecurity.setText(note.getMinimumSecurity());
        holder.udp.setText(note.getUdp());
        holder.smtp.setText(note.getSmtp());
        holder.http.setText(note.getHttp());
        holder.https.setText(note.getHttps());
        holder.ftp.setText(note.getFtp());
        holder.lpd.setText(note.getLpd());
    }


    /************************************************************************************************************************/
    public dataAdapter(Context context, ArrayList<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }


    /***********************************************************************************************
     * Formatting timestamp to `MMM d` format --> Input: 2018-02-21 00:15:42 --> Output: Feb 21
     **********************************************************************************************/
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            //SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {
        }

        return "";
    }
}

