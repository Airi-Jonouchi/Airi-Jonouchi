package arpg.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import arpg.main.MainPanel;

public class Sound implements LineListener {

	public enum SoundEffect {

		SAVE("effect/save.wav", 2.0),
		MESSAGE("effect/man_3.wav", 2.0),
		MESSAGE_2("effect/ladies.wav", 1.5),
		BOTTUN("effect/bottun.wav", 2.0),
		CANCEL("effect/cancel.wav", 2.0),
		CHURCH("effect/church.wav", 2.0),
		CURSOR("effect/cursor.wav", 2.0),
		DECISION("effect/decision.wav", 2.0),
		DOOR("effect/door.wav", 2.0),
		INN("effect/inn.wav", 2.0),
		PAY("effect/pay.wav", 2.0), 
		AXE("effect/axe.wav", 2.0),	//10
		FIRE("effect/fire.wav", 2.0),
		HEAL("effect/heal.wav", 2.0),
		HIT("effect/hit.wav", 2.0),
		PUSH("effect/push.wav", 2.0),
		POWER("effect/power.wav", 2.0),
		STATUS("effect/status.wav", 2.0),
		STORN("effect/storn.wav", 2.0),
		SWORD("effect/sword.wav", 2.0),
		WATER("effect/water.wav", 2.0),
		WIND("effect/wind.wav", 2.0),	//20
		LANSER("effect/lanser.wav", 1.0),
		LEVEL("effect/level.wav", 2.0),
		STAIRS("effect/stairs.wav", 0.8);
		

		private String path;
		private double volume;
		private SoundEffect(String path, double volume) {
			this.path = path;
			this.volume = volume;
		}

		public String getPath() {
			return this.path;
		}

		public double getVolume() {
			return this.volume;
		}
	}

	public enum Bgm {
		CITY("bgm/city.wav", 0.7),
		DUNGEON("bgm/dungeon.wav", 1.0),
		PINCHI("bgm/pinchi.wav", 1.0),
		DEAD("bgm/dead.wav", 1.2),
		TELOP("bgm/telop.wav", 1.2);

		private String path;
		private double volume;
		private Bgm(String path, double volume) {
			this.path = path;
			this.volume = volume;
		}

		public String getPath() {
			return this.path;
		}

		public double getVolume() {
			return this.volume;
		}
	}

	private static HashMap<SoundEffect, Clip> soundEffectMap;
	private static List<Bgm> bgmList;
	private static SourceDataLine sourceDataLine;

	public Sound() {
		if(soundEffectMap == null) {
			setSoundEffect();
		}
		if(sourceDataLine == null) {
			setBgmList();
		}	
	}

	private void setBgmList() {

		bgmList = new ArrayList<>();
		bgmList.add(Bgm.CITY);
		sourceDataLine = null;
	}

	public void bgmPlayer(Bgm bgm) {
	
		try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(bgm.path)));) {

			AudioFormat audioFormat = audioInputStream.getFormat();
			DataLine.Info dataLine = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLine);
			sourceDataLine.open();
			sourceDataLine.start();
			bgmVolumSetting(bgm.volume);		
			byte[] data = new byte[sourceDataLine.getBufferSize()];

			int buffer = -1;
			while((buffer = audioInputStream.read(data))!= -1) {
				sourceDataLine.write(data, 0, buffer);
			}
			sourceDataLine.drain();
			sourceDataLine.stop();
			sourceDataLine.close();
		}
		catch(UnsupportedAudioFileException | LineUnavailableException | IOException e) {
			throw new IllegalStateException(e);
		}
		finally {
			if(sourceDataLine != null) {
				sourceDataLine.close();
			}
		}
	}

	public void stop() {
		if(sourceDataLine != null) {
			sourceDataLine.stop();
			sourceDataLine.flush();
			sourceDataLine.close();
		}	
	}

	private void bgmVolumSetting(double volume) {

		if(volume > 2.0) {
			volume = 2.0;
		}
		else if(volume < 0.1) {
			volume = 0.1;
		}
		FloatControl volumControl = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
		volumControl.setValue((float)Math.log10(volume) * 20);
	}

	private void soundEffectVolumeSetting(Clip clip, double volume) {

		if(volume > 2.0) {
			volume = 2.0;
		}
		else if(volume < 0.1) {
			volume = 0.1;
		}
		FloatControl volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue((float)Math.log10(volume) * 20);
	}

	public void soundEffectStart(SoundEffect soundEffect) {
	    Clip clip = soundEffectMap.get(soundEffect);
		if(forceAFramePosition(soundEffect)) {
			if(clip.getFramePosition() != 0) {
				clip.stop();
				clip.setFramePosition(0);
			}
		}
		soundEffectVolumeSetting(clip, soundEffect.volume);
		clip.start();
	}

	private boolean forceAFramePosition(SoundEffect soundEffect) {
		switch(soundEffect) {
			case CURSOR, CANCEL, BOTTUN -> {
				return true;
			}
			default -> {
				return false;
			}
		}
	}

	private void setSoundEffect() {

		soundEffectMap = new HashMap<>();
		SoundEffect[] values = SoundEffect.values();
		Arrays.stream(values).forEach(v -> soundEffectLording(v));
	}

	private void soundEffectLording(SoundEffect soundEffect) {

		try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(soundEffect.getPath())));) {

			AudioFormat audioFormat = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
			Clip clip = (Clip)AudioSystem.getLine(info);
			clip.addLineListener(this);
			clip.open(audioInputStream);
			soundEffectMap.put(soundEffect, clip);
		}
		catch(UnsupportedAudioFileException | LineUnavailableException | IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void update(LineEvent event) {
		
		if(event.getType() == LineEvent.Type.STOP) {
			Clip clip = (Clip)event.getSource();
			clip.stop();
			clip.setFramePosition(0);

			if(!MainPanel.isGameSwitch()) {
				clip.close();
			}
		}
	}
}
