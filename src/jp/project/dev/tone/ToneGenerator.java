package jp.project.dev.tone;

import java.io.ByteArrayOutputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.util.Log;

/**
 * 音を出します。
 * 5ms単位の処理をしているので、負荷が高いかも。
 * モノラル、モノフォニックです。
 * いずれ和音に対応したい。
 * 
 * 参考:音の出し方
 * http://k-hiura.cocolog-nifty.com/blog/2011/07/java-6e01.html
 * http://naskit.com/2010/02/26/android%E3%81%A7%E9%9F%B3%E5%A3%B0%E5%87%A6%E7%90%862/
 * WAVEファイルのフォーマット
 * http://www.kk.iij4u.or.jp/~kondo/wave/
 * @author TAKA@はままつ
 */
public class ToneGenerator implements OnPlaybackPositionUpdateListener, Runnable{

	/** AudioTrack */
	private AudioTrack audioTrack = null;
	/** サンプリングレート */
	private int sampleRate = 44100;
//	/** チャンネル数 */
//	private int channel = 1;
//	/** ビットレート */
//	private int bitRate = 16;
	/** バッファサイズ */
	private int buffSize = 0;
	/** スレッド */
	private Thread thread = null;

//	boolean setData = false;
	/** キー */
	private double frequency = TONE.A1.getFrequency();
	/** 音量 */
	private int level = 0;
	/** */
	private int cnt = 0;

	/**
	 * コンストラクタ
	 */
	public ToneGenerator() {
		this(44100, 1, 16);
	}

