package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;


public class MainActivity extends ActionBarActivity {

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

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContent = null;
    }
}
