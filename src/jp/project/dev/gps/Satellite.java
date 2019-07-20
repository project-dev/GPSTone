package jp.project.dev.gps;

public class Satellite {

	// 衛星タイプ

	// 0:通常 1:準天頂衛星
	private int satelliteType = 0;
	
	// 衛星番号
	private int satelliteNo = -1;

	// 仰角
	private double angle = 0;
	
	// 方位
	private int direction = 0;
	
	// SNR(デシベル)
	private double snr = 0;
	
	public Satellite() {
	}

	/**
	 * 衛星タイプ
	 * 0:GPS衛星
	 * 1:準天頂衛星
	 * @return
	 */
	public int getSatelliteType(){
		return this.satelliteType;
	}

	/**
	 * 衛星タイプ
	 * 0:GPS衛星
	 * 1:準天頂衛星
	 * @param satelliteType
	 */
	public void setSatelliteType(int satelliteType){
		this.satelliteType = satelliteType;
	}

	/**
	 * 衛星番号
	 * @return
	 */
	public int getSatelliteNo(){
		return this.satelliteNo;
	}

	/**
	 * 衛星番号
	 * @param satelliteNo
	 */
	public void setSatelliteNo(int satelliteNo){
		this.satelliteNo = satelliteNo;
	}
	
	/**
	 * 仰角
	 * @return
	 */
	public double getAngle(){
		return this.angle;
	}

	/**
	 * 仰角
	 * @param angle
	 */
	public void setAngle(int angle){
		this.angle = angle;
	}

	/**
	 * 方位
	 * @return
	 */
	public int getDirection(){
		return this.direction;
	}

	/**
	 * 方位
	 * @param direction
	 */
	public void setDirection(int direction){
		this.direction = direction;
	}

	/**
	 * SNR
	 * @return
	 */
	public double getSnr(){
		return this.snr;
	}

	/**
	 * SNR
	 * @param snr
	 */
	public void setSnr(double snr){
		this.snr = snr;
	}
}
