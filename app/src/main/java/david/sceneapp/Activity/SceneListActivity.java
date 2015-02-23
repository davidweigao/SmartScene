package david.sceneapp.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import david.sceneapp.SceneManageService;
import david.sceneapp.R;
import david.sceneapp.Model.Scene;

public class SceneListActivity extends Activity {

    Map<Integer, Scene> sceneMap = new HashMap<Integer, Scene>();
    int selectedSceneIndex = -1;
    ListView listView;
    SceneArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_list);
        listView = (ListView) findViewById(R.id.listView);
        //updateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) {
                    Scene s = (Scene) listView.getItemAtPosition(position);
                    SceneManageService.currentInstance.implementScene(s.getId());
                }
            }
        });

        IntentFilter filter = new IntentFilter(SceneManageService.ACTION_SCENE_IMPLEMENTED);
        filter.addAction(SceneManageService.ACTION_SCENES_UPDATED);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(SceneManageService.ACTION_SCENE_IMPLEMENTED)) {
                    int id = intent.getIntExtra(SceneManageService.EXTRA_SCENE_ID, -1);
                    if(id != -1) {
                        Scene s = sceneMap.get(id);
                        int position = adapter.getPosition(s);
                        selectedSceneIndex = position;
                        adapter.notifyDataSetInvalidated();
                    }
                } else if(intent.getAction().equals(SceneManageService.ACTION_SCENES_UPDATED)) {
                    updateList();
                    adapter.notifyDataSetChanged();
                }
            }
        };
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SceneManageService.currentInstance != null)
            SceneManageService.currentInstance.updateScenes();
    }

    private void updateList() {
        List<Object> objects = new ArrayList<Object>();
        objects.add(new Object());
        sceneMap = SceneManageService.currentInstance.getSceneMap();

        objects.addAll(sceneMap.values());
        adapter = new SceneArrayAdapter(this, R.layout.list_item_scene, objects);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scene_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(SceneListActivity.this, AddSceneActivity.class));
        } else if(id == R.id.wifi_switch_edit) {
            startActivity(new Intent(SceneListActivity.this, TriggerListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }




    private class SceneArrayAdapter extends ArrayAdapter<Object> {
        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public SceneArrayAdapter(Context context, int resource, List<Object> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch(position) {
                case 0: return getWifiSwitchView();

                default:return getSceneView(position, convertView, parent);
            }
        }

        private View getWifiSwitchView() {
            View v = inflater.inflate(R.layout.list_item_wifi_switch,null);
            ToggleButton tb = (ToggleButton) v.findViewById(R.id.toggleButton);
//            tb.setChecked(LALALAService.wifiEnabled);
            tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SceneManageService.currentInstance.toggleAllWifiSwitch(isChecked);
                }
            });
            return v;
        }

        private View getSceneView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.list_item_scene, null);
            CheckedTextView ctv = (CheckedTextView) convertView.findViewById(R.id.textView);
            Scene scene = (Scene)getItem(position);
            ctv.setText(scene.getName());
            ctv.setChecked(selectedSceneIndex == position ? true : false);
            return convertView;
        }

    }

}
