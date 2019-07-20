package jp.project.dev.gps;

import android.location.Location;
import android.os.Bundle;
import jp.project.dev.gps.nmea.NMEAUnit;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public interface GPSListener {
	/**
	 * 
	 * @param nmea
	 */
	void onNmeaReceived(NMEAUnit nmea);

	/**
	 * 
	 * @param location
	 */
	void onLocationChanged(Location location);

	/**
	 * 
	 * @param provider
	 */
	void onProviderDisabled(String provider);

	/**
	 * 
	 * @param provider
	 */
	void onProviderEnabled(String provider);

	/**
	 * 
	 * @param provider
	 * @param status
	 * @param extras
	 */
	void onStatusChanged(String provider, int status, Bundle extras);
}
