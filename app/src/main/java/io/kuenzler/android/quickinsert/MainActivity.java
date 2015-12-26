package io.kuenzler.android.quickinsert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences pref = getSharedPreferences("user_settings", MODE_WORLD_READABLE);
        String text = pref.getString("text", "");
        TextView tv = (TextView) findViewById(R.id.t_newText);
        tv.setText(text);
        Switch sw = (Switch) findViewById(R.id.sw_textOverwrite);
        boolean enabled = pref.getBoolean("enabled", false);
        sw.setChecked(enabled);
        if (!isModuleActive()) {
            xposedAlert();
        }
    }

    /**
     *
     */
    private void xposedAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Xposed Error")
                .setMessage("This Xposed module is not activated or Xposed is not installed. " +
                        "You can't use this app without Xposed.\n\nDo you want to close it?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

    public void apply(View view) {

        TextView tv = (TextView) findViewById(R.id.t_newText);
        if(tv.hasFocus()){
            tv.clearFocus();
            Button b = (Button) findViewById(R.id.b_setText);
            b.requestFocus();
        } else {
            tv.requestFocus();
        }
        /*SharedPreferences pref = getSharedPreferences("user_settings", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("text", input);
        editor.commit();
        toast("Set " + input + " as new text", true);*/

    }

    public void toggle(View view) {
        Switch sw = (Switch) findViewById(R.id.sw_textOverwrite);
        boolean enabled = sw.isChecked();
        SharedPreferences pref = getSharedPreferences("user_settings", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("enabled", enabled);
        editor.commit();
        String message;
        if (enabled) {
            message = "StringSmash enabled";
        } else {
            message = "StringSmash disabled";
        }
        toast(message, false);
    }

    /**
     * @param view
     */
    public void visitHomepage(View view) {
        String url = "http://www.kuenzler.io";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    /**
     * @param message
     * @param length
     */
    public void toast(String message, boolean length) {
        int duration = Toast.LENGTH_SHORT;
        if (length) {
            duration = Toast.LENGTH_LONG;
        }
        Toast.makeText(getApplicationContext(), message, duration).show();
    }

    /**
     * @return
     */
    private boolean isModuleActive() {
        return false;
    }
}
