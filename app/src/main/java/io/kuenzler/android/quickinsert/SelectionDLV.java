package io.kuenzler.android.quickinsert;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Leonhard Künzler
 * @version 0.1
 *          Does not work on MM
 */
public class SelectionDLV implements AdapterView.OnItemClickListener {

    Xposed mMain;
    Context mCon;
    Dialog mListDialog;
    String[] mVal;
    EditText mEt;

    /**
     * @param main
     * @param options
     */
    public SelectionDLV(Xposed main, String[] options) {
        mMain = main;
        mCon = main.getContext();
        mEt = main.getET();
        mVal = options;
    }

    /**
     *
     */
    public void showdialog() {
        try {
            mListDialog = new Dialog(mCon);
            mListDialog.setTitle("Select Text");
            mListDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            LayoutInflater li = (LayoutInflater) mCon.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.list, (ViewGroup) mEt.getParent(), false);
            mListDialog.setContentView(v);
            mListDialog.setCancelable(true);
            ListView list1 = (ListView) mListDialog.findViewById(R.id.listview);
            list1.setOnItemClickListener(this);
            list1.setAdapter(new ArrayAdapter<String>(mCon, android.R.layout.simple_list_item_1, mVal));
            mListDialog.show();
        } catch (WindowManager.BadTokenException e) {
            //until android 6, TYPE_SYTEM_ALARM works. After that app needs permission.
            Toast.makeText(mCon, "Cannot open dialog. Looks like you run marshmallow.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mListDialog.cancel();
        mMain.setText(arg2);
    }
}

