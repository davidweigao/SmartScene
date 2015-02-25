package david.sceneapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import david.sceneapp.R;

public class AddWifiTriggerActivity extends Activity {
    private static final String TAG = AddWifiTriggerActivity.class.getSimpleName();

    public static final String EXTRA_WIFI_NAME = "com.david.wifi_name";

    @InjectView(R.id.wifiNameET) EditText wifiNameET;
    @InjectView(R.id.currentWifiBT) Button currentWifiButton;

    private boolean isNextEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_trigger);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.currentWifiBT)
    public void setToCurrentWifi() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiNameET.setText(wifiManager.getConnectionInfo().getSSID());
    }

    @OnTextChanged(R.id.wifiNameET)
    public void onWifiNameChanged(CharSequence s) {
        boolean previousValue = isNextEnabled;
        if(s.length() == 0) {
            isNextEnabled = false;
        } else {
            isNextEnabled = true;
        }
        if(previousValue != isNextEnabled) {
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isNextEnabled)
            getMenuInflater().inflate(R.menu.add_wifi_trigger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            Intent intent = new Intent(this, AddWifiTriggerActivity2.class);
            intent.putExtra(EXTRA_WIFI_NAME, wifiNameET.getText().toString());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
