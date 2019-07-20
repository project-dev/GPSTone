package jp.project.dev.gps.nmea;

import java.util.ArrayList;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class GPGGA extends NMEAUnit {

	/**
	 * 
	 * @param nmea
	 */
	public GPGGA(String nmea) {
		super(nmea);
	}

	@Override
	protected void analyze(String nmea) {
		String[] arrNmea = splitNmea(nmea);
		// 先頭
		setData("KEY", arrNmea[0]);

		// UTC 時間
		setData("TIME", arrNmea[1]);
		
		// 緯度
		setData("LATITUDE", arrNmea[2]);
		setData("LATITUDE_DIRECTION", arrNmea[3]);

		// 経度
		setData("LONGITUDE", arrNmea[4]);
		setData("LONGITUDE_DIRECTION", arrNmea[5]);

		// 位置特定品質
		setData("QUALITY", arrNmea[6]);
		
		// 使用衛星数
		setData("SATELLITES_NUM", arrNmea[7]);

		// HDOP
		setData("HDOP", arrNmea[8]);

		// アンテナの高さ
		setData("ANNTENA_HEIGHT", arrNmea[9]);
		setData("ANNTENA_HEIGHT_UNIT", arrNmea[10]);

		// ジオイドの高さ
		setData("GEOIDAL_HEIGHT", arrNmea[11]);
		setData("GEOIDAL_HEIGHT_UNIT", arrNmea[12]);

		// DGPSデータエイジ
		setData("DGPS_AGE", arrNmea[13]);

		// DGPS基準局のID
		setData("DGPS_ID", arrNmea[14]);

		// チェックサム
		setData("CHECKSUM", arrNmea[15]);
	}

	@Override
	public ArrayList<String> getNameList() {
		return null;
	}
}
