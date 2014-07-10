package david.sceneapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import david.sceneapp.R;

public class TriggerListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_trigger_list);
        List<SceneTriggerData> triggerList = new ArrayList<SceneTriggerData>();
        SceneStorageManager ssm = new SceneStorageManager(this);
        triggerList.addAll(ssm.getAllTrigger().values());
        TriggerAdapter adapter = new TriggerAdapter(this, android.R.layout.simple_list_item_1, triggerList);
        ListView triggerListView = (ListView)findViewById(R.id.listView);
        triggerListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wifi_trigger_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(this, AddTriggerOptionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private class TriggerAdapter extends ArrayAdapter<SceneTriggerData> {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public TriggerAdapter(Context context, int resource, List<SceneTriggerData> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            return super.getView(position, convertView, parent);
            if(convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            ((TextView)convertView.findViewById(android.R.id.text1)).setText(getItem(position).getName());
            return convertView;
        }
    }
}
