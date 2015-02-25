package david.sceneapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import david.sceneapp.R;

public class AddTriggerOptionActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trigger_option);
        findViewById(R.id.wifiTriggerBT).setOnClickListener(this);
        findViewById(R.id.notificationTriggerBT).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wifiTriggerBT:
                startActivity(new Intent(this, AddWifiTriggerActivity.class));
                break;
            case R.id.notificationTriggerBT:
                break;
        }
    }
}
