package jp.project.dev.gps.nmea;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public abstract class NMEAUnit {

	/**
	 * 
	 */
	private String nmea = null;

	/**
	 * 
	 */
	private HashMap<String, String> nmeaMap = null;

	/**
	 * 
	 * @param nmea
	 * @return
	 */
	public static String[] splitNmea(String nmea){
		return nmea.split("[,\\*]");
	}

	/**
	 * 
	 * @param nmea
	 */
	public NMEAUnit(String nmea) {
		this.nmea = nmea;
		nmeaMap = new HashMap<String, String>();
		String[] arrNmea = splitNmea(nmea);
		Log.d("GPSTone", arrNmea[0]);
		analyze(nmea);
	}

	/**
	 * 
	 * @param nmea
	 */
	protected abstract void analyze(String nmea);

	/**
	 * 
	 * @return
	 */
	public abstract ArrayList<String> getNameList();

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getData(String name){
		return nmeaMap.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void setData(String name, String value){
		nmeaMap.put(name, value);
	}

	/**
	 * 
	 * @return
	 */
	public String getNmea(){
		return nmea;
	}

	public String get(String string) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
