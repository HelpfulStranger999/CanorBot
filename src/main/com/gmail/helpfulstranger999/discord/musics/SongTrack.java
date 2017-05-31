package com.gmail.helpfulstranger999.discord.musics;

import java.util.Date;

import com.gmail.helpfulstranger999.discord.musics.exceptions.SongNotPlayingException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import sx.blah.discord.handle.obj.IUser;

public class SongTrack {

	protected IUser user;
	protected AudioTrack track;
	protected Date timestamp;
	
	protected final long id;
	protected static long global_id = 0;
	
	public SongTrack(IUser user, AudioTrack track) {
		this.user = user;
		this.track = track;
		this.timestamp = new Date();
		this.id = global_id;
		global_id++;
	}
	
	public final long getID () {
		return this.id;
	}
	
	public IUser getUser () {
		return this.user;
	}
	
	public AudioTrack getTrack () {
		return this.track;
	}
	
	public Date getTimestamp () {
		return this.timestamp;
	}
	
	public void skip () throws SongNotPlayingException {
		if(!track.getState().equals(AudioTrackState.PLAYING)) {
			throw new SongNotPlayingException(this);
		} else {
			if(track.getInfo().isStream) {
				track.stop();
			} else {
				track.setPosition(track.getDuration());
			}
		}
	}
	
}
