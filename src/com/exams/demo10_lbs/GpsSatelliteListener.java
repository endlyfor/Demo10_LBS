package com.exams.demo10_lbs;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;

public class GpsSatelliteListener implements Listener {

	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		switch (event) {
		// ��һ�ζ�λ
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			break;
		// ����״̬�ı�
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			break;
		// ��λ����
		case GpsStatus.GPS_EVENT_STARTED:
			break;
		// ��λ����
		case GpsStatus.GPS_EVENT_STOPPED:
			break;
		}
	}

}
