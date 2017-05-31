package com.gmail.helpfulstranger999.discord.musics;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import com.gmail.helpfulstranger999.discord.musics.Configuration.Settings;
import com.gmail.helpfulstranger999.discord.musics.listener.ConfigurerListener;
import com.gmail.helpfulstranger999.discord.musics.listener.GeneralListener;
import com.gmail.helpfulstranger999.discord.musics.listener.MusicListener;
import com.gmail.helpfulstranger999.discord.musics.listener.QueueListener;
import com.gmail.helpfulstranger999.discord.musics.listener.TrackAdapter;
import com.gmail.helpfulstranger999.discord.musics.util.TriValueMap;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class CanorBot {
	
	protected IDiscordClient client = null;
	protected AudioPlayerManager manager = null;
	protected Configuration config = null;
	protected TriValueMap<IGuild, AudioPlayer, TrackAdapter, SongQueue> audio = TriValueMap.create();
	
	public void start () throws Exception {
		config = new Configuration();
		ClientBuilder builder = new ClientBuilder();
		builder.setMaxMessageCacheCount(-1);
		builder.setMaxReconnectAttempts(100);
		builder.withToken("MzE5MjY3ODcyMDkyNDU0OTEy.DA-c6g.fWSt4-TMDJYyNmJE8cklOLJodYA");
		this.client = builder.build();
		
		AudioPlayerManager manager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(manager);
		this.manager = manager;
		
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new QueueListener(this));
		dispatcher.registerListener(new MusicListener(this));
		dispatcher.registerListener(new GeneralListener(this));
		dispatcher.registerListener(new ConfigurerListener(this));
		
		client.login();
	}
	
	public void stop () throws SQLException {
		config.unload();
		client.logout();
	}
	
	public Configuration getConfig () {
		return this.config;
	}
	
	public Settings getSettings (IGuild guild) {
		return this.config.getSettings(guild);
	}
	
	public IDiscordClient getClient () {
		return this.client;
	}
	
	public AudioPlayerManager getManager () {
		return this.manager;
	}
	
	public AudioPlayer getPlayer (IGuild guild) {
		return (audio.containsKey(guild)) ? audio.getValue1(guild) : null;
	}
	
	public TrackAdapter getAdapter (IGuild guild) {
		return (audio.containsKey(guild)) ? audio.getValue2(guild) : null;
	}
	
	public SongQueue getQueue (IGuild guild) {
		return (audio.containsKey(guild)) ? audio.getValue3(guild) : null;
	}
	
	public void createNewGuild (IGuild guild) {
		AudioPlayer player = manager.createPlayer();
		SongQueue queue = new SongQueue(this.manager);
		TrackAdapter ta = new TrackAdapter(guild.getGeneralChannel(), queue);
		player.addListener(ta);
		audio.put(guild, player, ta, queue);
		
		guild.getAudioManager().setAudioProvider(new AudioProvider(player));
	}
	
	public static String getStreamType (AudioTrack audioTrack) {
		if(audioTrack.getInfo().isStream) {
			if(audioTrack instanceof TwitchStreamAudioTrack) {
				return "Twitch";
			} else if (audioTrack instanceof BeamAudioTrack) {
				return "Beam";
			} else if (audioTrack instanceof YoutubeAudioTrack) {
				return "YouTube";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static String getStreamer (AudioTrack audioTrack) {
		if(audioTrack.getInfo().isStream) {
			switch (getStreamType(audioTrack)) {
				case "Twitch":
					return TwitchStreamAudioSourceManager.getChannelIdentifierFromUrl(audioTrack.getInfo().uri);
				case "Beam":
					return audioTrack.getInfo().author;
				case "Youtube":
					return audioTrack.getInfo().author;
				default:
					return null;
				}
		} else {
			return null;
		}
	}
	
	public static void selfDestruct (long millis, IMessage msg) {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run () {
				msg.delete();
			}
		}, millis);
	}
	

}
