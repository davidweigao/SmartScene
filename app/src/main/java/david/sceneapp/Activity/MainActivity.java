package david.sceneapp.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import david.sceneapp.Fragment.ExceptionFragment;
import david.sceneapp.Fragment.SceneFragment;
import david.sceneapp.Fragment.TriggerFragment;
import david.sceneapp.SceneManageService;
import david.sceneapp.R;

public class MainActivity extends Activity implements
        SceneFragment.OnFragmentInteractionListener,
        TriggerFragment.OnTriggerInteractionListener,
        ExceptionFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.pager) ViewPager mViewPager;
    @InjectView(R.id.tabs)  PagerSlidingTabStrip tabs;

    private SceneFragment mSceneFragment = SceneFragment.newInstance();
    private TriggerFragment mTriggerFragment = TriggerFragment.newInstance();
    private ExceptionFragment mExceptionFragment = ExceptionFragment.newInstance();
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> fragmentTitleList = new ArrayList<String>();
    private IntentFilter mIntentFilter;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SceneManageService.ACTION_SCENE_IMPLEMENTED)) {
                int id = intent.getIntExtra(SceneManageService.EXTRA_SCENE_ID, -1);
                if (mSceneFragment != null && id != -1) {
                    mSceneFragment.selectScene(SceneManageService.currentScene);
                }
            } else if(intent.getAction().equals(SceneManageService.ACTION_SCENES_UPDATED)) {
                mSceneFragment.updateScene(SceneManageService.currentInstance.getScenes());
            } else if(intent.getAction().equals(SceneManageService.ACTION_EXCEPTIONS_UPDATED)) {
                mExceptionFragment.updateException(
                        SceneManageService.currentInstance.getExceptions());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.inject(this);

        fragmentList.add(mSceneFragment);
        fragmentList.add(mTriggerFragment);
        fragmentList.add(mExceptionFragment);

        fragmentTitleList.add(getString(R.string.title_section_scenes));
        fragmentTitleList.add(getString(R.string.title_section_triggers));
        fragmentTitleList.add(getString(R.string.title_section_exceptions));

        mViewPager.setAdapter(new SectionsPagerAdapter(getFragmentManager()));
        tabs.setViewPager(mViewPager);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(SceneManageService.ACTION_SCENE_IMPLEMENTED);
        mIntentFilter.addAction(SceneManageService.ACTION_SCENES_UPDATED);
        mIntentFilter.addAction(SceneManageService.ACTION_EXCEPTIONS_UPDATED);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSceneClicked(int id) {
        SceneManageService.currentInstance.implementScene(id);
    }

    @Override
    public void onTriggerClicked(int id) {
        // TODO
    }
    @Override
    public void onExceptionClicked(int id) {
        // TODO
    }

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

}
