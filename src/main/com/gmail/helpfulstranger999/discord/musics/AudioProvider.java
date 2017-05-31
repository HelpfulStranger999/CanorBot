package com.gmail.helpfulstranger999.discord.musics;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioProvider;

public class AudioProvider implements IAudioProvider {
	
	protected final AudioPlayer player;
	protected AudioFrame frame;
	
	protected final static AudioEncodingType encode = AudioEncodingType.OPUS;
	protected final static int channels = 2;

	public AudioProvider (AudioPlayer player) {
		this.player = player;
	}

	@Override
	public boolean isReady() {
		frame = (frame == null) ? player.provide() : frame;
		return (frame != null);
	}

	@Override
	public byte[] provide() {
		frame = (frame == null) ? player.provide() : frame;
		byte[] bytes = (frame != null) ? frame.data : null;
		frame = null;
		return bytes;
	}
	
	@Override
	public AudioEncodingType getAudioEncodingType () {
		return encode;
	}
	
	@Override
	public int getChannels () {
		return channels;
	}

}
