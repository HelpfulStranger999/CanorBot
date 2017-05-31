package com.gmail.helpfulstranger999.discord.musics.exceptions;

import com.gmail.helpfulstranger999.discord.musics.SongTrack;

public class SongNotPlayingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6133604248742297710L;
	protected SongTrack track;

	public SongNotPlayingException (SongTrack track) {
		super("Track " + track.getTrack().getInfo().identifier + " is not currently playing!");
		this.track = track;
	}
	
	public SongTrack getSong () {
		return this.track;
	}

}
