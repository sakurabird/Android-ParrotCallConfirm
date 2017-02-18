package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.Config;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.dto.AppMessage;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;
import com.sakurafish.parrot.callconfirm.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private Fragment mContent;
    private Context mContext;

    public static Intent createIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        initLayout();

        if (savedInstanceState == null) {
            mContent = MainFragment.getInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.content, mContent)
                    .commit();
        } else {
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, mContent)
                    .addToBackStack("MainFragment").commit();
        }
    }

    private void init() {
        mContext = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        retrieveAppMessage();
    }

    private void initLayout() {
    }

    private void retrieveAppMessage() {
        final String url = getString(R.string.URL_APP_MESSAGE);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = null;
                Request request = new Request.Builder().url(url).get().build();
                OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                } catch (IOException e) {
                    Utils.logError("retrieveAppMessage failed IOException");
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Utils.logError("retrieveAppMessage failed response==null");
                    return;
                }
                Utils.logDebug(result);
                try {
                    Gson gson = new Gson();
                    AppMessage message = gson.fromJson(result, AppMessage.class);
                    showAppMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.logError("Json parse error");
                }
            }
        }.execute();
    }

    private void showAppMessage(AppMessage message) {
        final int lastNo = Pref.getPrefInt(mContext, Config.PREF_APP_MESSAGE_NO);

        for (AppMessage.Data data : message.getData()) {
            int messageNo = data.getMessage_no();
            if (data.getApp().equals("ParrotCallConfirm") && messageNo > lastNo) {
                String msg = Utils.isJapan() ? data.getMessage_jp() : data.getMessage_en();
                Utils.logDebug("no:" + data.getMessage_no() + " message:" + msg);
                new MaterialDialog.Builder(this)
                        .theme(Theme.LIGHT)
                        .title("APP_MESSAGE")
                        .content(msg)
                        .positiveText(getString(android.R.string.ok))
                        .show();

                Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo++);
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    private void finalizeLayout() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeLayout();
        mContent = null;
    }
}
