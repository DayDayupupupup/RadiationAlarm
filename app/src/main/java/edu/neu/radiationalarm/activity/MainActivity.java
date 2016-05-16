package edu.neu.radiationalarm.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.radiationalarm.R;
import edu.neu.radiationalarm.adapter.MainPagerAdapter;
import edu.neu.radiationalarm.service.LacService;


public class MainActivity extends BaseActivity {

	private final static int[] APP_TAB_TITLE = {
			R.string.radiation_detection_fragment_title,
			R.string.base_station_radiation_detection_title,
			R.string.baidumap_fragment_title
	};

	@Bind(R.id.main_app_bar)
	Toolbar mToolBar;
	ActionBar mAppBar;
	@Bind(R.id.main_app_tab)
	TabLayout mMainAppTab;
	@Bind(R.id.main_view_pager)
	ViewPager mMainViewPager;

	public LacService.MyBinder binder;
	private MyServiceConnection conn = new MyServiceConnection();

	final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


	private MainPagerAdapter mMainPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);
		SDKInitializer.initialize(getApplicationContext());

		Log.e("...", "  " + TelephonyManager.SIM_STATE_ABSENT);

		Log.e(".......", " " + ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getSimState());

		if(((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getSimState() != 5) {

			new AlertDialog.Builder(this)
					.setMessage("请检查有无插入SIM卡！")
					.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
					.create()
					.show();

		} else {

			Intent intent = new Intent(this, LacService.class);
			getApplicationContext().bindService(intent, conn, Service.BIND_AUTO_CREATE);

			initView();

		}
		requestPermission(1, Manifest.permission.ACCESS_FINE_LOCATION, new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "Location OK", Toast.LENGTH_SHORT).show();
					}
				}, new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "Location Denied", Toast.LENGTH_SHORT).show();
					}
				}
		);

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void initView(){



		setSupportActionBar(mToolBar);

		mAppBar = getSupportActionBar();

		mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
		mMainViewPager.setAdapter(mMainPagerAdapter);

		mMainAppTab.setTabMode(TabLayout.MODE_FIXED);
		mMainAppTab.setupWithViewPager(mMainViewPager);
		for(int i=0;i<mMainAppTab.getTabCount();i++){
			TabLayout.Tab tab = mMainAppTab.getTabAt(i);
			if(tab!=null){
				tab.setText(APP_TAB_TITLE[i]);
			}
		}




		mMainAppTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				mMainViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			binder = (LacService.MyBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	}

	public LacService.MyBinder getBinder() {

		return binder;
	}

}
