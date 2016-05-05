package edu.neu.radiationalarm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import edu.neu.radiationalarm.fragment.FragmentException;
import edu.neu.radiationalarm.fragment.MainFragmentManager;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/21
 * Project: QMJZ
 * Package: edu.neu.qmjz.adapter
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {
	public MainPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		try {
			fragment = MainFragmentManager.getFragment(position);
		} catch (FragmentException e) {
			Log.d("MainPagerAdapter","New fragment is null.");
			return null;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
