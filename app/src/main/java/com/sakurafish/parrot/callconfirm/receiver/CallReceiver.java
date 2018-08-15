package com.sakurafish.parrot.callconfirm.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.activity.ConfirmActivity;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils;

import java.util.List;

import static com.sakurafish.parrot.callconfirm.config.Config.PREF_STATE_INVALID_TELNO;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.hasPermissions;

/**
 * 発信を捉える
 * Created by sakura on 2014/12/28.
 */
public class CallReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
            return;

        if (!canProceedConfirm(context, intent)) return;

        startNextActivity(context, intent);
    }

    private boolean canProceedConfirm(final Context context, final Intent intent) {

        // 発信確認が無効の場合処理を行わない
        if (!Pref.getPrefBool(context, context.getString(R.string.PREF_CONFIRM), true)) {
            return false;
        }

        // 必要なパーミッションが許可されていない場合処理を行わない
        if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(context, RuntimePermissionsUtils.PERMISSIONS_MUST)) {
            return false;
        }

        // インコ発信確認アプリがユーザーから確認ダイアログで電話をかけるよう指示された後の発信の場合処理を行わない
        if (Pref.getPrefBool(context, Config.PREF_AFTER_CONFIRM, false)) {
            Pref.setPref(context, Config.PREF_AFTER_CONFIRM, false);
            return false;
        }

        String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        if (TextUtils.isEmpty(phoneNumber)) {

            // 発信確認が出来ない機種の可能性あり。機能を無効にする。
            Pref.setPref(context, context.getString(R.string.PREF_CONFIRM), false);
            Pref.setPref(context, PREF_STATE_INVALID_TELNO, true);

            return false;
        }
        Pref.setPref(context, PREF_STATE_INVALID_TELNO, false);

        return true;
    }

    private void startNextActivity(Context context, Intent intent) {
        //発信確認ダイアログを表示する
        String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);

        if (!Pref.getPrefBool(context, context.getString(R.string.PREF_HEADSET_MODE), false)) {
            callConfirmActivity(context, phoneNumber);

            return;
        }

        // ドライブモードがonの場合、Bluetooth通信中は処理を行わない
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            callConfirmActivity(context, phoneNumber);
            return;
        }

        BluetoothProfile.ServiceListener profileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile bluetoothprofile) {
                if (profile == BluetoothProfile.HEADSET) {
                    List list = bluetoothprofile.getConnectedDevices();
                    if (list.isEmpty()) {
                        callConfirmActivity(context, phoneNumber);
                    }
                }
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothprofile);
            }

            public void onServiceDisconnected(int profile) {
            }
        };
        bluetoothAdapter.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET);
    }

    private void callConfirmActivity(Context context, String phoneNumber) {
        // 発信を取り消す
        setResultData(null);
        abortBroadcast();
        context.startActivity(ConfirmActivity.createIntent(context, ConfirmActivity.class, phoneNumber));
    }
}