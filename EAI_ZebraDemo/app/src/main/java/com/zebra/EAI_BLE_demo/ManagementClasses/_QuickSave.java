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
 *  NOTES:  Extends the macActivity_database class which allows access to all of the methods, and objects the parent class
 *          has, and has created.
 *
 *          Prevents creating new objects with null values and reuses methods.
 *
 *************************************************************************************************************************/

package com.zebra.EAI_BLE_demo.ManagementClasses;

import android.os.Bundle;

import com.zebra.EAI_BLE_demo.R;
import com.zebra.EAI_BLE_demo.DatabaseClasses.macActivity_database;

public class _QuickSave extends macActivity_database {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTask = new AsyncTaskRunner();


        if (!this.macAddress.equals("")) {
            fromQuickSave = true;
            myTask.execute();
        }
    }
}