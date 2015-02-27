package david.sceneapp.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import david.sceneapp.Activity.AppListActivity;
import david.sceneapp.Model.Scene;
import david.sceneapp.SceneManageService;
import david.sceneapp.Model.ExceptionScene;
import david.sceneapp.R;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * interface.
 */
public class ExceptionFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private ExceptionSceneAdapter mAdapter;
    private ActionMode mActionMode;


    // TODO: Rename and change types of parameters
    public static ExceptionFragment newInstance() {
        ExceptionFragment fragment = new ExceptionFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExceptionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ExceptionSceneAdapter(getActivity(), R.layout.list_item_exception);
        setListAdapter(mAdapter);
        setHasOptionsMenu(true);

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

    public void updateException(Collection<ExceptionScene> exps) {
        if(mAdapter != null) {
            mAdapter.clear();
            mAdapter.addAll(exps);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(SceneManageService.currentInstance != null) {
            SceneManageService.currentInstance.updateExceptions();
        }
        updateException(SceneManageService.currentInstance.getExceptions());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onExceptionClicked(1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                getActivity().startActivity(new Intent(getActivity(), AppListActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                    SceneManageService.currentInstance.deleteException(id);
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

    public class ExceptionSceneAdapter extends ArrayAdapter<ExceptionScene> {

        private LayoutInflater inflater;

        public ExceptionSceneAdapter(Context context, int resource) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_exception, parent, false);
                convertView.setTag(new ExceptionSceneViewHolder(convertView));
            }
            ((ExceptionSceneViewHolder) convertView.getTag()).setExceptionScene(getItem(position));
            return convertView;
        }
    }

    public class ExceptionSceneViewHolder {
        @InjectView(R.id.textView)
        TextView textView;

        @InjectView(R.id.icon)
        ImageView icon;

        @InjectView(R.id.checkbox)
        CheckBox checkBox;

        ExceptionScene exceptionScene;

        public ExceptionSceneViewHolder(View view) {
            ButterKnife.inject(this, view);
            setExceptionScene(exceptionScene);
        }

        @OnCheckedChanged(R.id.checkbox)
        public void onCheckedChanged(boolean isChecked) {
            exceptionScene.setActivated(isChecked);
            SceneManageService.currentInstance.toggleExceptionScene(exceptionScene.getId(), isChecked);
        }

        public void setExceptionScene(ExceptionScene exceptionScene) {
            if(exceptionScene == null) return;
            this.exceptionScene = exceptionScene;
            textView.setText(exceptionScene.getName());
            checkBox.setChecked(exceptionScene.isActivated());
            try {
                icon.setImageDrawable(ExceptionFragment.this.getActivity()
                        .getPackageManager().getApplicationIcon(exceptionScene.getPkgName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
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
        public void onExceptionClicked(int id);
    }

}
