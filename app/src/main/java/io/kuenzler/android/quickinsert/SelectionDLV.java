package io.kuenzler.android.quickinsert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * @author Leonhard Künzler
 * @version 0.1
 */
public class SelectionDLV {

    Xposed mMain;
    Context mCon;
    Dialog mListDialog;
    String[] mVal;
    EditText mEt;
    AlertDialog alertDialog;
    DialogInterface.OnKeyListener mOkl;

    /**
     * @param main
     */
    public SelectionDLV(Xposed main) {
        mMain = main;
        mCon = main.getContext();
        mEt = main.getET();
        mOkl = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
                        mMain.keyPressed(keyCode);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public void createDialog(String[] array) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        mVal = array;
        showdialog();
    }

    private void showdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMain.getActivity());

        builder.setTitle("Select Text");
        builder.setItems(mVal, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                alertDialog.cancel();
                mMain.setSeletctedText(item);
            }
        });
        alertDialog = builder.create();
        alertDialog.setOnKeyListener(mOkl);
        alertDialog.show();
    }
}

