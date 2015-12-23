package io.kuenzler.android.quickinsert;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 *
 */
public class QuickInsert extends Activity {

    final EditText mEditText;
    final String[] temp = {"blabla1", "bla2", "thisis3", "viier", "five"}; //testing only

    int index;

    /**
     *
     * @param editText the EditText obj to influence
     */
    public QuickInsert(EditText editText) {
        mEditText = editText;
        index = 0;
        init();
    }

    /**
     *
     */
    private void init() {
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mEditText.setText("focus gone:(");
                    finish();
                    //TODO: how to kill this?
                }
            }
        });
        mEditText.setText("Listener Set!"); //TODO: delete
    }

    /**
     * Onkeydown event for list
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            index++;
            if (index <= 4) {

            } else {
                index = 0;
            }
            mEditText.setText(temp[index]);

            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            index--;
            if (index >= 0) {

            } else {
                index = 4;
            }
            mEditText.setText(temp[index]);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
