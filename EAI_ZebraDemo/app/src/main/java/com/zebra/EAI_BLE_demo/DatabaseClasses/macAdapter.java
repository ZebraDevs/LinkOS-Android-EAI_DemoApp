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
 *  NOTES:  Contains the macAdapter elements and over-ride methods to display the macAddresses in the recycler view.
 *
 *************************************************************************************************************************/


package com.zebra.EAI_BLE_demo.DatabaseClasses;

import com.zebra.EAI_BLE_demo.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class macAdapter extends RecyclerView.Adapter<macAdapter.MyViewHolder> {

    private Context context;
    public ArrayList<String> macList;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mac;

        public MyViewHolder(View view) {
            super(view);
            mac = view.findViewById(R.id.id_listOfMacs);
        }
    }

    /*************************************************************************************************************************/
    public macAdapter(Context context, ArrayList<String> macList) {
        this.context = context;
        this.macList = macList;
    }


    /*************************************************************************************************************************/
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mac.setText(macList.get(position));
    }


    /*************************************************************************************************************************/
    @Override
    public int getItemCount() {
        return macList.size();
    }


    /*************************************************************************************************************************/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.database_access_layout, parent, false);
        return new MyViewHolder(itemView);
    }


}

