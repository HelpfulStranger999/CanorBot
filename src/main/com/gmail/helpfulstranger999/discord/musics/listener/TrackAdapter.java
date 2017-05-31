package com.gmail.helpfulstranger999.discord.musics.listener;

import com.gmail.helpfulstranger999.discord.musics.CanorBot;
import com.gmail.helpfulstranger999.discord.musics.SongQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class TrackAdapter extends AudioEventAdapter {
	
	protected SongQueue queue;
	protected IChannel channel;

	public TrackAdapter (IChannel channel, SongQueue queue) {
		this.channel = channel;
		this.queue = queue;
	}
	
	public void beginQueue (AudioPlayer player) {
		player.playTrack(queue.fetchSong());
	}
	
	@Override
	public void onPlayerResume (AudioPlayer player) {
		if(playerPauseMSG != null) playerPauseMSG.delete();
		CanorBot.selfDestruct(30000, channel.sendMessage("Now resuming the queue of songs."));
	}
	
	protected IMessage playerPauseMSG = null;
	
	@Override
	public void onPlayerPause (AudioPlayer player) {
		playerPauseMSG = channel.sendMessage("Music Bot now paused");
	}
	
	protected IMessage trackMSG = null;
	
	@Override
	public void onTrackStart (AudioPlayer player, AudioTrack track) {
		AudioTrackInfo info = track.getInfo();
		String uname = queue.getCurrentTrack().getUser().getName();
		
		if(info.isStream) {
			
			String streamer = CanorBot.getStreamer(track);
			trackMSG = channel.sendMessage("Now streaming " + streamer + "'s livestream requested by " + uname + ".");
		
		} else {
			
			trackMSG = channel.sendMessage("Now playing `" + info.title + "` requested by " + uname + ".");
			
		}
		
	}
	
	@Override
	public void onTrackEnd (AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
		trackMSG.delete();
		trackMSG = null;
		
		if(queue.isEmpty()) {
			
			channel.sendMessage("The end of the queue is reached. Add some more songs with /?add");
			
		} else {
			
			player.playTrack(queue.fetchSong());
			
		}
		
	}
	
	@Override
	public void onTrackStuck (AudioPlayer player, AudioTrack track, long thresholdMS) {
		
		if(track.getState().equals(AudioTrackState.PLAYING)) track.stop();
		channel.sendMessage("Track '" + track.getInfo().title + "' has gotten stuck. "
				+ "Please notify one of the bot developers.");
		
	}
	
	@Override
	public void onTrackException (AudioPlayer player, AudioTrack track, FriendlyException e) {
		
		channel.sendMessage("Track '" + track.getInfo().title + "' has caused an exception. "
				+ "Please notify one of the bot developers.");
		e.printStackTrace();
		
	}

}
