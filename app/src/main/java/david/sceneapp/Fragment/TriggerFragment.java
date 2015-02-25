package david.sceneapp.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import david.sceneapp.Activity.AddTriggerOptionActivity;
import david.sceneapp.SceneManageService;
import david.sceneapp.Model.SceneTriggerData;
import david.sceneapp.R;
import david.sceneapp.SceneStorageManager;


/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * interface.
 */
public class TriggerFragment extends ListFragment {

    private OnTriggerInteractionListener mListener;
    private TriggerAdapter mAdapter;
    private ActionMode mActionMode;

    public static TriggerFragment newInstance() {
        TriggerFragment fragment = new TriggerFragment();
        return fragment;
    }

    public TriggerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TriggerAdapter(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
    }

    public static class TriggerAdapter extends ArrayAdapter<SceneTriggerData> {

        private LayoutInflater inflater;

        public TriggerAdapter(Context context, int resource) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TriggerViewHolder holder;
            if(convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TriggerViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (TriggerViewHolder) convertView.getTag();
            holder.textView.setText(getItem(position).getName());
            return convertView;
        }
    }

    public static class TriggerViewHolder{
        @InjectView(android.R.id.text1)
        TextView textView;
        public TriggerViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateTriggers();
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                getActivity().startActivity(new Intent(getActivity(),
                        AddTriggerOptionActivity.class));
                break;
        }
        return true;
    }

    public void updateTriggers() {
        SceneStorageManager ssm = new SceneStorageManager(getActivity());
        mAdapter.clear();
        mAdapter.addAll(ssm.getAllTrigger().values());
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTriggerInteractionListener) activity;
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onTriggerClicked(1);
        }
    }

    public interface OnTriggerInteractionListener {
        // TODO: Update argument type and name
        public void onTriggerClicked(int id);
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
                    SceneManageService.currentInstance.deleteTrigger(id);
                    updateTriggers();
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


}
