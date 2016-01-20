package io.kuenzler.android.quickinsert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 *
 */
public class Xposed implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private static int index, indexMax;
    private static SelectionDLV selectionDLV;
    private static Activity currentActivity;
    private static boolean newObject;
    private static boolean first = true;
    private static String[] valuesX = {"", "bla1", "bla2", "bla3", "bla4"};
    private static String[] values1 = {"", "a@a.de", "bla@a.com", "work@me.com"};
    private static String[] values2 = {"", "0516165", "+4956315515", "+4930982522", "112"};
    private static String[] values3 = {"", "Adress1", "adrress work", "myhomeadress"};
    private static String[][] arrays = {valuesX, values1, values2, values3};
    private static int etHash;
    private Context mCon;
    private EditText mEditText;

    /**
     * Hook "view gained focus" and start check if view is edittext, then run quickinsert
     *
     * @param startupParam
     * @throws Throwable
     */
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

        //get current activity
        findAndHookMethod(android.app.Instrumentation.class, "newActivity", ClassLoader.class, String.class, Intent.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                currentActivity = (Activity) param.getResult();
                //mCon = currentActivity.getApplicationContext();
            }
        });

        //hook focus changed for textedit hook
        findAndHookMethod(TextView.class, "onFocusChanged", boolean.class, int.class, Rect.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.thisObject instanceof EditText) {
                    //reference edittext and get ApplicationContext
                    mEditText = (EditText) param.thisObject;
                    mCon = mEditText.getContext().getApplicationContext();
                    if ((boolean) param.args[0]) {
                        //has focus on an EditText
                        //mEditText.setText("I have focus!"); //to future me: this one works <<
                        int newHash = mEditText.hashCode();
                        if (!(etHash == newHash)) {
                            //new EditText
                            etHash = newHash;
                            first = true;
                            valuesX[0] = mEditText.getText().toString();
                        } else {
                            //old EditText, keep text
                        }
                        XposedBridge.log("QI - Focus on " + getET().getClass().getName() + ", " + newHash);
                        //TODO: some sort of ui
                        //Intent myIntent = new Intent(CurrentActivity.this, QuickInsert.class);
                        //myIntent.putExtra("edittext", mEditText);
                        //CurrentActivity.this.startActivity(myIntent);
                        View.OnKeyListener okl = new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                    if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
                                        keyPressed(keyCode);
                                        return true;
                                    }

                                }
                                return false;
                            }
                        };
                        mEditText.setOnKeyListener(okl);
                        //TODO: need for debugging //writeTextEdit(getET().getClass().getName() + ", " + newHash + ",\n");
                    }
                }
            }
        });
    }

    private SelectionDLV getSelectionDLV() {
        if (selectionDLV == null) {
            selectionDLV = new SelectionDLV(this);
        }
        return selectionDLV;
    }

    private void init() {
        XSharedPreferences pref = new XSharedPreferences("io.kuenzler.android.quickinsert", "user_settings");
        //String values = pref.getString("userValues", "eins,zwei,drei,vier,fünf,sechs,sieben");
        //String[] valuesParsed = values.split(",");
        //TODO: init user Strings here
    }

    /**
     * Reacts on volume up/down press
     *
     * @param keyCode up or down
     */
    protected void keyPressed(int keyCode) {
        //indexMax = valuesX.length - 1;
        indexMax = arrays.length - 1;
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if (first) {
                first = false;
            } else {
                if (index < indexMax) {
                    index++;
                } else {
                    index = 0;
                }
            }
            getSelectionDLV().createDialog(arrays[index]);
            //mEditText.setText(valuesX[index]);
            XposedBridge.log("keycode down consumed (index " + index + ")");
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            if (first) {
                first = false;
            } else {
                if (index > 0) {
                    index--;
                } else {
                    index = indexMax;
                }
            }
            getSelectionDLV().createDialog(arrays[index]);
            mEditText.setText(valuesX[index]);
            XposedBridge.log("keycode up consumed (index " + index + ")");
        }
    }

    /**
     * Sets selected text to et, callback from sDLV.
     *
     * @param i index of string to set
     */
    public void setSeletctedText(int i) {
        Toast.makeText(mCon, "Setting " + arrays[index][i] + " from array " + index + " at index " + i, Toast.LENGTH_SHORT).show();
        setText(arrays[index][i]);
    }

    private void writeTextEdit(String type) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/quickinsert");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "EditTexts");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(type);
            myOutWriter.close();
            fOut.close();

        } catch (IOException e) {
            Toast.makeText(mCon, "Could not write to file!!!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return
     */
    public Context getContext() {
        return mCon;
    }

    /**
     * @return
     */
    public EditText getET() {
        return mEditText;
    }

    /**
     * @return
     */
    public Activity getActivity() {
        return currentActivity;
    }

    /**
     * @param text
     */
    public void setText(String text) {
        mEditText.setText(text);
    }

    /**
     * Method checking if module is enabled in xposed and xposed is working
     *
     * @param lpparam
     * @throws Throwable
     */
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws
            Throwable {
        if (lpparam.packageName.equals("io.kuenzler.android.quickinsert")) {

            findAndHookMethod("io.kuenzler.android.quickinsert.MainActivity", lpparam.classLoader, "isModuleActive", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                    XposedBridge.log("Xposed Module in \"QuickInsert\" is enabled");
                }
            });
        }
    }
}