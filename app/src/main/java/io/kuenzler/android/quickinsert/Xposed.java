package io.kuenzler.android.quickinsert;

import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private static boolean newObject;
    private static boolean first = true;
    private static String[] valuesX = {"", "bla1", "bla2", "bla3", "bla4"};
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
        findAndHookMethod(TextView.class, "onFocusChanged", boolean.class, int.class, Rect.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.thisObject instanceof EditText) {
                    //reference edittext and get ApplicationContext
                    mEditText = (EditText) param.thisObject;
                    mCon = mEditText.getContext().getApplicationContext();
                    if ((boolean) param.args[0]) {
                        //has focus on an EditText
                        mEditText.setText("I have focus!"); //to future me: this one works <<
                        int newHash = mEditText.hashCode();
                        if (!(etHash == newHash)) {
                            //new EditText
                            etHash = newHash;
                            first = true;
                            valuesX[0] = mEditText.getText().toString();
                        } else {
                            //old EditText, keep text
                        }
                        Toast.makeText(mCon, "Focus on " + getET().getClass().getName() + ", " + newHash, Toast.LENGTH_SHORT).show();
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
                    }
                }
            }
        });
    }

    private void init() {
        XSharedPreferences pref = new XSharedPreferences("io.kuenzler.android.quickinsert", "user_settings");
        //String values = pref.getString("userValues", "eins,zwei,drei,vier,fünf,sechs,sieben");
        //String[] valuesParsed = values.split(",");
        //TODO: init user Strings here
    }

    /**
     * Has no more use
     *
     * @param et
     * @param values
     * @return
     */
    private int getCurrentIndex(EditText et, String[] values) {
        String current = et.getText().toString().trim();
        if (!current.isEmpty()) {
            for (int i = 0; i < values.length; i++) {
                if (current.equals(values[i])) {
                    return i;
                }
            }
        }
        Toast.makeText(mCon, "Text: " + current + " did not find text, i=0", Toast.LENGTH_SHORT).show();
        return 0;
    }

    /**
     * Reacts on volume up/down press
     *
     * @param keyCode up or down
     */
    private void keyPressed(int keyCode) {
        Toast.makeText(mCon, "index is " + index + ", first is " + String.valueOf(first), Toast.LENGTH_SHORT).show();
        indexMax = valuesX.length - 1;
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if (first) {
                first = false;
            } else {
                if (index < indexMax) {
                    index++;
                } else {
                    index = 0;
                }
                //no need //prefEdit.putInt("index", index);
            }
            //SelectionDLV dlv = new SelectionDLV(this, valuesX);
            //dlv.showdialog(); //TODO: does not work on MM
            mEditText.setText(valuesX[index]);
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
            //SelectionDLV dlv = new SelectionDLV(this, valuesX);
            //dlv.showdialog(); //TODO: does not work on MM
            mEditText.setText(valuesX[index]);
            XposedBridge.log("keycode up consumed (index " + index + ")");
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
     * @param i
     */
    public void setText(int i) {
        setText(valuesX[i]);
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