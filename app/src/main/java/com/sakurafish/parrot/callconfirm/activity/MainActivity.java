package com.sakurafish.parrot.callconfirm.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {

    private Fragment mContent;

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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initLayout() {
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
