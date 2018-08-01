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
 *  NOTES: This class creates and displays the recycler view and adapter components for displaying the data for a selected printer.
 *      It is called when the user selects a macAddress in the database_access activity that they wish to view data from.
 *
 *************************************************************************************************************************/


package com.zebra.EAI_BLE_demo.DatabaseClasses;

import com.zebra.EAI_BLE_demo.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;


public class dataActivity_database extends AppCompatActivity {

    public ArrayList<Note> notesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_data_log);

        Intent intent = getIntent();
        String thisMacAddress = intent.getStringExtra("address");

        TextView macAddressEditText = (TextView) this.findViewById(R.id.idMacAdressInput);
        macAddressEditText.setText(thisMacAddress);

        DatabaseHelper db = new DatabaseHelper(this);

        notesList.addAll(db.findCommonNotes(thisMacAddress));

        dataAdapter dataAdapter = new dataAdapter(this, notesList);

        RecyclerView dataRecyclerView = findViewById(R.id.id_dataRecycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        dataRecyclerView.setLayoutManager(mLayoutManager);
        dataRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dataRecyclerView.setAdapter(dataAdapter);

        dataAdapter.notifyDataSetChanged();
    }
}
