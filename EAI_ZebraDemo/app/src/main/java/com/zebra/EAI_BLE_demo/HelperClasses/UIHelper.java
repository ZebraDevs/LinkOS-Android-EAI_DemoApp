/**
 * *******************************************
 * CONFIDENTIAL AND PROPRIETARY
 * <p/>
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published,
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * <p/>
 * Copyright ZIH Corp. 2010
 * <p/>
 * ALL RIGHTS RESERVED
 * *********************************************
 */
package com.zebra.EAI_BLE_demo.HelperClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;

import com.zebra.EAI_BLE_demo.R;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;


public class UIHelper {

    private ProgressDialog loadingDialog;
    private Activity activity;

    private ProgressDialog csprogress;

    public UIHelper(Activity activity) {
        this.activity = activity;
    }


    /************************************************************************************************************************/
    public void dismissLoadingDialog() {
        if (activity != null && loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /************************************************************************************************************************/
    public void showErrorDialogOnGuiThread(final String errorMessage) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(activity).setMessage(errorMessage).setTitle("Error").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            dismissLoadingDialog();
                        }
                    }).create().show();
                }
            });
        }
    }

    /************************************************************************************************************************/
    public void toggleEditField(EditText editText, boolean set) {
        editText.setEnabled(set);
        editText.setFocusable(set);
        editText.setFocusableInTouchMode(set);
    }

    /************************************************************************************************************************/
    public void dialog(Context context, String message) {
        csprogress = new ProgressDialog(context);
        csprogress.setMessage(message);
        csprogress.show();
        csprogress.setCancelable(false);
    }


    /************************************************************************************************************************/
    public void dismissDialog() {
        csprogress.dismiss();
    }


    /************************************************************************************************************************/
    public Connection getBTLEPrinterConn(String macAddress, Context context) {
        return new BluetoothLeConnection(macAddress, context);
    }


    /************************************************************************************************************************/
    public Connection getClassicConn(String macAddress) {
        return new BluetoothConnection(macAddress);
    }
}
