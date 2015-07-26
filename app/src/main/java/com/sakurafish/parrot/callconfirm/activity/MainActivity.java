package com.sakurafish.parrot.callconfirm.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;


public class MainActivity extends Activity {

    private ListView mDrawerList;
    private Fragment mContent;

    public static Intent createIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        // TODO
//        mDrawerList.setAdapter(new NavExpandableAdapter(mContext));
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    private void finalizeLayout() {
        mDrawerList = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeLayout();
        mContent = null;
    }
}
