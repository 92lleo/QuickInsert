package io.kuenzler.android.quickinsert;

import android.graphics.Rect;
import android.widget.EditText;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 *
 */
public class Xposed implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    /**
     * Hook "edittext gained focus" and start quickinsert
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
                    EditText et = (EditText) param.thisObject;
                    if ((boolean) param.args[0]) {
                        et.setText("I have focus!!! (1)"); //to future me: this one works <<
                        QuickInsert qi = new QuickInsert(et);
                    }
                } else if (param.thisObject instanceof TextView) {
                    //TODO: delete else-if, obsolete
                    TextView et = (TextView) param.thisObject;
                    if ((boolean) param.args[0]) {
                        et.setText("I have focus!!! (2)");
                    }
                } else {
                    //nothing to do here
                }
            }
        });
    }

    /**
     * Method checking if module is enabled in xposed and xposed is working
     *
     * @param lpparam
     * @throws Throwable
     */
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
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