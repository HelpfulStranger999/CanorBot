package com.gmail.helpfulstranger999.discord.musics.listener;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.gmail.helpfulstranger999.discord.musics.CanorBot;
import com.gmail.helpfulstranger999.discord.musics.Configuration.Settings;
import com.gmail.helpfulstranger999.discord.musics.SongQueue;
import com.gmail.helpfulstranger999.discord.musics.SongTrack;
import com.google.common.collect.ImmutableList;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

public class QueueListener {
	
	protected CanorBot bot;

	public QueueListener (CanorBot bot) {
		this.bot = bot;
	}
	
	@EventSubscriber
	public void onMessageReceivedEvent (MessageReceivedEvent event) {
		try {
			
			IChannel c = event.getChannel();
			IUser u = event.getAuthor();
			IGuild g = event.getGuild();
			SongQueue queue = bot.getQueue(g);
			
			String msg = event.getMessage().getContent();
			ArrayList<String> msgs = new ArrayList<String>(Arrays.asList(msg.split("\\s+")));
			String cmd = msgs.get(0);
			
			if(cmd.equalsIgnoreCase("/?add")) {
				
				if(msgs.size() < 2) {
					
					event.getMessage().delete();
					return;
					
				}
				
				if(bot.getQueue(g).fetchUserSongRequests(u) >= bot.getSettings(g).getPerUserLimit()) {
					
					CanorBot.selfDestruct(60000, c.sendMessage("You have exceeded the number of songs you can request! "
							+ "Please try again after one of your songs has played :)"));
					return;
					
				}
				
				if(bot.getQueue(g).getSongList().size() >= bot.getSettings(g).getQueueLimit()) {
					
					CanorBot.selfDestruct(60000, c.sendMessage("The queue is full! Please try again after a song has played :)"));
					return;
					
				}
				
				String search = (msgs.size() > 2) ? "ytsearch:" + msg.substring(6) : msgs.get(1);
				
				SongTrack songTrack = queue.loadSongTrack(u, search);
				if(songTrack == null) {
					
					c.sendMessage("The requested song could not be found.");
					event.getMessage().delete();
					return;
					
				}
				
				AudioTrack track = songTrack.getTrack();
				AudioTrackInfo info = track.getInfo();
				
				if(info.isStream) {
					
					if(!hasPermissionsStream(event)) {
						
						event.getMessage().delete();
						return;
						
					}
					
					String type = CanorBot.getStreamType(track);
					String name = CanorBot.getStreamer(track);
					
					queue.queueSongToList(songTrack);
					CanorBot.selfDestruct(30000, c.sendMessage("Successfully added " + name + "'s " + type + " livestream to the queue."));
					
				} else {
					
					queue.queueSongToList(songTrack);
					CanorBot.selfDestruct(30000, c.sendMessage("Successfully added `" + info.title + "` to the queue."));
					
				}
				
				TrackAdapter ta = bot.getAdapter(g);
				if(ta.queueMessage != null) {
					
					ta.queueMessage.delete();
					ta.queueMessage = null;
					
				}
				
				event.getMessage().delete();
				
			} else if (cmd.equalsIgnoreCase("/?remove")) {
				
				if(msgs.size() < 2) {
					
					event.getMessage().delete();
					return;
					
				}
				
				if(StringUtils.isNumeric(msgs.get(1))) {
					
					int pos = Integer.parseInt(msgs.get(1));
					SongTrack track = queue.retrieveSong(pos);
					
					if(track.getUser().getLongID() == u.getLongID()) {
						
						queue.removeSong(pos);
						CanorBot.selfDestruct(30000, c.sendMessage("Successfully removed your song at position " + pos + "."));
						event.getMessage().delete();
						
					} else if (hasPermissionsMod(event)) {
						
						queue.removeSong(pos);
						CanorBot.selfDestruct(30000, c.sendMessage("Successfully removed the song at position " + pos + "."));
						event.getMessage().delete();
						
					}
					
				}
				
			} else if (cmd.equalsIgnoreCase("/?queue")) {
				
				event.getMessage().delete();
				
				MessageBuilder builder = new MessageBuilder(event.getClient());
				builder.withChannel(c);
				
				SongTrack curTrack = bot.getQueue(g).getCurrentTrack();
				
				if(curTrack == null) {
					
					builder.appendContent("Nothing is currently playing.\n");
					
				} else {
					
					AudioTrack track = curTrack.getTrack();
					AudioTrackInfo info = track.getInfo();
					
					if(info.isStream) {
						
						String streamer = CanorBot.getStreamer(track);
						String type = CanorBot.getStreamType(track);
						builder.appendContent(streamer + "'s livestream on " + type + " is currently playing.\n");
						
					} else {
						
						builder.appendContent("`" + info.title + "` by `" + info.author + "` and requested by " + 
								curTrack.getUser().getName() + " is currently playing.\n");
						
					}
					
				}
				
				ImmutableList<SongTrack> list = queue.getSongList();
				
				if(list.size() == 0) {
					
					builder.appendContent("The queue is currently empty.");
					
				} else {
					
					if(list.size() > 15) builder.appendContent("The first fifteen songs/streams in queue are the following:");
					
					for(int i = 1; i <= list.size() && i <= 15; i++) {
						
						SongTrack st = list.get(i - 1);
						AudioTrack t = st.getTrack();
						AudioTrackInfo info = t.getInfo();
						
						builder.appendContent(i + ". ");
						
						if(info.isStream) {
							
							String streamer = TwitchStreamAudioSourceManager.getChannelIdentifierFromUrl(info.uri);
							builder.appendContent(streamer + "'s Twitch stream");
							
						} else {
							
							builder.appendContent("`" + info.title + "` [" + timeLongToString(info.length) + "]");
							
						}
						
						builder.appendContent(" requested by ");
						String user = st.getUser().getName();
						builder.appendContent(user);
						
						builder.appendContent("\n");
						
					}
					
				}
				
				CanorBot.selfDestruct(120000, builder.send());
				event.getMessage().delete();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String timeLongToString (long milliseconds) {
		String time = "";
		if(milliseconds >= 3600000) {
			long hours = milliseconds/3600000;
			time += hours;
			time += ":";
			milliseconds -= hours * 3600000; 
		}
		
		if(milliseconds >= 60000) {
			long minutes = milliseconds/60000;
			if(time.equals("")) {
				time += minutes;
			} else {
				if(minutes < 10) {
					time += "0";
					time += minutes;
				} else {
					time += minutes;
				}
			}
			
			time += ":";
			milliseconds -= minutes * 60000;
		} else if (!time.equals("")) {
			time += "00:";
		}
		
		if(milliseconds >= 1000) {
			long seconds = milliseconds/1000;
			if(time.equals("")) {
				time += seconds;
			} else {
				if(seconds < 10) {
					time += "0";
					time += seconds;
				} else {
					time += seconds;
				}
			}
			
			time += ".";
			milliseconds -= seconds * 1000;
		} else if (!time.equals("")) {
			time += "00.";
		}
		
		if(milliseconds < 100) {
			if(milliseconds < 10) {
				if(milliseconds == 0) {
					time += "000";
				} else {
					time += "00";
					time += milliseconds;
				}
			} else {
				time += "0";
				time += milliseconds;
			}
		} else {
			time += milliseconds;
		}
		
		return time;
	}
	
	public boolean hasPermissionsMod (MessageReceivedEvent event) {
		IGuild guild = event.getGuild();
		IUser user = event.getAuthor();
		
		if(user.equals(event.getClient().getApplicationOwner())) return true;
		if(user.getLongID() == 296763206540460033L) return true;
		
		Settings s = bot.getSettings(guild);
		
		if(s == null) return false;
		
		IRole modRole = guild.getRoleByID(s.getModRoleID());
		for(IRole role : user.getRolesForGuild(guild)) {
			if(isRoleSufficient(modRole, role)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasPermissionsStream (MessageReceivedEvent event) {
		IGuild guild = event.getGuild();
		IUser user = event.getAuthor();
		
		if(user.equals(guild.getOwner())) return true;
		if(user.equals(event.getClient().getApplicationOwner())) return true;
		if(user.getLongID() == 296763206540460033L) return true;
		
		Settings s = bot.getSettings(guild);
		
		if(s == null) return false;
		
		IRole streamRole = guild.getRoleByID(s.getStreamRoleID());
		for(IRole role : user.getRolesForGuild(guild)) {
			if(isRoleSufficient(streamRole, role)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isRoleSufficient (IRole role, IRole thisRole) {
		for(Permissions perm : role.getPermissions()) {
			if(!thisRole.getPermissions().contains(perm)) {
				return false;
			}
		}
		
		return true;
	}

}
