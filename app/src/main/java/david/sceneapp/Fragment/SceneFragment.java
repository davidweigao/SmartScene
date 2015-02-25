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
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ToggleButton;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import david.sceneapp.Activity.AddSceneActivity;
import david.sceneapp.SceneManageService;
import david.sceneapp.Model.Scene;
import david.sceneapp.R;
import david.sceneapp.SceneStorageManager;


public class SceneFragment extends ListFragment {
    public static final String TAG = SceneFragment.class.getSimpleName();

    @InjectView(R.id.toggleButton) ToggleButton wifiEnableButton;

    private OnFragmentInteractionListener mListener;
    private SceneAdapter mAdapter;
    private ActionMode mActionMode;

    public static SceneFragment newInstance() {
        SceneFragment fragment = new SceneFragment();
        return fragment;
    }

    public SceneFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(SceneManageService.ACTION_SCENE_IMPLEMENTED);
        filter.addAction(SceneManageService.ACTION_SCENES_UPDATED);
        getActivity().registerReceiver(receiver, filter);
        mAdapter = new SceneAdapter(getActivity(), R.layout.list_item_scene);
        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
    }

    private static class SceneAdapter extends ArrayAdapter<Scene> {

        LayoutInflater inflater ;
        public SceneAdapter(Context context, int resource) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SceneViewHolder holder;
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_scene, parent, false);
                holder = new SceneViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (SceneViewHolder)convertView.getTag();
            holder.checkedTextView.setText(getItem(position).getName());
            if(SceneManageService.currentScene != null) {
                holder.checkedTextView.setChecked(
                        getItem(position).getId() == SceneManageService.currentScene.getId());
            } else {
                holder.checkedTextView.setChecked(false);
            }
            return convertView;
        }
    }

    public static class SceneViewHolder {
        @InjectView(R.id.textView)
        CheckedTextView checkedTextView;
        public SceneViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scene, null);
        ButterKnife.inject(this, v);
        wifiEnableButton.setChecked(SceneStorageManager.getWifiEnabled(getActivity()));
        return v;
    }

    @OnCheckedChanged(R.id.toggleButton)
    public void toggleWifiSwitch(boolean isChecked) {
        SceneManageService.currentInstance.toggleAllWifiSwitch(isChecked);
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
            if(intent.getAction().equals(SceneManageService.ACTION_SCENE_IMPLEMENTED)) {
                int id = intent.getIntExtra(SceneManageService.EXTRA_SCENE_ID, -1);
                if(id != -1) {
                    for(int i = 0 ; i < mAdapter.getCount(); i++) {
                        if(mAdapter.getItem(i).getId() == id) {
                            getListView().setItemChecked(i, true);
                            break;
                        }
                    }
                    mAdapter.notifyDataSetInvalidated();
                }
            } else if(intent.getAction().equals(SceneManageService.ACTION_SCENES_UPDATED)) {
                Log.d(TAG, "get Action Scenes update");
                updateScene(SceneManageService.currentInstance.getScenes());
            }

        }
    };
    @Override
    public void onResume() {
        super.onResume();
        if(SceneManageService.currentInstance != null)
            SceneManageService.currentInstance.updateScenes();
        IntentFilter filter = new IntentFilter(SceneManageService.ACTION_SCENE_IMPLEMENTED);
        filter.addAction(SceneManageService.ACTION_SCENES_UPDATED);

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
                    SceneManageService.currentInstance.deleteScene(id);
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
