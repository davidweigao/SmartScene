package david.sceneapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import david.sceneapp.SceneManageService;
import david.sceneapp.R;
import david.sceneapp.Model.Scene;
import david.sceneapp.SceneStorageManager;
import david.sceneapp.Model.SceneTriggerData;

public class AddWifiTriggerActivity2 extends Activity {

    String wifiName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiName = getIntent().getStringExtra(AddWifiTriggerActivity.EXTRA_WIFI_NAME);
        setContentView(R.layout.activity_add_wifi_trigger_activity2);
        ListView listView = (ListView) findViewById(R.id.listView);
        final SceneAdapter adapter = new SceneAdapter(this, android.R.layout.simple_list_item_1,
                SceneManageService.currentInstance.getScenes());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SceneTriggerData triggerData = new SceneTriggerData();
                triggerData.setName(wifiName);
                triggerData.setParameters(new String[]{wifiName});
                triggerData.setSceneId(adapter.getItem(i).getId());
                triggerData.setTriggerType(SceneTriggerData.TYPE_WIFI_SWITCH);
                SceneStorageManager ssm = new SceneStorageManager(AddWifiTriggerActivity2.this);
                ssm.saveTrigger(triggerData);
                AddWifiTriggerActivity2.this.startActivity(
                        new Intent(AddWifiTriggerActivity2.this, MainActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_wifi_trigger_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SceneAdapter extends ArrayAdapter<Scene> {

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        public SceneAdapter(Context context, int resource, List<Scene> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            ((TextView)convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position).getName());
            return convertView;

        }
    }
}
