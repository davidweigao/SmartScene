package david.sceneapp.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import david.sceneapp.Fragment.ExceptionFragment;
import david.sceneapp.Fragment.SceneFragment;
import david.sceneapp.Fragment.TriggerFragment;
import david.sceneapp.LALALAService;
import david.sceneapp.R;

public class MainActivity extends Activity implements ActionBar.TabListener,
        SceneFragment.OnFragmentInteractionListener,
        TriggerFragment.OnTriggerInteractionListener,
        ExceptionFragment.OnFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private SceneFragment mSceneFragment = SceneFragment.newInstance("", "");
    private TriggerFragment mTriggerFragment = TriggerFragment.newInstance("", "");
    private ExceptionFragment mExceptionFragment = ExceptionFragment.newInstance("", "");
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> fragmentTitleList = new ArrayList<String>();

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LALALAService.ACTION_SCENE_IMPLEMENTED)) {
                int id = intent.getIntExtra(LALALAService.EXTRA_SCENE_ID, -1);
                if (mSceneFragment != null && id != -1) {
                    mSceneFragment.selectScene(LALALAService.currentScene);
                }
            } else if(intent.getAction().equals(LALALAService.ACTION_SCENES_UPDATED)) {
                mSceneFragment.updateScene(LALALAService.currentInstance.getScenes());
            } else if(intent.getAction().equals(LALALAService.ACTION_EXCEPTIONS_UPDATED)) {
                mExceptionFragment.updateException(LALALAService.currentInstance.getExceptions());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fragmentList.add(mSceneFragment);
        fragmentList.add(mTriggerFragment);
        fragmentList.add(mExceptionFragment);

        fragmentTitleList.add(getString(R.string.title_section_scenes));
        fragmentTitleList.add(getString(R.string.title_section_triggers));
        fragmentTitleList.add(getString(R.string.title_section_exceptions));

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//               actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LALALAService.ACTION_SCENE_IMPLEMENTED);
        filter.addAction(LALALAService.ACTION_SCENES_UPDATED);
        filter.addAction(LALALAService.ACTION_EXCEPTIONS_UPDATED);
        registerReceiver(mReceiver, filter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onSceneClicked(int id) {
        LALALAService.currentInstance.implementScene(id);
    }

    @Override
    public void onTriggerClicked(int id) {

    }

    @Override
    public void onExceptionClicked(int id) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}