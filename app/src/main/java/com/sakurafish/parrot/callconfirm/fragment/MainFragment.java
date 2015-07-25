package com.sakurafish.parrot.callconfirm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.activity.CreditActivity;
import com.sakurafish.parrot.callconfirm.activity.SettingActivity;

/**
 * Created by sakura on 2015/01/23.
 */
public class MainFragment extends Fragment {

    private Context mContext;

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        initLayout();
    }

    private void initLayout() {
        getView().findViewById(R.id.menu_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SettingActivity.createIntent(mContext, SettingActivity.class));
//                startActivity(ConfirmActivity.createIntent(mContext, ConfirmActivity.class,"08056549370"));
            }
        });

        getView().findViewById(R.id.menu_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        getView().findViewById(R.id.menu_googleplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.share_text))));
            }
        });

        getView().findViewById(R.id.menu_credit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CreditActivity.createIntent(mContext, CreditActivity.class));
            }
        });

        getView().findViewById(R.id.menu_mail_to_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL,  new String[] { "sakurafish1@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_mail_to_dev3));
                startActivity(intent);
            }
        });

        // 広告枠の設定
        RelativeLayout adarea = (RelativeLayout) getView().findViewById(R.id.ad_area);
        adarea.addView(MyApplication.getAdContext()
                .createBanner(mContext, getResources().getInteger(R.integer.surupass_bannerId)));
    }

    private void finalizeLayout() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finalizeLayout();
        mContext = null;
    }
}