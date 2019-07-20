package jp.project.dev.gps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import jp.project.dev.gps.nmea.GPGGA;
import jp.project.dev.gps.nmea.GPRMC;
import jp.project.dev.gps.nmea.GPZDA;
import jp.project.dev.gps.nmea.GSA;
import jp.project.dev.gps.nmea.GSV;
import jp.project.dev.gps.nmea.NMEAUnit;
import jp.project.dev.gps.nmea.PGLOR;
import android.content.Context;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class GPSManager implements LocationListener, NmeaListener, Listener {
	
	/**
	 * 衛星情報
	 */
	private ArrayList<Satellite> satellites = null;

	/**
	 * GPS衛星
	 */
	private HashMap<String, Satellite> gpSatelliteMap = null;

	/**
	 * 準天頂衛星
	 */
	private HashMap<String, Satellite> qzSatelliteMap = null;

	/**
	 * GPGGA:緯度
	 */
	private double latitude = 0.0;

	/**
	 * GPGGA:緯度(方角)
	 */
	private String latitudeDirection = ""; 
	
	/**
	 * GPGGA:経度
	 */
	private double longitude = 0.0;

	/**
	 * GPGGA:経度(方角)
	 */
	private String longitudeDirection = ""; 

	/**
	 * GPGGA:GPSのクオリティ
	 */
	private int quality = 0;
	
	/**
	 * GPGGA:使用衛星数
	 */
	private int satellitesCount = 0;

	/**
	 * GPGGA:水平精度低下率
	 */
	private double hdop = 0.0;
	
	/**
	 * GPGGA:アンテナの高さ
	 */
	private double anntenaHeight = 0.0;
	/**
	 * GPGGA:アンテナの高さの単位
	 */
	private String anntenaHeightUnit = "";
	
	/**
	 * GPGGA:ジオイドの高さ
	 */
	private double geoidalHeight = 0.0;
	/**
	 * GPGGA:ジオイドの高さの単位
	 */
	private String geoidalHeightUnit = "";

	/**
	 * DGPSデータの最後の有効なRTCM通信からの時間
	 */
	private String dgpsAge = "";
	
	/**
	 * 差動基準地点ID
	 */
	private String dgpsID = "";
	/**
	 * 
	 */
	private static HashMap<String, NMEANames> nmeaMap = new HashMap<String, NMEANames>(){
		private static final long serialVersionUID = 1L;

		{
			put("$PGLOR", NMEANames.PGLOR);
			put("$GNGSA", NMEANames.GNGSA);
			put("$GPGSA", NMEANames.GPGSA);
			put("$GPGGA", NMEANames.GPGGA);
			put("$GPGSV", NMEANames.GPGSV);
			put("$GPRMC", NMEANames.GPRMC);
			put("$GPZDA", NMEANames.GPZDA);
			put("$QZGSA", NMEANames.QZGSA);
			put("$QZGSV", NMEANames.QZGSV);
		}
	};

	/**
	 *  ロケーションマネージャ
	 */
	private LocationManager locManager = null;

	/**
	 * 
	 */
	private GPSListener listener = null;

	/**
	 * 
	 * @param owner
	 * @param listener
	 */
	public GPSManager(Context owner, GPSListener listener) {
		// ロケーションマネジャを取得
		locManager = (LocationManager)owner.getSystemService(Context.LOCATION_SERVICE);
		locManager.addNmeaListener(this);
		locManager.addGpsStatusListener(this);
		this.listener = listener;
		
	}

	public Satellite[] getGPSSatellite(){
		if(gpSatelliteMap == null){
			return null;
		}
		Collection<Satellite> values = gpSatelliteMap.values();
		Satellite[] satellites = new Satellite[values.size()];
		try{
			values.toArray(satellites);
		}catch(Exception ex){
			Log.e("GPSTone", "", ex);
			satellites = null;
		}
		return satellites;
	}

	public Satellite[] getQzssSatellite(){
		if(qzSatelliteMap == null){
			return null;
		}
		Collection<Satellite> values = qzSatelliteMap.values();
		Satellite[] satellites = new Satellite[values.size()];
		values.toArray(satellites);
		return satellites;
	}
	
	public int getGPSSatelliteCount(){
		if(gpSatelliteMap == null){
			return 0;
		}
		return gpSatelliteMap.size();
	}
	
	public int getQzssSatelliteCount(){
		if(qzSatelliteMap == null){
			return 0;
		}
		return qzSatelliteMap.size();
	}
	
	/**
	 * 
	 */
	public void requestLocationUpdates(){
		if(locManager != null){
			// ロケーションマネージャを更新
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
	}

	/**
	 * 
	 */
	public void removeUpdates(){
		if(locManager != null){
			locManager.removeUpdates(this);
		}
	}

	/**
	 * 
	 * @see android.location.GpsStatus.NmeaListener#onNmeaReceived(long, java.lang.String)
	 */
	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
		NMEAUnit unit = null;
		Log.d("NMEA", nmea);
		try{
			
			String[] nmeaArray = NMEAUnit.splitNmea(nmea);
			if(nmeaMap.containsKey(nmeaArray[0]) == false){
				return;
			}
			
			switch(nmeaMap.get(nmeaArray[0])){
			case PGLOR:
				// 製造情報
				unit = new PGLOR(nmea);
				break;

			case GNGSA:
				// Glonass の捕捉された衛星の番号
			case GPGSA:
				// 衛星番号
			case QZGSA:
				// 準天頂の捕捉された衛星の番号
				unit = new GSA(nmea);
				onGSA(unit);
				break;

			case GPGSV:
				// 1行で4つの衛星の位置
			case QZGSV:
				// 準天頂の衛星の位置情報
				unit = new GSV(nmea);
				onGSV(unit);
				break;
				
			case GPGGA:
				unit = new GPGGA(nmea);
				// 緯度
				try{
					latitude = Double.parseDouble(unit.getData("LATITUDE"));
				}catch(Exception e){
				}
				// 緯度(方角)
				latitudeDirection = unit.getData("LATITUDE_DIRECTION");
				// 経度
				try{
					longitude = Double.parseDouble(unit.getData("LONGITUDE"));
				}catch(Exception e){
				}
				// 経度(方角)
				longitudeDirection = unit.getData("LONGITUDE_DIRECTION");

				// 品質
				try{
					quality = Integer.parseInt(unit.getData("QUALITY"));
				}catch(Exception e){
				}

				// 衛星使用数
				try{
					satellitesCount = Integer.parseInt(unit.getData("SATELLITES_NUM"));
					if(satellites == null || satellitesCount != satellites.size()){
						satellites = new ArrayList<Satellite>();
						for(int i = 0; i < satellitesCount; i++){
							satellites.add(new Satellite());
						}
					}
				}catch(Exception e){
				}

				// HDOP
				try{
					hdop = Double.parseDouble(unit.getData("HDOP"));
				}catch(Exception e){
				}
				
				// アンテナの高さ
				try{
					anntenaHeight = Double.parseDouble(unit.getData("ANNTENA_HEIGHT"));
				}catch(Exception e){
				}

				// アンテナの高さの単位
				anntenaHeightUnit = unit.getData("ANNTENA_HEIGHT_UNIT");

				// ジオイドの高さ
				try{
					geoidalHeight = Double.parseDouble(unit.getData("GEOIDAL_HEIGHT"));
				}catch(Exception e){
				}

				// ジオイドの高さの単位
				geoidalHeightUnit = unit.getData("GEOIDAL_HEIGHT_UNIT");

				//
				dgpsAge = unit.getData("DGPS_AGE");

				//
				dgpsID = unit.getData("DGPS_ID");
				break;

			case GPRMC:
				// 測位された現在地(推奨)
				unit = new GPRMC(nmea);
				break;

			case GPZDA:
				unit = new GPZDA(nmea);
				break;
			}
			
			listener.onNmeaReceived(unit);
		}catch(Exception e){
		}
	}

	private void onGSV(NMEAUnit unit){
		String key = unit.getData("KEY");
		String sentenceNo = unit.getData("SENTENCE_NO");
		HashMap<String, Satellite> satelliteMap = null;
		int satlliteType = 0;
		if(key.equals("$GPGSV")){
			// GPS衛星
			//if(sentenceNo.equals("1") || gpSatelliteMap == null){
			if(gpSatelliteMap == null){
				gpSatelliteMap = new HashMap<String, Satellite>();
			}
			satelliteMap = gpSatelliteMap;
			satlliteType = 0;
		}else if(key.equals("$QZGSV")){
			//　準天頂衛星
//			if(sentenceNo.equals("1") || qzSatelliteMap == null){
			if(qzSatelliteMap == null){
				qzSatelliteMap = new HashMap<String, Satellite>();
			}
			satelliteMap = qzSatelliteMap;
			satlliteType = 1;
		}
		if(satelliteMap == null){
			return;
		}
		
		String satelliteNo = "";
		String angle = "";
		String direction = "";
		String snr = "";
		int satelliteNoVal = -1;
		int angleVal = 0;
		int directionVal = 0;
		double snrVal = 0;

		for(int i = 0; i < 4; i++){
			satelliteNo = unit.getData(String.format("STATELLITE_NO_%d", i));
			angle = unit.getData(String.format("ANGLE_%d", i));
			direction = unit.getData(String.format("DIRECTION_%d", i));
			snr = unit.getData(String.format("SNR_%d", i));
			
			Satellite satellite = new Satellite();

			satelliteNoVal = Integer.parseInt(satelliteNo);
			angleVal = Integer.parseInt(angle);
			directionVal = Integer.parseInt(direction);
			snrVal = Integer.parseInt(snr);

			satellite.setSatelliteType(satlliteType);
			satellite.setSatelliteNo(satelliteNoVal);
			satellite.setAngle(angleVal);
			satellite.setDirection(directionVal);
			satellite.setSnr(snrVal);
			satelliteMap.put(satelliteNo, satellite);
		}
	}
	
	private void onGSA(NMEAUnit unit){
		String key = unit.getData("KEY");
		HashMap<String, Satellite> satelliteMap = null;
		if(key.equals("$GPGSV")){
			// GPS衛星
			if(gpSatelliteMap == null){
				gpSatelliteMap = new HashMap<String, Satellite>();
			}
			satelliteMap = gpSatelliteMap;
		}else if(key.equals("$QZGSV")){
			if(qzSatelliteMap == null){
				qzSatelliteMap = new HashMap<String, Satellite>();
			}
			satelliteMap = qzSatelliteMap;
		}
		if(satelliteMap == null){
			return;
		}
		String satelliteNo = null;
		HashMap<String, Satellite> newMap = new HashMap<String, Satellite>();
		Satellite satellite = null;
		for(int i = 0; i < 12; i++){
			satelliteNo = unit.getData(String.format("STATELLITE%d",  i));
			satellite = satelliteMap.get(satelliteNo);
			newMap.put(satelliteNo, satellite);
		}
		satelliteMap.clear();
		satelliteMap.putAll(newMap);
	}
	
	/**
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		listener.onLocationChanged(location);
	}

	/**
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		listener.onProviderDisabled(provider);
	}

	/**
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		listener.onProviderEnabled( provider);
	}

	/**
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		listener.onStatusChanged(provider, status, extras);
	}

	/**
	 * 
	 * @see android.location.GpsStatus.Listener#onGpsStatusChanged(int)
	 */
	@Override
	public void onGpsStatusChanged(int event) {
		switch(event){
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			break;
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			break;
		}
	}

	/**
	 * @return satellites
	 */
	public ArrayList<Satellite> getSatellites() {
		return satellites;
	}

	/**
	 * @param satellites セットする satellites
	 */
	public void setSatellites(ArrayList<Satellite> satellites) {
		this.satellites = satellites;
	}

	/**
	 * NMEAで取得した緯度を取得
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * NMEAで取得した緯度の方位を設定
	 * @return latitudeDirection
	 */
	public String getLatitudeDirection() {
		return latitudeDirection;
	}

	/**
	 * NMEAで取得した経度を取得
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * NMEAで取得した経度の方位を取得
	 * @return longitudeDirection
	 */
	public String getLongitudeDirection() {
		return longitudeDirection;
	}

	/**
	 * NMEAで取得したGPSの精度を取得
	 * @return quality
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 * NMEAで取得した衛星の数を取得
	 * @return satellitesCount
	 */
	public int getSatellitesCount() {
		return satellitesCount;
	}

	/**
	 * NMEAで取得したHDOPを取得
	 * @return hdop
	 */
	public double getHdop() {
		return hdop;
	}

	/**
	 * NMEAで取得したアンテナの高さを取得
	 * @return anntenaHeight
	 */
	public double getAnntenaHeight() {
		return anntenaHeight;
	}

	/**
	 * NMEAで取得したアンテナの高さの単位を取得
	 * @return anntenaHeightUnit
	 */
	public String getAnntenaHeightUnit() {
		return anntenaHeightUnit;
	}

	/**
	 * NMEAで取得したジオイドの高さを取得
	 * @return geoidalHeight
	 */
	public double getGeoidalHeight() {
		return geoidalHeight;
	}

	/**
	 * NMEAで取得したジオイドの高さの単位を取得
	 * @return geoidalHeightUnit
	 */
	public String getGeoidalHeightUnit() {
		return geoidalHeightUnit;
	}

	/**
	 * NMEAで取得したDGPSデータエイジを取得
	 * @return dgpsAge
	 */
	public String getDgpsAge() {
		return dgpsAge;
	}

	/**
	 * NMEAで取得したDGPS基準局IDを取得
	 * @return dgpsID
	 */
	public String getDgpsID() {
		return dgpsID;
	}
}
