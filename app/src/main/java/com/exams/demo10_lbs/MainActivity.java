package com.exams.demo10_lbs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.logging.Handler;

public class MainActivity extends Activity {
    public static final int CMD_STOP_SERVICE = 0;

    public static final String TAG = "MainActivity";
    public Button startbtnButton, stopButton;
    public TableLayout tView;
    public TextView lat,lat_label,lng,lng_label,accuracy_lable,accuracy,speed_label,speed,satnum_inuse_label,satnum_inuse,satnum_inview_label,satnum_inview,time;
    public TableRow row_1;
    DataReceiver dataReceiver;// BroadcastReceiver对象
    public LocationManager lManager;
    public BarChartView mBarChartView;
    private String mTmpSatenum = "0";
    private Switch mSwitch;  //GPS on/off switch
    boolean mStarted;
    ArrayList<BarChartView.BarChartItemBean> mArrayItems;
    boolean[] array_checkbox;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        startbtnButton = (Button) findViewById(R.id.Startbtn);
//        stopButton = (Button) findViewById(R.id.Stopbtn);
//
//        mBarChartView = (BarChartView) findViewById(R.id.bar_chart);
//        tView = (TableLayout) findViewById(R.id.tv1);
//        row_1=(TableRow)findViewById(R.id.row_1);
//        lat_label=(TextView)findViewById(R.id.lat_label);
//        lat=(TextView)findViewById(R.id.lat);
//        lng_label=(TextView)findViewById(R.id.lng_label);
//        lng=(TextView)findViewById(R.id.lng);





        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        Toast.makeText(MainActivity.this, point.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "the screen size is " + point.toString());


        // 判断GPS是否正常启动
        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }


        startService();
