package david.sceneapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import david.sceneapp.R;

public class AddWifiTriggerActivity extends Activity {

    EditText wifiNameET;
    public static final String EXTRA_WIFI_NAME = "com.david.wifi_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_trigger);
        wifiNameET = (EditText) findViewById(R.id.wifiNameET);
        findViewById(R.id.currentWifiBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiNameET.setText(wifiManager.getConnectionInfo().getSSID());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_wifi_trigger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_next) {
            Intent intent = new Intent(this, AddWifiTriggerActivity2.class);
            intent.putExtra(EXTRA_WIFI_NAME, wifiNameET.getText().toString());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