	/**
	 * コンストラクタ
	 * @param sampleRate
	 * @param channel
	 * @param bitRate
	 */
	public ToneGenerator(int sampleRate, int channel, int bitRate) {
		this.sampleRate = sampleRate;
//		this.channel = channel;
//		this.bitRate = bitRate;
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		// 最後の10の根拠がない・・・
		this.buffSize = sampleRate	* channel * bitRate * 2 / bitRate /10;
		int minSize = AudioTrack.getMinBufferSize(sampleRate, channel, AudioFormat.CHANNEL_OUT_DEFAULT);
		if(this.buffSize < minSize){
			Log.d("ToneGenerator", "buffer size is min.");
			this.buffSize = minSize;
		}
		
		Log.d("ToneGenerator", "Buffer Size = " + Integer.toString(this.buffSize));
		audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				sampleRate,
				AudioFormat.CHANNEL_OUT_DEFAULT, 
				AudioFormat.ENCODING_DEFAULT, 
				buffSize,
				AudioTrack.MODE_STREAM);
//		audioTrack.setPlaybackPositionUpdateListener(this);
//		audioTrack.setPositionNotificationPeriod(1024);
		audioTrack.play();
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(thread != null){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			if(this.level > 0){
				try{
					playTone(this.frequency, this.level);
				}catch(Exception e){
				}
			}
		}
	}
	
	/**
	 * 音を鳴らします
	 * @param tone 音階
	 * @param level 音量
	 */
	public void toneOn(TONE tone, int level){
		if(tone == TONE.NONE){
			toneOff();
		}else{
			toneOn(tone.getFrequency(), level);
		}
	}

	/**
	 * 音を鳴らします
	 * @param frequency 音階(周波数)
	 * @param level 音量
	 */
	public void toneOn(double frequency, int level){
		this.frequency = frequency;
		this.level = level;
		//Log.d("ToneGenerator", "Tone on");
	}

	/**
	 * 音を止めます
	 * 内部の処理が気に入らない。
	 */
	public void toneOff(){
		this.level = 0;
		this.cnt = 0;
		//Log.d("ToneGenerator", "Tone off");
	}

	/**
	 * 音を再生します
	 * @param frequency 音程
	 * @param level 音量
	 */
	private void playTone(double frequency, int level){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // バイト列に適当な矩形波を作成
        int len = buffSize / 2;
        //Log.d("ToneGenerator", "Length = " + Integer.toString(len));
        //以下データの参考
//        try {
//        	// RIFF形式の識別子 'RIFF'
//			baos.write(new byte[]{'R', 'I', 'F', 'F'});
//			// ファイルサイズ(byte単位)
//			baos.write(new byte[]{' ', ' ', ' ', ' '});
//			// RIFFの種類を表す識別子 'WAVE'
//			baos.write(new byte[]{'W', 'A', 'V', 'E'});
//			// fmt チャンク
//			baos.write(new byte[]{'f', 'm', 't', ' '});
//			// fmt チャンクのバイト数(リニアPCM ならば 16(10 00 00 00))
//			baos.write(new byte[]{0x10, 0x00, 0x00, 0x00});
//			// フォーマットID(リニアPCM ならば 1(01 00))
//			baos.write(new byte[]{0x01, 0x00});
//			// チャンネル数 (モノラル ならば 1(01 00) ステレオ ならば 2(02 00))
//			baos.write(intToBytes(channel, 2));
//			// サンプリングレート 44.1kHz ならば 44100(44 AC 00 00)
//			baos.write(intToBytes(sampleRate, 4));
//			// データ速度 (Byte/sec)44.1kHz 16bit ステレオ ならば44100×2×2 = 176400(10 B1 02 00)
//			baos.write(intToBytes(sampleRate * channel * bitRate, 4));
//			// ブロックサイズ (Byte/sample×チャンネル数) 16bit ステレオ ならば 2×2 = 4(04 00)
//			baos.write(intToBytes((bitRate / 8 * channel), 2));
//			// サンプルあたりのビット数 (bit/sample) 16bit ならば 16(10 00)
//			baos.write(new byte[]{0x10, 0x00});
//			// 拡張部分のサイズ リニアPCMならば存在しない
//			// 拡張部分 リニアPCMならば存在しない
//			// d' 'a' 't' 'a'
//			baos.write(new byte[]{'d', 'a', 't', 'a'});
//			// バイト数n 波形データのバイト数
//			baos.write(intToBytes(len, 4));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        for (int i = 0; i < len; i++) {
//            double r = (i / (sampleRate / frequency));
//            //波形
//            baos.write((byte)(level * Math.sin(r * Math.PI * 2)));
        	// この計算が理解できてない・・・
            double r = (cnt / (sampleRate / frequency));
            //波形
            baos.write((byte)(level * Math.sin(r * Math.PI * 2)));
            //baos.write((byte)(level * Math.cos(r * Math.PI * 2)));
            //baos.write((byte)(level * Math.tan(r * Math.PI * 2)));

            //baos.write((byte)(level * Math.sin(cnt / 180 * Math.PI * 2)));
            cnt++;
            if(cnt >= sampleRate * frequency){
            	cnt = 0;
            }
        }
        audioTrack.write(baos.toByteArray(), 0, baos.size());
        //audioTrack.setNotificationMarkerPosition(len);
 	}

	@Override
	public void onMarkerReached(AudioTrack track) {
		Log.d("ToneGenerator", "onMarkerReached");
	}
	
	@Override
	public void onPeriodicNotification(AudioTrack track) {
		Log.d("ToneGenerator", "onPeriodicNotification");
		if(this.level > 0){
			try{
				playTone(this.frequency, this.level);
			}catch(Exception e){
			}
		}
	}

//	/**
//	 * int型32ビットデータをリトルエンディアンのバイト配列にする
//	 * @param value 
//	 * @param byteLen byte数
//	 * @return
//	 */
//	private byte[] intToBytes(int value, int byteLen) {
//	    ByteArrayOutputStream baos = new ByteArrayOutputStream(byteLen);
//	    for(int i  = 0; i < byteLen; i++){
//	    	baos.write((value >> (i * 8)) & 0x000000ff);
//	    }
//	    return baos.toByteArray();
//	}
	
	/**
	 * ToneGeneratorを使わなくなったら呼び出してください
	 */
	public void release(){
		thread = null;
	    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
		if(audioTrack != null){
			thread = null;
	        if(audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
	        	audioTrack.stop();
	        }
			audioTrack.release();
		}
		audioTrack = null;
	}
}
