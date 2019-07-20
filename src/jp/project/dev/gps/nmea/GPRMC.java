package jp.project.dev.gps.nmea;

import java.util.ArrayList;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class GPRMC extends NMEAUnit {

	/**
	 * 
	 * @param nmea
	 */
	public GPRMC(String nmea) {
		super(nmea);
	}

	@Override
	protected void analyze(String nmea) {
		String[] arrNmea = splitNmea(nmea);
		// 先頭
		setData("KEY", arrNmea[0]);
		// 時間
		setData("TIME", arrNmea[1]);
		// ステータス
		setData("STATUS", arrNmea[2]);
		// 緯度
		setData("LATITUDE", arrNmea[3]);
		setData("LATITUDE_DIRECTION", arrNmea[4]);
		// 経度
		setData("LONGITUDE", arrNmea[5]);
		setData("LONGITUDE_DIRECTION", arrNmea[6]);
		// 移動速度
		setData("SPEED", arrNmea[7]);
		// 方位
		setData("DIRECTION", arrNmea[8]);
		// UTC 日付
		setData("DATE", arrNmea[9]);
		// 磁北と真北の間の角度の差(仰角?
		setData("AAA1", arrNmea[10]);
		// 磁北と真北の間の角度の差の方向。E = 東、W = 西
		setData("AAA2", arrNmea[11]);
		// モード
		setData("MODE", arrNmea[12]);
		// チェックサム
		setData("CHECKSUM", arrNmea[13]);
	}

	@Override
	public ArrayList<String> getNameList() {
		return null;
	}
}
