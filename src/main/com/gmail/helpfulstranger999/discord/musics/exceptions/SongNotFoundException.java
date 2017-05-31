package com.gmail.helpfulstranger999.discord.musics.exceptions;

public class SongNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6242244544832024768L;
	protected String song = "";

	public SongNotFoundException(String song) {
		this("The specified song was not found", song);
	}

	public SongNotFoundException(String message, String song) {
		super(message);
		this.song = song;
	}

	public SongNotFoundException(Throwable cause, String song) {
		super(cause);
		this.song = song;
	}

	public SongNotFoundException(String message, Throwable cause, String song) {
		super(message, cause);
		this.song = song;
	}
	
	public String getSong () {
		return this.song;
	}

}
