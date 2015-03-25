package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.web.WebConsts;
import com.sakurafish.parrot.callconfirm.web.WebFragment;


public class CreditActivity extends ActionBarActivity {

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
        findViewById(R.id.content).setBackgroundColor(Color.WHITE);

        if (savedInstanceState == null) {
            mContent = WebFragment.getInstance(WebConsts.LOCAL_CREDIT, getString(R.string.text_credit));
            getFragmentManager().beginTransaction()
                    .add(R.id.content, mContent)
                    .commit();
        } else {
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, mContent)
                    .addToBackStack("WebFragment").commit();
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
