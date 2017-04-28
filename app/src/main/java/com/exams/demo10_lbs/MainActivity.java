package com.exams.demo10_lbs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final int CMD_STOP_SERVICE = 0;

	public static final String TAG = "MainActivity";
	public Button startbtnButton, stopButton;
	public TextView tView;
	DataReceiver dataReceiver;// BroadcastReceiver对象
	public LocationManager lManager;
	public BarChartView mBarChartView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startbtnButton = (Button) findViewById(R.id.Startbtn);
		stopButton = (Button) findViewById(R.id.Stopbtn);
		
		tView = (TextView) findViewById(R.id.tv);
		mBarChartView=(BarChartView)findViewById(R.id.bar_chart);

		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS是否正常启动
		if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			return;
		}

		startbtnButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				startService();
				
			}
		});
		stopButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopService();
				
			}
		});

	}

	private void startService() {
		startbtnButton.setEnabled(false);
		stopButton.setEnabled(true);
		Intent i = new Intent(this, LBSService.class);
		this.startService(i);
		Log.i(TAG, "in startService method.");
		if (dataReceiver == null) {
			dataReceiver = new DataReceiver();
			IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
			filter.addAction("com.exams.demo10_lbs");
			registerReceiver(dataReceiver, filter);// 注册Broadcast Receiver
		}
	}

	private void stopService() {
		startbtnButton.setEnabled(true);
		stopButton.setEnabled(false);
		Intent i = new Intent(this, LBSService.class);
		this.stopService(i);
		Log.i(TAG, "in stopService method.");
		if (dataReceiver != null) {
			unregisterReceiver(dataReceiver);// 取消注册Broadcast Receiver
			dataReceiver = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class DataReceiver extends BroadcastReceiver {// 继承自BroadcastReceiver的子类

		@Override
		public void onReceive(Context context, Intent intent) {// 重写onReceive方法

			Bundle bundledata = intent.getExtras();
			if (bundledata != null) {
				String latitude = bundledata.getString("latitude");
				String longitude = bundledata.getString("longitude");
				String accuracy = bundledata.getString("accuracy");
				String speed=bundledata.getString("speed");
				String Satenum = bundledata.getString("Satenum");
				String dateString = bundledata.getString("date");
				tView.setText("\t卫星在用数量:" + Satenum + "\n\t纬度:" + latitude
						+ "\t经度:" + longitude + "\n\t精度:" + accuracy
						+"\n\t速度:"+speed+ "\n\t更新时间:" + dateString);
				int[] mPNR=bundledata.getIntArray("pnr");
				float[] mSNR=bundledata.getFloatArray("snr");
//				BarChartView.BarChartItemBean[] items = new BarChartView.BarChartItemBean[255];
//				for(int i=0; i<255;i++){
//					if(mPNR[i]!=0&&mSNR[i]!=0.0f)
//					items[i]=new BarChartView.BarChartItemBean(String.valueOf(mPNR[i]), mSNR[i]);
//				}

				for(int i=0;i<mPNR.length;i++){
					if(mPNR[i]!=0)
						Log.i("item",String.valueOf(mPNR[i]));
				}
//               mBarChartView.setItems(items);
				BarChartView barChartView = (BarChartView) findViewById(R.id.bar_chart);
				BarChartView.BarChartItemBean[] items = new BarChartView.BarChartItemBean[]{

						new BarChartView.BarChartItemBean("8", 30),
						new BarChartView.BarChartItemBean("11", 15),
						new BarChartView.BarChartItemBean("14", 60),
						new BarChartView.BarChartItemBean("17", 60),
						new BarChartView.BarChartItemBean("27", 60),
						new BarChartView.BarChartItemBean("31", 45),
						new BarChartView.BarChartItemBean("37", 50),
						new BarChartView.BarChartItemBean("47", 30),
						new BarChartView.BarChartItemBean("57", 20),
						new BarChartView.BarChartItemBean("67", 10),
						new BarChartView.BarChartItemBean("77", 45),
						new BarChartView.BarChartItemBean("87", 60),
						new BarChartView.BarChartItemBean("97", 60),
						new BarChartView.BarChartItemBean("100", 60),
						new BarChartView.BarChartItemBean("101", 58),
						new BarChartView.BarChartItemBean("102", 46),
						new BarChartView.BarChartItemBean("108", 26),
						new BarChartView.BarChartItemBean("112", 33),

				};
				barChartView.setItems(items);
			}

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
