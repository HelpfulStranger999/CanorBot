package com.gmail.helpfulstranger999.discord.musics;

import java.util.LinkedList;
import java.util.concurrent.Future;

import com.google.common.collect.ImmutableList;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import sx.blah.discord.handle.obj.IUser;

public class SongQueue {
	
	protected LinkedList<SongTrack> songlist = new LinkedList<SongTrack>();
	protected AudioPlayerManager manager = null;
	protected SongTrack currentTrack = null;
	
	public SongQueue (AudioPlayerManager playerManager) {
		this.manager = playerManager;
	}
	
	public ImmutableList<SongTrack> getSongList () {
		return ImmutableList.copyOf(songlist);
	}
	
	public SongTrack fetchSongTrack () {
		currentTrack = songlist.poll();
		return currentTrack;
	}
	
	public AudioTrack fetchSong () {
		currentTrack = songlist.poll();
		return currentTrack.getTrack();
	}
	
	public SongTrack getNextSongTrack () {
		return songlist.peek();
	}
	
	public AudioTrack getNextSong () {
		return songlist.peek().getTrack();
	}
	
	public SongTrack getCurrentTrack () {
		return currentTrack;
	}
	
	public boolean isEmpty () {
		return songlist.isEmpty();
	}
	
	public SongTrack queueSongToList (IUser user, String url) {
		TrackWaiter waiter = new TrackWaiter();
		Future<Void> fut = manager.loadItem(url, new AudioLoadResultHandler () {

			@Override
			public void loadFailed(FriendlyException arg0) {
				waiter.setTrack(null);
			}

			@Override
			public void noMatches() {
				waiter.setTrack(null);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				SongTrack track = new SongTrack(user, playlist.getTracks().get(0));
				songlist.offer(track);
				waiter.setTrack(track);
			}

			@Override
			public void trackLoaded(AudioTrack track) {
				SongTrack strack = new SongTrack(user, track);
				songlist.offer(strack);
				waiter.setTrack(strack);
			}
			
		});
		while(!fut.isDone()) {}
		return waiter.getTrack();
	}
	
	public void queueSongToList (SongTrack track) {
		songlist.offer(track);
	}
	
	public SongTrack loadSongTrack (IUser user, String url) {
		TrackWaiter waiter = new TrackWaiter();
		Future<Void> fut = manager.loadItem(url, new AudioLoadResultHandler () {

			@Override
			public void loadFailed(FriendlyException arg0) {
				waiter.setTrack(null);
			}

			@Override
			public void noMatches() {
				waiter.setTrack(null);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				SongTrack track = new SongTrack(user, playlist.getTracks().get(0));
				waiter.setTrack(track);
			}

			@Override
			public void trackLoaded(AudioTrack track) {
				SongTrack strack = new SongTrack(user, track);
				waiter.setTrack(strack);
			}
			
		});
		while(!fut.isDone()) {}
		return waiter.getTrack();
	}
	
	private class TrackWaiter {
		
		private SongTrack track;
		
		public void setTrack(SongTrack track) {
			this.track = track;
		}
		
		public SongTrack getTrack () {
			return this.track;
		}
	}
	
	public void promoteSong (int position) {
		SongTrack t = songlist.get(position - 1);
		songlist.offerFirst(t);
	}
	
	public void removeSong (int position) {
		songlist.remove(position - 1);
	}
	
	public SongTrack retrieveSong (int position) {
		return songlist.get(position - 1);
	}
	
	public int fetchUserSongRequests (IUser user) {
		int total = 0;
		long id = user.getLongID();
		
		LinkedList<SongTrack> songs = songlist;
		for(SongTrack track : songs) {
			IUser request = track.getUser();
			long requestID = request.getLongID();
			
			if(id == requestID) {
				total++;
			}
		}
		
		return total;
	}
	
	public boolean canUserRequest (IUser user) {
		return (fetchUserSongRequests(user) < 10);
	}

}
