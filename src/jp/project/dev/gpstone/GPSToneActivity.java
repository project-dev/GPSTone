package jp.project.dev.gpstone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import jp.project.dev.gps.GPSListener;
import jp.project.dev.gps.GPSManager;
import jp.project.dev.gps.Satellite;
import jp.project.dev.gps.nmea.NMEAUnit;
import jp.project.dev.tone.TONE;
import jp.project.dev.tone.ToneGenerator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class GPSToneActivity extends Activity implements GPSListener, Runnable{

	private boolean isDebug = false;
	
	/**
	 * 
	 */
	private ArrayList<String> infoList = null;

	/**
	 * 
	 */
	private GPSManager gpsMan = null;
	
	/**
	 * 
	 */
	private ToneGenerator tg = null;

	// 緯度
	private double latitude = 0;
	// 経度
	private double longitude = 0;
	// 精度(m)
	private double accuracy = 0;
	// 標高(m)
	private double altitude = 0;
	// 時間(UTC時間)
	private long time = 0;
	// 速度(m/秒)
	private float speed = 0;
	// 方位(北が０で時計回りに増加します。)
	private float bearing = 0;

	private Thread thread = null;

	private double[] circleCnt = new double[]{
			0, 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48, 51
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LayoutParams prms = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);	
        setContentView(BaseSurfaceView.createView(getApplicationContext()), prms);
        gpsMan = new GPSManager(this, this);
	}

	/**
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		if(tg == null){
//			tg = new ToneGenerator(44100, 1, 16);
			tg = new ToneGenerator(22050, 1, 8);
		}
		if(gpsMan != null){
			// ロケーションマネージャを更新
			gpsMan.requestLocationUpdates();
		}
		thread = new Thread(this);
		thread.start();
		super.onResume();
	}

	/**
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		if(tg != null){
			tg.release();
			tg = null;
		}
		if(gpsMan != null){
			gpsMan.removeUpdates();
		}
		if(thread != null){
			thread = null;
		}
		super.onPause();
	}

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onNmeaReceived(NMEAUnit nmea) {
		if(infoList == null){
			infoList = new ArrayList<String>();
		}
		if(nmea.get("KEY").equals("$QZGSV")){
			infoList.add(nmea.getNmea());
		}
		while(infoList.size() > 30){
			infoList.remove(0);
		}
	
		Log.d("GPSTone", nmea.getNmea());
		
		//drawUpdate();
	}

	@Override
	public void onLocationChanged(Location location) {
		// 緯度
		latitude = location.getLatitude();
		// 経度
		longitude = location.getLongitude();
		// 精度(m)
		accuracy = location.getAccuracy();
		// 標高(m)
		altitude = location.getAltitude();
		// 時間(UTC時間)
		time = location.getTime();
		// 速度(m/秒)
		speed = location.getSpeed();
		// 方位(北が０で時計回りに増加します。)
		bearing = location.getBearing();

		
		//drawUpdate();
	}

	private void drawSatellite(Satellite[] arrSatellite){
		Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
		if(arrSatellite == null){
			return;
		}
		for(Satellite satellite : arrSatellite){
			// 方位・・・中心からの距離
			int direction = satellite.getDirection();
			// 仰角・・・円周上の位置
			double angle = satellite.getAngle();
			
			// 底辺を求める
			// 角度 = 斜辺  * cos(角度)
			// 角度 = 方位  * cos(仰角)
			double baseLine = direction * Math.cos(angle);
			
			// 高さを求める
			// 高さ = 斜辺 * sin(角度)
			// 高さ = 方位  * sin(仰角)
			double height = direction * Math.sin(angle);

			// 円を描く
			if(satellite.getSatelliteType() == 1){
				pt.setColor(Color.rgb(0x00, 0xFF, 0x00));
			}else{
				pt.setColor(Color.rgb(0x00, 0xAF, 0xAF));
			}
			pt.setStyle(Style.STROKE);
			Point point = BaseSurfaceView.getViewCenterPoint();
			float x = (float)(point.x + baseLine);
			float y = (float)(point.y + height);
			BaseSurfaceView.drawCircle(x, y, 10, pt);

			pt.setColor(Color.WHITE);
			pt.setTextSize(16);
			String sno = Integer.toString(satellite.getSatelliteNo());
			float width = pt.measureText(sno) / 2;
			BaseSurfaceView.drawText(sno, (int)(x-width), (int)y-5, pt);
		}
	}
	
	/**
	 * 
	 */
	private void drawUpdate(){
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float scale = metrics.scaledDensity;
		
		BaseSurfaceView.drawBegin();
		Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);

		//　速度に関係する描画
		double kh = speed / 1000.0 * 60.0 * 60.0 ;
		double addValue = (double)((kh > 49 ? 50 : (kh + 1)) / 50);
		for(int i = 0; i < circleCnt.length; i++){
			circleCnt[i] += addValue ; 
			if(circleCnt[i] > 2.0 * circleCnt.length - 1){
				circleCnt[i] = 0.0;
			}
			if(gpsMan.getQzssSatelliteCount() > 0){
				pt.setColor(Color.rgb(0,  255 - (int)(circleCnt[i] * 3),  0));
			}else{
				pt.setColor(Color.rgb(0,  0,  255 - (int)(circleCnt[i] * 3)));
			}
			pt.setStyle(Style.STROKE);
			BaseSurfaceView.drawCircleCenter((float)(circleCnt[i] * 10.0), pt);
		}

		try{
			if(gpsMan.getGPSSatelliteCount() > 0){
				Satellite[] arrSatellite = gpsMan.getGPSSatellite();
				drawSatellite(arrSatellite);
			}		
			if(gpsMan.getQzssSatelliteCount() > 0){
				Satellite[] arrSatellite = gpsMan.getQzssSatellite();
				drawSatellite(arrSatellite);
			}		
		}catch(Exception e){
			Log.e("GPSTone", "", e);
		}

		pt.setColor(Color.WHITE);
		pt.setTextSize(40);
		String speedText = String.format("Speed %.2f k/h", kh);
		float strWidth = pt.measureText(speedText);
		Point pos = BaseSurfaceView.getViewCenterPoint();
		BaseSurfaceView.drawText(speedText, (int)(pos.x - strWidth / 2), pos.y + (int)(pos.y / 2), pt);
		
		// デバッグ
		if(isDebug == true){
			pt.setColor(Color.rgb(0x4F, 0x4F, 0x4F));
			pt.setTextSize(10 * scale);

			int y = (int)(80 * scale);
			try{
				if(infoList != null){
					for(String info : infoList){
						BaseSurfaceView.drawText(info, (int)(10 * scale), (int)(y * scale), pt);
						y += (int)(12 * scale);
					}
				}
			}catch(Exception e){
			}
			pt = new Paint(Paint.ANTI_ALIAS_FLAG);
			pt.setColor(Color.WHITE);
			pt.setTextSize(10 * scale);
			BaseSurfaceView.drawText(String.format("緯度 %f",   latitude),  (int)(20 * scale), (int)(120 * scale), pt);
			BaseSurfaceView.drawText(String.format("経度 %f",   longitude), (int)(20 * scale), (int)(150 * scale), pt);
			BaseSurfaceView.drawText(String.format("精度 %f m", accuracy),  (int)(20 * scale), (int)(180 * scale), pt);
			BaseSurfaceView.drawText(String.format("標高 %f m", altitude),  (int)(20 * scale), (int)(210 * scale), pt);
			BaseSurfaceView.drawText(String.format("時間 %d",   time),      (int)(20 * scale), (int)(240 * scale), pt);
			BaseSurfaceView.drawText(String.format("速度 %f m/sec", speed), (int)(20 * scale), (int)(270 * scale), pt);
			BaseSurfaceView.drawText(String.format("方位 %f",   bearing),   (int)(20 * scale), (int)(300 * scale), pt);

			Satellite[] arrGpsSatellite = gpsMan.getGPSSatellite();
			Satellite[] arrQzssSatellite = gpsMan.getQzssSatellite();
			y = 200;
			if(arrGpsSatellite != null){
				Arrays.sort(arrGpsSatellite, new Comparator<Satellite>(){
					public int compare(Satellite satellite1, Satellite satellite2) {
						if(satellite1.getSatelliteNo() > satellite2.getSatelliteNo()){
							return 1;
						}else if(satellite1.getSatelliteNo() < satellite2.getSatelliteNo()){
							return -1;
						}else{
							return 0;
						}
					}
				});
				for(Satellite st : arrGpsSatellite){
					String text = String.format("GPS  %d: %f, %d", st.getSatelliteNo(), st.getAngle(), st.getDirection());
					BaseSurfaceView.drawText(text, (int)(150 * scale), y, pt);
					y += 20 * scale;
				}
			}
			if(arrQzssSatellite != null){
				Arrays.sort(arrQzssSatellite, new Comparator<Satellite>(){
					public int compare(Satellite satellite1, Satellite satellite2) {
						if(satellite1.getSatelliteNo() > satellite2.getSatelliteNo()){
							return 1;
						}else if(satellite1.getSatelliteNo() < satellite2.getSatelliteNo()){
							return -1;
						}else{
							return 0;
						}
					}
				});
				for(Satellite st : arrQzssSatellite){
					String text = String.format("QZSS %d: %f, %d", st.getSatelliteNo(), st.getAngle(), st.getDirection());
					BaseSurfaceView.drawText(text, (int)(150 * scale), y, pt);
					y += 12 * scale;
				}
			}
		}
		BaseSurfaceView.drawEnd();
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void run() {
		int counter = 0;
		int cotarb = 0;
		//int spFlag = 0;
		int seqCnt = 0;
		int toneCnt = 0;
		int spBase = 3;
		// 1:モード
		// 2:音階
		TONE[][] tones = new TONE[][]{
			 {
				  TONE.C2, TONE.NONE, TONE.C2, TONE.NONE, TONE.C2
			 }
			// GPSモード
			,{
				  TONE.A1,  TONE.NONE, TONE.A1,  TONE.NONE, TONE.C2,  TONE.NONE, TONE.A1,  TONE.NONE
				 ,TONE.D2s, TONE.NONE, TONE.D2s, TONE.NONE, TONE.D2,  TONE.NONE, TONE.C2,  TONE.NONE
				 ,TONE.A1,  TONE.NONE, TONE.A1,  TONE.NONE, TONE.C2,  TONE.NONE, TONE.D2,  TONE.NONE
				 ,TONE.D2s, TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE

				 ,TONE.A1,  TONE.NONE, TONE.A1,  TONE.NONE, TONE.C2,  TONE.NONE, TONE.A1,  TONE.NONE
				 ,TONE.D2s, TONE.NONE, TONE.D2s, TONE.NONE, TONE.D2,  TONE.NONE, TONE.C2,  TONE.NONE
				 ,TONE.A1,  TONE.NONE, TONE.A1,  TONE.NONE, TONE.C2,  TONE.NONE, TONE.D2,  TONE.NONE
				 ,TONE.D2s, TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE

				 ,TONE.D2,  TONE.NONE, TONE.D2,  TONE.NONE, TONE.F2,  TONE.NONE, TONE.G2,  TONE.NONE
				 ,TONE.G2s, TONE.NONE, TONE.G2s, TONE.NONE, TONE.G2,  TONE.NONE, TONE.F2,  TONE.NONE
				 ,TONE.D2,  TONE.NONE, TONE.D2,  TONE.NONE, TONE.F2,  TONE.NONE, TONE.G2,  TONE.NONE
				 ,TONE.G2s, TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE
			}
			// QZSSモード
			,{
				  TONE.A2,  TONE.NONE, TONE.A2,  TONE.NONE, TONE.C3,  TONE.NONE, TONE.A2,  TONE.NONE
				 ,TONE.D3s, TONE.NONE, TONE.D3s, TONE.NONE, TONE.D3,  TONE.NONE, TONE.C3,  TONE.NONE
				 ,TONE.A2,  TONE.NONE, TONE.A2,  TONE.NONE, TONE.C3,  TONE.NONE, TONE.D3,  TONE.NONE
				 ,TONE.D3s, TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE

				 ,TONE.A2,  TONE.NONE, TONE.A2,  TONE.NONE, TONE.C3,  TONE.NONE, TONE.A2,  TONE.NONE
				 ,TONE.D3s, TONE.NONE, TONE.D3s, TONE.NONE, TONE.D3,  TONE.NONE, TONE.C3,  TONE.NONE
				 ,TONE.A2,  TONE.NONE, TONE.A2,  TONE.NONE, TONE.C3,  TONE.NONE, TONE.D3,  TONE.NONE
				 ,TONE.D3s, TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE

				 ,TONE.D3,  TONE.NONE, TONE.D3,  TONE.NONE, TONE.F3,  TONE.NONE, TONE.G3,  TONE.NONE
				 ,TONE.G3s, TONE.NONE, TONE.G3s, TONE.NONE, TONE.G3,  TONE.NONE, TONE.F3,  TONE.NONE
				 ,TONE.D3,  TONE.NONE, TONE.D3,  TONE.NONE, TONE.F3,  TONE.NONE, TONE.G3,  TONE.NONE
				 ,TONE.G3s, TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE, TONE.NONE,TONE.NONE
			}
		};
		
		while(thread != null){
			try {
				Thread.sleep(33);

				counter++;
				try{
					if(gpsMan.getQzssSatelliteCount() > 0){
						if(cotarb != 2 && seqCnt % spBase == 0){
							seqCnt = 0;
							cotarb = 2;
						}
					}else if(gpsMan.getGPSSatelliteCount() > 0){
						if(cotarb != 1 && seqCnt % spBase == 0){
							seqCnt = 0;
							cotarb = 1;
						}
					}else{
						if(cotarb != 0 && seqCnt % spBase == 0){
							seqCnt = 0;
							cotarb = 0;
						}
					}
					// m/sec→ km/hに変換
					double sp = ((double)speed) / 1000.0 * 60.0 * 60.0;
					spBase = (int)((100 - sp) / 10);
					if(spBase < 1){
						spBase = 1;
					}
					tg.toneOn(tones[cotarb][toneCnt], 100);
					seqCnt++;
					if(seqCnt % spBase == 0){
						toneCnt++;
						seqCnt = 0;
						if(toneCnt >= tones[cotarb].length){
							toneCnt = 0;
						}
					}
				}catch(Exception e){
					Log.e("GPSTone", "", e);
				}
				
				if(counter >= 8){
					counter = 0;
				}
				if(counter % 4 == 0){
					drawUpdate();
				}

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
