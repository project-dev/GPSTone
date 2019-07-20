package jp.project.dev.gps.nmea;

import java.util.ArrayList;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class GSA extends NMEAUnit {

	/**
	 * 
	 * @param nmea
	 */
	public GSA(String nmea) {
		super(nmea);
	}

	@Override
	protected void analyze(String nmea) {
		String[] arrNmea = splitNmea(nmea);
		// 先頭
		setData("KEY", arrNmea[0]);
		// モード
		setData("MODE", arrNmea[1]);
		// 特定タイプ
		setData("TYPE", arrNmea[2]);

		// 衛星01~12
		for(int i = 0; i < 12; i++){
			setData(String.format("STATELLITE%d", i), arrNmea[3 + i]);
		}
		// 位置精度低下率
		//TODO:キー名称修正する
		setData("AAA1", arrNmea[15]);
		// 水平精度低下率
		//TODO:キー名称修正する
		setData("AAA2", arrNmea[16]);
		// 垂直精度低下率
		//TODO:キー名称修正する
		setData("AAA3", arrNmea[17]);
		// チェックサム
		setData("CHECKSUM", arrNmea[18]);
	}

	@Override
	public ArrayList<String> getNameList() {
		return null;
	}
}
