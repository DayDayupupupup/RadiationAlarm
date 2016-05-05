package edu.neu.radiationalarm.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import edu.neu.radiationalarm.R;
import edu.neu.radiationalarm.activity.MainActivity;
import edu.neu.radiationalarm.dbutil.DBManager;
import edu.neu.radiationalarm.info.RecentData;
import edu.neu.radiationalarm.service.LacService;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/27
 * Project: RadiationAlarm
 * Package: edu.neu.radiationalarm.fragment
 */
public class RadiationDetectionFragment extends Fragment implements LacService.FragmentListener_1 {

	private LineChart dial_chart;
	private LineChart history;
	private LacService.MyBinder binder;
	private TextView level;
	private TabLayout tab;

	private DBManager manager;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_radiation_detection, container, false);

		dial_chart = (LineChart) rootView.findViewById(R.id.radiation_dial_chart);
		history = (LineChart) rootView.findViewById(R.id.radiation_history_chart);

		level = (TextView) rootView.findViewById(R.id.live_radiation_level);
		tab = (TabLayout) rootView.findViewById(R.id.history_duration_selector);

		manager = new DBManager(getActivity());

		ButterKnife.bind(this, rootView);

		initTab();

		initView(inflater.getContext());

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(((MainActivity)getActivity()).binder == null) {}
				binder = ((MainActivity)getActivity()).binder;
				((MainActivity) getActivity()).getBinder().setFragmentListener_1(RadiationDetectionFragment.this);
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {

						level.setText(binder.getInfo().getStrengh() + " dBm");
						/*LineData mLineData = getLineData(0);
						showChart(dial_chart, mLineData, Color.WHITE);*/

						initChart();
						addEntry();
					}
				});
			}
		}).start();

		return rootView;
	}


	private void initChart() {
		dial_chart.setDescription("");
		dial_chart.setNoDataTextDescription("You need to provide data for the chart.");

		// enable touch gestures
		dial_chart.setTouchEnabled(true);

		// enable scaling and dragging
		dial_chart.setDragEnabled(true);
		dial_chart.setScaleEnabled(true);
		dial_chart.setDrawGridBackground(false);

		// if disabled, scaling can be done on x- and y-axis separately
		dial_chart.setPinchZoom(true);

		// set an alternative background color
		dial_chart.setBackgroundColor(Color.LTGRAY);

		LineData data = new LineData();
		data.setValueTextColor(Color.WHITE);

		// add empty data
		dial_chart.setData(data);

		//Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

		// get the legend (only possible after setting data)
		Legend l = dial_chart.getLegend();

		// modify the legend ...
		// l.setPosition(LegendPosition.LEFT_OF_CHART);
		l.setForm(Legend.LegendForm.LINE);
		//l.setTypeface(tf);
		l.setTextColor(Color.WHITE);

		XAxis xl = dial_chart.getXAxis();
		//xl.setTypeface(tf);
		xl.setTextColor(Color.WHITE);
		xl.setDrawGridLines(false);
		xl.setAvoidFirstLastClipping(true);
		xl.setSpaceBetweenLabels(5);
		xl.setEnabled(true);

		YAxis leftAxis = dial_chart.getAxisLeft();
		//leftAxis.setTypeface(tf);
		leftAxis.setTextColor(Color.WHITE);
		leftAxis.setAxisMaxValue(300f);
		leftAxis.setAxisMinValue(0f);
		leftAxis.setStartAtZero(false);
		leftAxis.setDrawGridLines(true);

		YAxis rightAxis = dial_chart.getAxisRight();
		rightAxis.setEnabled(false);
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

	private void initView(Context context){

	}

	private void initTab() {
		tab.addTab(tab.newTab().setText("年记录"));
		tab.addTab(tab.newTab().setText("月记录"));
		tab.addTab(tab.newTab().setText("日记录"));
		tab.setTabMode(TabLayout.MODE_FIXED);
		tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				if(tab.getText().equals("日记录")) {
					LineData mLineData = getLineData(1);
					showChart(history, mLineData, Color.rgb(114, 188, 223));
				} else if(tab.getText().equals("月记录")) {
					LineData mLineData = getLineData(2);
					showChart(history, mLineData, Color.rgb(114, 188, 223));
				} else if(tab.getText().equals("年记录")) {
					LineData mLineData = getLineData(3);
					showChart(history, mLineData, Color.rgb(114, 188, 223));
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	private String getTime(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);

		return calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE);
	}

	// 设置显示的样式
	private void showChart(LineChart lineChart, LineData lineData, int color) {
		lineChart.setDrawBorders(false);  //是否在折线图上添加边框

		// no description text
		lineChart.setDescription("");// 数据描述
		// 如果没有数据的时候，会显示这个，类似listview的emtpyview
		lineChart.setNoDataTextDescription("You need to provide data for the chart.");

		// enable / disable grid background
		lineChart.setDrawGridBackground(false); // 是否显示表格颜色

		// enable touch gestures
		lineChart.setTouchEnabled(true); // 设置是否可以触摸

		// enable scaling and dragging
		lineChart.setDragEnabled(true);// 是否可以拖拽
		lineChart.setScaleEnabled(true);// 是否可以缩放

		// if disabled, scaling can be done on x- and y-axis separately
		lineChart.setPinchZoom(false);//

		lineChart.setBackgroundColor(color);// 设置背景

		// add data
		lineChart.setData(lineData); // 设置数据

		// get the legend (only possible after setting data)
		/*Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

		// modify the legend ...
		// mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
		mLegend.setForm(LegendForm.CIRCLE);// 样式
		mLegend.setFormSize(6f);// 字体
		mLegend.setTextColor(Color.WHITE);// 颜色
//      mLegend.setTypeface(mTf);// 字体*/

		lineChart.animateX(100); // 立即执行的动画,x轴
	}




	private LineData getLineData(int flag) {

		ArrayList<String> xValues = new ArrayList<>();
		// y轴的数据
		ArrayList<Entry> yValues = new ArrayList<Entry>();

		if(flag == 0) {
			List<RecentData> datas = binder.getInfo().getRecentData();

			for(int i = 0; i < datas.size(); i++) {
				xValues.add("");
			}


			for (int i = 0; i < datas.size(); i++) {

				yValues.add(new Entry(datas.get(i).getBsss(), i));
			}

			// create a dataset and give it a type
			// y轴的数据集合
		} else if(flag == 1) {

			List<Long> list_time_day = manager.timeOfDay();
			List<Integer> list_strength_day = manager.strengthOfDay();
			for(int i = 0; i < list_time_day.size(); i++) {
				xValues.add(getTime(list_time_day.get(i)));
			}
			for(int i = 0; i < list_strength_day.size(); i++) {
				yValues.add(new Entry(list_strength_day.get(i), i));
			}
		} else if(flag == 2) {

			List<Integer> list_day_month = manager.dayOfMonth();
			List<Integer> list_strength_month = manager.strengthOfMonth();
			for(int i = 0; i < list_day_month.size(); i++) {
				xValues.add(list_day_month.get(i) + "");
				yValues.add(new Entry(list_strength_month.get(i), i));
			}
		} else if(flag == 3) {

			List<Integer> list_month_year = manager.monthOfYear();
			List<Integer> list_strength_year = manager.strengthOfYear();
			for(int i = 0; i < list_month_year.size(); i++) {
				xValues.add(list_month_year.get(i) + "");
				yValues.add(new Entry(list_strength_year.get(i), i));
			}
		}

		LineDataSet lineDataSet = new LineDataSet(yValues, null);
		// mLineDataSet.setFillAlpha(110);
		// mLineDataSet.setFillColor(Color.RED);

		//用y轴的集合来设置参数
		lineDataSet.setLineWidth(1.75f); // 线宽
		lineDataSet.setCircleSize(3f);// 显示的圆形大小
		lineDataSet.setColor(Color.WHITE);// 显示颜色
		lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
		lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

		ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
		lineDataSets.add(lineDataSet); // add the datasets

		// create a data object with the datasets
		LineData lineData = new LineData(xValues, lineDataSets);

		return lineData;
	}


	private void addEntry() {

		LineData data = dial_chart.getData();

		if (data != null) {

			LineDataSet set = data.getDataSetByIndex(0);
			// set.addEntry(...); // can be called as well

			if (set == null) {
				set = createSet();
				data.addDataSet(set);
			}

			// add a new x-value first
			data.addXValue("");
			data.addEntry(new Entry(binder.getInfo().getRecentData().get(0).getBsss(), set.getEntryCount()), 0);


			// let the chart know it's data has changed
			dial_chart.notifyDataSetChanged();

			// limit the number of visible entries
			dial_chart.setVisibleXRangeMaximum(20);

			// mChart.setVisibleYRange(30, AxisDependency.LEFT);

			// move to the latest entry
			dial_chart.moveViewToX(data.getXValCount() - 20);

			// this automatically refreshes the chart (calls invalidate())
			// mChart.moveViewTo(data.getXValCount()-7, 55f,
			// AxisDependency.LEFT);
		}
	}


	private LineDataSet createSet() {

		LineDataSet set = new LineDataSet(null, "Dynamic Data");
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		set.setColor(ColorTemplate.getHoloBlue());
		set.setCircleColor(Color.WHITE);
		set.setLineWidth(2f);
		set.setCircleSize(0f);
		set.setFillAlpha(65);
		set.setFillColor(ColorTemplate.getHoloBlue());
		set.setHighLightColor(Color.rgb(244, 117, 117));
		set.setValueTextColor(Color.WHITE);
		set.setValueTextSize(9f);
		set.setDrawValues(false);
		return set;
	}


	@Override
	public void onDataChanged() {

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				level.setText(binder.getInfo().getStrengh() + " dBm");
				/*LineData mLineData = getLineData(0);
				showChart(dial_chart, mLineData, Color.WHITE);*/
				addEntry();
			}
		});
	}
}
