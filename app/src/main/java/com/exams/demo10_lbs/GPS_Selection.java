package com.exams.demo10_lbs;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class GPS_Selection extends Activity {

    public CheckBox mCheckBox_china,mCheckBox_japan,mCheckBox_usa,mCheckBox_russia,mCheckBox_galileo;
    public Button mButton_ok,mButton_cancel;
    public boolean[] array_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_selection);
        mCheckBox_china=(CheckBox)findViewById(R.id.checkbox1);
        mCheckBox_usa=(CheckBox)findViewById(R.id.checkbox2);
        mCheckBox_russia=(CheckBox)findViewById(R.id.checkbox3);
        mCheckBox_galileo=(CheckBox)findViewById(R.id.checkbox4);
        mCheckBox_japan=(CheckBox)findViewById(R.id.checkbox5);

        mButton_ok=(Button)findViewById(R.id.menu_gps_selection_button_ok);
        mButton_cancel=(Button)findViewById(R.id.menu_gps_selection_button_cancel);
        array_checkbox=new boolean[5];

        checkListener(mCheckBox_china,1);
        checkListener(mCheckBox_usa,2);
        checkListener(mCheckBox_russia,3);
        checkListener(mCheckBox_galileo,4);
        checkListener(mCheckBox_japan,5);




        mButton_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putBooleanArray("array_checkbox",array_checkbox);

//                Log.i("array_checkbox",String.valueOf(array_checkbox[0]));
//                Log.i("array_checkbox",String.valueOf(array_checkbox[1]));
//                Log.i("array_checkbox",String.valueOf(array_checkbox[2]));
//                Log.i("array_checkbox",String.valueOf(array_checkbox[3]));
//                Log.i("array_checkbox",String.valueOf(array_checkbox[4]));

                Intent intent=new Intent();
                intent.putExtras(bundle);
               GPS_Selection.this.setResult(RESULT_OK,intent);
                GPS_Selection.this.finish();

            }
        });

        mButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            GPS_Selection.this.finish();

            }
        });





    }


    public void checkListener( CheckBox checkBox, final int i){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch (i) {
                        case 1:
                            array_checkbox[i - 1] = true;
                            break;
                        case 2:
                            array_checkbox[i - 1] = true;
                            break;
                        case 3:
                            array_checkbox[i - 1] = true;
                            break;
                        case 4:
                            array_checkbox[i - 1] = true;
                            break;
                        case 5:
                            array_checkbox[i - 1] = true;
                            break;

                    }
                } else {

                    switch (i) {
                        case 1:
                            array_checkbox[i - 1] = false;
                            break;
                        case 2:
                            array_checkbox[i - 1] = false;
                            break;
                        case 3:
                            array_checkbox[i - 1] = false;
                            break;
                        case 4:
                            array_checkbox[i - 1] = false;
                            break;
                        case 5:
                            array_checkbox[i - 1] = false;
                            break;

                    }
                }
            }
        });
    }
}

