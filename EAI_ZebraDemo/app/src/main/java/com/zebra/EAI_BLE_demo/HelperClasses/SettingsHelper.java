/***********************************************
 * CONFIDENTIAL AND PROPRIETARY
 *
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published,
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 *
 * Copyright ZIH Corp. 2012
 *
 * ALL RIGHTS RESERVED
 ***********************************************/

package com.zebra.EAI_BLE_demo.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsHelper {

    private static final String PREFS_NAME = "OurSavedAddress";
    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String tcpAddressKey = "ZEBRA_DEMO_TCP_ADDRESS";
    private static final String tcpPortKey = "ZEBRA_DEMO_TCP_PORT";

    /************************************************************************************************************************/
    public static void saveBluetoothAddress(Context context, String address) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(bluetoothAddressKey, address);
        editor.commit();
    }
}
