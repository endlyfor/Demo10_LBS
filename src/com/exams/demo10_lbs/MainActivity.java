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
	DataReceiver dataReceiver;// BroadcastReceiver����
	public LocationManager lManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startbtnButton = (Button) findViewById(R.id.Startbtn);
		stopButton = (Button) findViewById(R.id.Stopbtn);
		
		tView = (TextView) findViewById(R.id.tv);

		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// �ж�GPS�Ƿ���������
		if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "�뿪��GPS����...", Toast.LENGTH_SHORT).show();
			// ���ؿ���GPS�������ý���
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
			IntentFilter filter = new IntentFilter();// ����IntentFilter����
			filter.addAction("com.exams.demo10_lbs");
			registerReceiver(dataReceiver, filter);// ע��Broadcast Receiver
		}
	}

	private void stopService() {
		startbtnButton.setEnabled(true);
		stopButton.setEnabled(false);
		Intent i = new Intent(this, LBSService.class);
		this.stopService(i);
		Log.i(TAG, "in stopService method.");
		if (dataReceiver != null) {
			unregisterReceiver(dataReceiver);// ȡ��ע��Broadcast Receiver
			dataReceiver = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class DataReceiver extends BroadcastReceiver {// �̳���BroadcastReceiver������

		@Override
		public void onReceive(Context context, Intent intent) {// ��дonReceive����

			Bundle bundledata = intent.getExtras();
			if (bundledata != null) {
				String latitude = bundledata.getString("latitude");
				String longitude = bundledata.getString("longitude");
				String accuracy = bundledata.getString("accuracy");
				String speed=bundledata.getString("speed");
				String Satenum = bundledata.getString("Satenum");
				String dateString = bundledata.getString("date");
				tView.setText("\t������������:" + Satenum + "\n\tγ��:" + latitude
						+ "\t����:" + longitude + "\n\t����:" + accuracy
						+"\n\t�ٶ�:"+speed+ "\n\t����ʱ��:" + dateString);
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
