package jp.project.dev.gps.nmea;

import java.util.ArrayList;

/**
 * 
 * @author TAKA@はままつ
 * 
 *
 */
public class GSV extends NMEAUnit {

	/**
	 * 
	 * @param nmea
	 */
	public GSV(String nmea) {
		super(nmea);
	}

	@Override
	protected void analyze(String nmea) {
		String[] arrNmea = splitNmea(nmea);
		// 先頭
		setData("KEY", arrNmea[0]);
		if(arrNmea[0].equals("$QZGSV")){
			int ii = 0;
			ii++;
		}
		// 総センテンス数
		setData("SENTENCE_COUNT", arrNmea[1]);
		// センテンス番号
		setData("SENTENCE_NO", arrNmea[2]);
		// 衛星数
		setData("STATELLITENUM", arrNmea[3]);
		int num = Integer.parseInt(arrNmea[3]);

		int idx = 4;
		int satelliteCnt = 0;
		for(int i = 0; i < 4; i++){
			// 衛星番号
			setData(String.format("STATELLITE_NO_%d", i), arrNmea[idx]);
			// 仰角
			setData(String.format("ANGLE_%d", i), arrNmea[idx + 1]);
			// 方位
			setData(String.format("DIRECTION_%d", i), arrNmea[idx + 2]);
			// SNR
			setData(String.format("SNR_%d", i), arrNmea[idx + 3]);
			idx += 4;
			satelliteCnt++;
			if(satelliteCnt == num){
				break;
			}
		}
		// チェックサム
		setData("CHECKSUM", arrNmea[idx]);
	}

	@Override
	public ArrayList<String> getNameList() {
		return null;
	}
}
