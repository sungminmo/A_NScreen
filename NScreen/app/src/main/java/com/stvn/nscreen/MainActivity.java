package com.stvn.nscreen;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.jjiya.android.common.JYSharedPreferences;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String              tag = MainActivity.class.getSimpleName();
    public  static       MainActivity        mInstance;
    private              JYSharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mInstance     = this;
        mPref         = new JYSharedPreferences(this);

        String sWebhasTerminalKey = mPref.getWebhasTerminalKey();
        Log.d(tag, "sWebhasTerminalKey:"+sWebhasTerminalKey);

        if ( mPref.getValue(JYSharedPreferences.UUID, "").equals("") ) {
            // UUID 없으면 만들기.
            UUID uuid = UUID.randomUUID();
            mPref.put(JYSharedPreferences.UUID, uuid.toString());
        }

        /*
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.epg.EpgMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.my.MyMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.my.MySubActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.pvr.PvrMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.pvr.PvrSubActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.search.SearchMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.setting.CMSettingMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodMainActivity.class);
        Intent i = new Intent(MainActivity.this, com.stvn.nscreen.vod.VodDetailActivity.class);
        Intent i = new Intent(MainActivity.this, PairingMainActivity.class);
        Intent i = new Intent(MainActivity.this, PairingSubActivity.class);
        Intent i = new Intent(MainActivity.this, PairingCheckActivity.class);
        Intent i = new Intent(MainActivity.this, VodBuyActivity.class);
        Intent i = new Intent(MainActivity.this, RemoteControllerActivity.class);
        */

        /* StatusBarColor ----------------------------------------------------------------------- */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);          // clear FLAG_TRANSLUCENT_STATUS flag:
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.setStatusBarColor(getResources().getColor(R.color.violet));              // finally change the color
        }

        /* ActionBar TAB mode ------------------------------------------------------------------- */
        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.violet)));

        VodMainFragment  vodMainFragment = new VodMainFragment();
        VodEmptyFragment vodEmptyFragment = new VodEmptyFragment();

        ActionBar.Tab tab1 = actionBar.newTab().setText("추천").setTabListener(new MainTabsListener(vodMainFragment));
        ActionBar.Tab tab2 = actionBar.newTab().setText("영화").setTabListener(new MainTabsListener(vodEmptyFragment));
        ActionBar.Tab tab3 = actionBar.newTab().setText("애니키즈").setTabListener(new MainTabsListener(vodEmptyFragment));
        ActionBar.Tab tab4 = actionBar.newTab().setText("TV다시보기").setTabListener(new MainTabsListener(vodEmptyFragment));
        ActionBar.Tab tab5 = actionBar.newTab().setText("성인").setTabListener(new MainTabsListener(vodEmptyFragment));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);
        actionBar.addTab(tab4);
        actionBar.addTab(tab5);
        */
    }

    /*
    class MainTabsListener implements ActionBar.TabListener {
        private Fragment fragment;
        public MainTabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            //do what you want when tab is reselected, I do nothing
            //Toast.makeText(mInstance, "Reselected()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.fragment_placeholder, fragment);
            //Toast.makeText(mInstance, "Selected()", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
            //Toast.makeText(mInstance, "Unselected()", Toast.LENGTH_SHORT).show();
        }
    }
    */
}
