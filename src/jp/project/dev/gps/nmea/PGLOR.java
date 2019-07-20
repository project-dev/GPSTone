package jp.project.dev.gps.nmea;

import java.util.ArrayList;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class PGLOR extends NMEAUnit {

	/**
	 * 
	 * @param nmea
	 */
	public PGLOR(String nmea) {
		super(nmea);
	}

	@Override
	protected void analyze(String nmea) {
	}

	@Override
	public ArrayList<String> getNameList() {
		return null;
	}
}
