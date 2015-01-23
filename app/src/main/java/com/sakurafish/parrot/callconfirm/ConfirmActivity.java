package com.sakurafish.parrot.callconfirm;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sakurafish.common.lib.ui.GeneralDialogFragment;

/**
 * Created by sakura on 2015/01/23.
 */
public class ConfirmActivity extends ActionBarActivity {

    private static final int REQUEST_DIALOG_ERROR = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("title");
        builder.setMessage("message");
        builder.setPositiveText("OK");
        builder.setTargetFragment(this, REQUEST_DIALOG_ERROR);
        builder.create().show(getFragmentManager(), ConfirmActivity.class.getSimpleName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
