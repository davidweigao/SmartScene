package david.sceneapp.Activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import david.sceneapp.R;

public class AppListActivity extends Activity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    @InjectView(R.id.pager)
    ViewPager mViewPager;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        ButterKnife.inject(this);
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> nonSystemApps = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo packageInfo : packages) {
            if(((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)
                    | (packageInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP))
                    == 0){// && packageInfo.packageName.contains("com.htc.")
                //&& !packageInfo.packageName.contains("com.android.")
                //&& !packageInfo.packageName.contains("com.google.")) {
                nonSystemApps.add(packageInfo);
            }
            pm.getApplicationIcon(packageInfo);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mSectionsPagerAdapter.apps = packages;
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabs.setViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_list, menu);
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            AppFilter filter = getFilter(position);
            List<ApplicationInfo> filtedApps = new ArrayList<ApplicationInfo>();
            for(ApplicationInfo appInfo : apps) {
                if(filter.filter(appInfo)) filtedApps.add(appInfo);
            }
            return AppListFragment.newInstance(position + 1, filtedApps);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

        public AppFilter getFilter(int position) {
            switch (position) {
                case 0:
                    return new AppFilter(){
                        @Override
                        public boolean filter(ApplicationInfo appInfo) {
                            return suggestionApps.contains(appInfo.packageName.toString());
                        }
                    };
                case 1:
                    return new AppFilter(){
                        @Override
                        public boolean filter(ApplicationInfo appInfo) {
                            return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM
                                    | appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0;
                        }
                    };
                case 2:
                    return new AppFilter(){
                        @Override
                        public boolean filter(ApplicationInfo appInfo) {
                            return true;
                        }
                    };
                    default:return null;
            }
        }
    }

    private static interface AppFilter {
        boolean filter(ApplicationInfo appInfo);
    }

    public static class AppListFragment extends Fragment {
        List<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static AppListFragment newInstance(int sectionNumber,
                                                      List<ApplicationInfo> apps) {
            AppListFragment fragment = new AppListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            fragment.apps.addAll(apps);
            return fragment;
        }

        public AppListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);
            ArrayAdapter<ApplicationInfo> adapter = new ArrayAdapter<ApplicationInfo>(getActivity(),
                    R.layout.list_item_app) {
                final PackageManager pm = getActivity().getPackageManager();
                LayoutInflater inflater = (LayoutInflater)getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    //return super.getView(position, convertView, parent);
                    View v = inflater.inflate(R.layout.list_item_app, null);
                    ApplicationInfo ai = getItem(position);
                    TextView tv = (TextView) v.findViewById(R.id.label);
                    ImageView iv = (ImageView) v.findViewById(R.id.icon);
                    iv.setImageDrawable(pm.getApplicationIcon(ai));
                    tv.setText(pm.getApplicationLabel(ai));
                    return v;
                }
            };

            for(ApplicationInfo ai : apps) {
                adapter.add(ai);
            }
            final ListView listView = (ListView) rootView.findViewById(R.id.appListView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(
                            AppListFragment.this.getActivity(), AddExceptionActivity.class);
                    ApplicationInfo selectedApp = (ApplicationInfo) listView.getItemAtPosition(i);
                    String pkgName = selectedApp.packageName;
                    String name = selectedApp.loadLabel(getActivity().getPackageManager()).toString();
                    int iconRes = selectedApp.icon;
                    if(suggestionApps.contains(pkgName)) {
                        intent.putExtra(AddExceptionActivity.EXTRA_FROM_IM, true);
                    }
                    intent.putExtra(AddExceptionActivity.EXTRA_PACKAGE, pkgName);
                    intent.putExtra(AddExceptionActivity.EXTRA_NAME, name);
                    intent.putExtra(AddExceptionActivity.EXTRA_ICON, iconRes);
                    AppListFragment.this.getActivity().startActivity(intent);
                }
            });
            return rootView;
        }
    }

    public static Set<String> suggestionApps = new HashSet<String>();
    static {
        suggestionApps.add("com.immomo.momo");
        suggestionApps.add("com.tencent.mm");
        suggestionApps.add("jp.naver.line.android");
        suggestionApps.add("com.whatsapp");
    }

}
