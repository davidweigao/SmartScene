package david.sceneapp.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;


import java.util.List;

import david.sceneapp.Activity.AddSceneActivity;
import david.sceneapp.LALALAService;
import david.sceneapp.Model.Scene;
import david.sceneapp.R;
import david.sceneapp.SceneStorageManager;


/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * interface.
 */
public class SceneFragment extends ListFragment {
    public static final String TAG = SceneFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<Scene> mAdapter;
    private ActionMode mActionMode;
    private ToggleButton wifiEnableButton;

    // TODO: Rename and change types of parameters
    public static SceneFragment newInstance(String param1, String param2) {
        SceneFragment fragment = new SceneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SceneFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(LALALAService.ACTION_SCENE_IMPLEMENTED);
        filter.addAction(LALALAService.ACTION_SCENES_UPDATED);
        getActivity().registerReceiver(receiver, filter);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        //setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
        //        android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));

        mAdapter = new ArrayAdapter<Scene>(getActivity(), R.layout.list_item_scene, R.id.textView){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getView");
                View v = super.getView(position, convertView, parent);
                if (LALALAService.currentScene != null) {
                    int thisId = getItem(position).getId();
                    int currentId = LALALAService.currentScene.getId();
                    Log.d(TAG, "this id : " + thisId + "  that id : " + currentId);
                    getListView().setItemChecked(position, thisId == currentId);
//                    ((CheckedTextView)v).setChecked(thisId == currentId);
                }
                return v;
            }
        };
        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scene, null);
        wifiEnableButton = (ToggleButton) v.findViewById(R.id.toggleButton);
        wifiEnableButton.setChecked(SceneStorageManager.getWifiEnabled(getActivity()));
        wifiEnableButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LALALAService.currentInstance.toggleAllWifiSwitch(isChecked);
            }
        });
        return v;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                getActivity().startActivity(new Intent(getActivity(), AddSceneActivity.class));
                break;
        }
        return true;
    }

    public void updateScene(List<Scene> scenes) {
        if(mAdapter != null) {
            mAdapter.clear();
            mAdapter.addAll(scenes);
            mAdapter.notifyDataSetChanged();

        }
    }

    public void selectScene(Scene scene) {
        int position = mAdapter.getPosition(scene);
        getListView().setItemChecked(position, true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mActionMode != null) {
                    return false;
                }

                mActionMode = getActivity().startActionMode(mActionModeCallback);
                getListView().setItemChecked(i,true);

                return true;
            }

        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LALALAService.ACTION_SCENE_IMPLEMENTED)) {
                int id = intent.getIntExtra(LALALAService.EXTRA_SCENE_ID, -1);
                if(id != -1) {
                    for(int i = 0 ; i < mAdapter.getCount(); i++) {
                        if(mAdapter.getItem(i).getId() == id) {
                            getListView().setItemChecked(i, true);
                            break;
                        }
                    }
                    mAdapter.notifyDataSetInvalidated();
                }
            } else if(intent.getAction().equals(LALALAService.ACTION_SCENES_UPDATED)) {
                Log.d(TAG, "get Action Scenes update");
                updateScene(LALALAService.currentInstance.getScenes());
            }

        }
    };
    @Override
    public void onResume() {
        super.onResume();
        if(LALALAService.currentInstance != null)
            LALALAService.currentInstance.updateScenes();
        IntentFilter filter = new IntentFilter(LALALAService.ACTION_SCENE_IMPLEMENTED);
        filter.addAction(LALALAService.ACTION_SCENES_UPDATED);

        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.scene_long_press, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //shareCurrentItem();
                    int pos = getListView().getCheckedItemPosition();
                    int id = mAdapter.getItem(pos).getId();
                    LALALAService.currentInstance.deleteScene(id);
                    getListView().setItemChecked(pos,false);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onSceneClicked(mAdapter.getItem(position).getId());
        }
    }



    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSceneClicked(int id);
    }


}
