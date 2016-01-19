package io.kuenzler.android.quickinsert;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 *
 */
public class QuickInsert extends Activity {

    EditText mEditText;
    int index;

    /**
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = 0;
        //TODO: intent is not sent yet..
        //Intent intent = getIntent();
        //mEditText = (EditText) intent.getParcelableExtra("edittext");
        init();
    }

    /**
     * not used for now, all in Xposed class & working
     */
    private void init() {
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("QuickInsert", "focusChangedAfterInintial");
                if (!hasFocus) {
                    mEditText.setText("focus gone:(");
                    finish();
                    //TODO: how to kill this?
                }
            }
        });
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //action
                    return true;
                }
                return false;
            }
        });
        mEditText.setText("Listener Set!"); //delete when working
    }
}
