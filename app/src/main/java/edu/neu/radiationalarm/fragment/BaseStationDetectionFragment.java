package edu.neu.radiationalarm.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import edu.neu.radiationalarm.R;
import edu.neu.radiationalarm.activity.MainActivity;
import edu.neu.radiationalarm.info.GSMCellLocationInfo;
import edu.neu.radiationalarm.service.LacService;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/27
 * Project: RadiationAlarm
 * Package: edu.neu.radiationalarm.fragment
 */
public class BaseStationDetectionFragment extends Fragment implements LacService.FragmentListener_2 {

	private TextView network_type;
	private TextView network_provider;
	private TextView lac;
	private TextView cid;
	private TextView bsss;
	private GSMCellLocationInfo info;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_base_station_detection, container, false);

		ButterKnife.bind(this, rootView);

		network_type = (TextView) rootView.findViewById(R.id.network_type);
		network_provider = (TextView) rootView.findViewById(R.id.network_provider);
		lac = (TextView) rootView.findViewById(R.id.lac);
		cid = (TextView) rootView.findViewById(R.id.cid);
		bsss = (TextView) rootView.findViewById(R.id.bsss);

		initView(inflater.getContext());

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(((MainActivity)getActivity()).binder == null) {}
				((MainActivity)getActivity()).getBinder().setFragmentListener_2(BaseStationDetectionFragment.this);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateText();
					}
				});
			}
		}).start();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initView(Context context) {
	}

	private void updateText() {
		info = ((MainActivity) getActivity()).getBinder().getInfo();
		lac.setText(info.getLac() + "");
		cid.setText(info.getCellid() + "");
		bsss.setText(info.getStrengh() + "");
	}

	@Override
	public void onDataChanged() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateText();
			}
		});

	}
}
