package com.exams.demo10_lbs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
	public static final int CMD_STOP_SERVICE = 0;

	public static final String TAG = "MainActivity";
	public Button startbtnButton, stopButton;
	public TextView tView;
	DataReceiver dataReceiver;// BroadcastReceiver对象
	public LocationManager lManager;
	public BarChartView mBarChartView;
	private String mTmpSatenum="0";
	private Switch mSwitch;  //GPS on/off switch
	boolean mStarted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startbtnButton = (Button) findViewById(R.id.Startbtn);
		stopButton = (Button) findViewById(R.id.Stopbtn);
		
		tView = (TextView) findViewById(R.id.tv);


		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


		// 判断GPS是否正常启动
		if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			return;
		}

		BarChartView barChartView = (BarChartView) findViewById(R.id.bar_chart);
		BarChartView.BarChartItemBean[] items = new BarChartView.BarChartItemBean[]{

				new BarChartView.BarChartItemBean("8", 30),


		};
		barChartView.setItems(items);

		startbtnButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Point point = new Point();
				getWindowManager().getDefaultDisplay().getSize(point);
				Toast.makeText(MainActivity.this,point.toString(),Toast.LENGTH_LONG).show();
				Log.d(TAG,"the screen size is "+point.toString());
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
		getMenuInflater().inflate(R.menu.gps_menu, menu);
		initGpsSwitch(menu);
		return true;
	}

	private void initGpsSwitch(Menu menu) {
		MenuItem item = menu.findItem(R.id.gps_switch);
		if (item != null) {
			mSwitch = (Switch) MenuItemCompat
					.getActionView(item);
			if (mSwitch != null) {
				// Initialize state of GPS switch before we set the listener, so we don't double-trigger start or stop
				mSwitch.setChecked(mStarted);

				// Set up listener for GPS on/off switch, since custom menu items on Action Bar don't play
				// well with ABS and we can't handle in onOptionsItemSelected()
				mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// Turn GPS on or off
						if (!isChecked && mStarted) {

						} else {
							if (isChecked && !mStarted) {

							}
						}
					}
				});
			}
		}
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

	/*** 去除0卫星 **/
//				if(!Satenum.equals("0")){
//					mTmpSatenum=Satenum;
//				}else{
//					Satenum=mTmpSatenum;
//				}

				tView.setText("\t卫星在用数量:" + Satenum + "\n\t纬度:" + latitude
						+ "\t经度:" + longitude + "\n\t精度:" + accuracy
						+"\n\t速度:"+speed+ "\n\t更新时间:" + dateString);
				int[] mPNR=bundledata.getIntArray("pnr");
				float[] mSNR=bundledata.getFloatArray("snr");
				mBarChartView=(BarChartView)findViewById(R.id.bar_chart);

			   ArrayList<BarChartView.BarChartItemBean> mArrayItems=new ArrayList<BarChartView.BarChartItemBean>();
				for(int i=0; i<mPNR.length;i++){
					if(mPNR[i]!=0&&mSNR[i]!=0.0f)
					//items[i]=new BarChartView.BarChartItemBean(String.valueOf(mPNR[i]), mSNR[i]);
					mArrayItems.add(new BarChartView.BarChartItemBean(String.valueOf(mPNR[i]), mSNR[i]));
				}

				BarChartView.BarChartItemBean[] items =new BarChartView.BarChartItemBean[mArrayItems.size()];
				int count=0;
				for(BarChartView.BarChartItemBean tmp:mArrayItems){
					items[count]=tmp;
					count++;
				}
//
				for(int i=0;i<mPNR.length;i++){
					if(mPNR[i]!=0)
						Log.i("item",String.valueOf(mPNR[i]));
				}
               mBarChartView.setItems(items);



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
