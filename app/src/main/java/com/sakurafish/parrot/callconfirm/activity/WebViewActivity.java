package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.web.WebFragment;

import static com.sakurafish.parrot.callconfirm.web.BaseWebFragment.EXTRA_TITLE;
import static com.sakurafish.parrot.callconfirm.web.BaseWebFragment.EXTRA_URL;


public class WebViewActivity extends AppCompatActivity {

    private Fragment mContent;
    private String mUrl;
    private String mTitle;

    public static Intent createIntent(@NonNull final Context context,
                                      @NonNull final Class clazz,
                                      @NonNull final String url,
                                      @Nullable final String title) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_URL, url);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(EXTRA_TITLE, title);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(savedInstanceState);
    }

    private void initialize(final Bundle savedInstanceState) {
        if (getIntent() != null) {
            mUrl = getIntent().getStringExtra(EXTRA_URL);
            if (getIntent().hasExtra(EXTRA_TITLE)) {
                mTitle = getIntent().getStringExtra(EXTRA_TITLE);
            }
        }

        findViewById(R.id.content).setBackgroundColor(Color.WHITE);

        if (savedInstanceState == null) {
            mContent = WebFragment.getInstance(mUrl, mTitle);
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