//        tView.setText("\t卫星在用数量:" + " " + "\n\t纬度:" + " "
//                + "\t经度:" + " " + "\n\t精度:" + " "
//                + "\t速度:" + " " + "\t更新时间:" + " ");


    }

    private void startService() {
        //  startbtnButton.setEnabled(false);
        //  stopButton.setEnabled(true);
        Intent i = new Intent(this, LBSService.class);
        this.startService(i);
        Log.i(TAG, "in startService method.");
        if (dataReceiver == null) {
            dataReceiver = new DataReceiver();
            IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
            filter.addAction("com.exams.demo10_lbs");
            registerReceiver(dataReceiver, filter);// 注册Broadcast Receiver
           // mStarted = true;
        }
    }

    private void stopService() {
        //  startbtnButton.setEnabled(true);
        //   stopButton.setEnabled(false);

        Intent intent = new Intent();// 创建Intent对象
        intent.setAction("com.exams.demo10_lbs.LBSService");
        intent.putExtra("cmd",CMD_STOP_SERVICE);
        sendBroadcast(intent);
        Log.i(TAG, "in stopService method.");
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);// 取消注册Broadcast Receiver
            dataReceiver = null;
          //  mStarted = false;
        }
        Intent i = new Intent(this, LBSService.class);
        this.stopService(i);
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
                mSwitch.setChecked(true);

                // Set up listener for GPS on/off switch, since custom menu items on Action Bar don't play
                // well with ABS and we can't handle in onOptionsItemSelected()
                mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Turn GPS on or off
                        if (!isChecked) {
                           setContentView(R.layout.activity_main);
                            stopService();

                        } else {
                            if (isChecked ) {

                                startService();
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
            setContentView(R.layout.activity_main);

            mBarChartView = (BarChartView) findViewById(R.id.bar_chart);
            tView = (TableLayout) findViewById(R.id.tv1);
            row_1=(TableRow)findViewById(R.id.row_1);
            lat_label=(TextView)findViewById(R.id.lat_label);
            lat=(TextView)findViewById(R.id.lat);
            lng_label=(TextView)findViewById(R.id.lng_label);
            lng=(TextView)findViewById(R.id.lng);
            accuracy=(TextView)findViewById(R.id.accuracy);
            speed=(TextView) findViewById(R.id.speed);
            satnum_inview=(TextView) findViewById(R.id.satnum_inview);
            satnum_inuse=(TextView) findViewById(R.id.satnum_inuse);
            time=(TextView)findViewById(R.id.time);

            Bundle bundledata = intent.getExtras();
            if (bundledata != null) {
                String Latitude = bundledata.getString("latitude");
                String Longitude = bundledata.getString("longitude");
                String Accuracy = bundledata.getString("accuracy");
                String Speed = bundledata.getString("speed");
                String Satenum_inUse = bundledata.getString("Satenum_inUse");
                String Satenum_inView = bundledata.getString("Satenum_inView");
                String DateString = bundledata.getString("date");
                int[] mPNR = bundledata.getIntArray("pnr");
                float[] mSNR = bundledata.getFloatArray("snr");
                /*** 去除0卫星 **/
//				if(!Satenum.equals("0")){
//					mTmpSatenum=Satenum;
//				}else{
//					Satenum=mTmpSatenum;
//				}





                    mArrayItems = new ArrayList<BarChartView.BarChartItemBean>();
                    for (int i = 0; i < mPNR.length; i++) {
                        if (mPNR[i] != 0 && mSNR[i] != 0.0f)
                            //items[i]=new BarChartView.BarChartItemBean(String.valueOf(mPNR[i]), mSNR[i]);
                            mArrayItems.add(new BarChartView.BarChartItemBean(String.valueOf(mPNR[i]), mSNR[i]));
                    }

                    BarChartView.BarChartItemBean[] items = new BarChartView.BarChartItemBean[mArrayItems.size()];
                    int count = 0;
                    for (BarChartView.BarChartItemBean tmp : mArrayItems) {
                        items[count] = tmp;
                        count++;
                    }
//                    tView.setText("\t卫星在用数量:" + Satenum + "\n\t纬度:" + latitude
//                            + "\t经度:" + longitude + "\n\t精度:" + accuracy
//                            + "\t速度:" + speed + "\t更新时间:" + dateString);
//                String string=getResources().getString(R.string.lat);
//
//                String string1=String.format(string,Float.valueOf(latitude));
               // String.format(getString(R.string.lat), Float.valueOf(latitude));

                lat.setText(Latitude);
                lng.setText(Longitude);
                accuracy.setText(Accuracy);
                speed.setText(Speed);
                satnum_inview.setText(Satenum_inView);
                satnum_inuse.setText(Satenum_inUse);
                time.setText(DateString);


               // lng.setText(longitude);

                for (int i = 0; i < mPNR.length; i++) {
                    if (mPNR[i] != 0)
                        Log.i("item", String.valueOf(mPNR[i]));
                }


                    mBarChartView.setItems(items,array_checkbox);
                }


            }

        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean success;
        // Handle menu item selection
        switch (item.getItemId()) {
            case R.id.gps_switch:
                // Do nothing - this is handled by a separate listener added in onCreateOptionsMenu()
                return true;
            case R.id.menu_settings:
                /**
                 * 待处理
                 */

                return true;
            case R.id.menu_gps_selection:
                Intent intent=new Intent(MainActivity.this, GPS_Selection.class);
                startActivityForResult(intent,0);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 0:         // 子窗口ChildActivity的回传数据

                if (resultCode==RESULT_OK) {

                    array_checkbox = data.getBooleanArrayExtra("array_checkbox");
                    Log.i("array_checkbox",String.valueOf(array_checkbox[0]));
                    Log.i("array_checkbox",String.valueOf(array_checkbox[1]));
                    Log.i("array_checkbox",String.valueOf(array_checkbox[2]));
                    Log.i("array_checkbox",String.valueOf(array_checkbox[3]));
                    Log.i("array_checkbox",String.valueOf(array_checkbox[4]));
                    if (array_checkbox != null) {
                        //处理代码在此地
                      // Toast.makeText(this,String.valueOf(array_checkbox[0]), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                //其它窗口的回传数据
                break;
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
