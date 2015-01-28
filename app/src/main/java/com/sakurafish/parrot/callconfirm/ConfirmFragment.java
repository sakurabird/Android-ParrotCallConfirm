package com.sakurafish.parrot.callconfirm;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakurafish.common.lib.ui.GeneralDialogFragment;

/**
 * Created by sakura on 2015/01/23.
 */
public class ConfirmFragment extends Fragment {

    private static final int REQUEST_DIALOG_ERROR = 123;
    private Context mContext;

    public static ConfirmFragment getInstance() {
        return new ConfirmFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initLayout();
    }

    private void initLayout() {
        GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("title");
        builder.setMessage("message");
        builder.setPositiveText("OK");
        builder.setTargetFragment(this, REQUEST_DIALOG_ERROR);
        builder.create().show(getFragmentManager(), ConfirmActivity.class.getSimpleName());
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