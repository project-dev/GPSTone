package jp.project.dev.gps.nmea;

import java.util.ArrayList;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class GPZDA extends NMEAUnit {

	/**
	 * 
	 * @param nmea
	 */
	public GPZDA(String nmea) {
		super(nmea);
	}

	@Override
	protected void analyze(String nmea) {
		String[] arrNmea = splitNmea(nmea);
		// 先頭
		setData("KEY", arrNmea[0]);
	}

	@Override
	public ArrayList<String> getNameList() {
		return null;
	}
}
