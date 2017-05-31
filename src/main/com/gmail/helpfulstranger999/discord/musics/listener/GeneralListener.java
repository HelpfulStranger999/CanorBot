package com.gmail.helpfulstranger999.discord.musics.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.gmail.helpfulstranger999.discord.musics.CanorBot;
import com.gmail.helpfulstranger999.discord.musics.Configuration.Settings;
import com.gmail.helpfulstranger999.discord.musics.SongQueue;
import com.gmail.helpfulstranger999.discord.musics.SongTrack;
import com.google.common.collect.Maps;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

public class GeneralListener {
	
	protected CanorBot bot;
	
	// Song ID, Users so far
	protected HashMap<Long, Long> voteSkip = Maps.newHashMap();
	protected HashMap<Long, Long> voteRemove = Maps.newHashMap();
	
	// Song ID, Users
	protected HashMap<Long, IUser> voteSkipUsers = Maps.newHashMap();
	protected HashMap<Long, IUser> voteRemoveUsers = Maps.newHashMap();

	public GeneralListener (CanorBot bot) {
		this.bot = bot;
	}
	
	@EventSubscriber
	public void onReadyEvent (ReadyEvent event) {
		event.getClient().changePlayingText("/?info for commands");
	}
	
	@EventSubscriber
	public void onMessageReceivedEvent (MessageReceivedEvent event) {
		try {
			
			String message = event.getMessage().getContent();
			ArrayList<String> msgs = new ArrayList<String>(Arrays.asList(message.split("\\s+")));
			String cmd = msgs.get(0);
			
			IChannel c = event.getChannel();
			IUser u = event.getAuthor();
			IGuild g = event.getGuild();
			
			SongQueue queue = bot.getQueue(g);
			
			if(cmd.equalsIgnoreCase("/?vote")) {
				
				String sub = msgs.get(1);
				
				if(sub.equalsIgnoreCase("skip")) {
					
					if(voteSkipUsers.containsValue(u)) {
						
						event.getMessage().delete();
						return;
						
					}
					
					long id = queue.getCurrentTrack().getID();
					
					if(voteSkip.containsKey(id)) {
						
						long curVotes = voteSkip.get(id);
						voteSkip.put(id, curVotes + 1);
						
					} else {
						
						voteSkip.put(id, 1L);
						
					}
					
					voteSkipUsers.put(id, u);
					
					if(voteSkip.get(id) >= 5) {
						
						SongTrack track = queue.getCurrentTrack();
						AudioTrack audioTrack = track.getTrack();
						AudioTrackInfo info = audioTrack.getInfo();
						
						track.skip();
						
						if(info.isStream) {
							
							String streamer = CanorBot.getStreamer(audioTrack);
							CanorBot.selfDestruct(120000, c.sendMessage("Now ending stream of " + streamer + 
									"'s livestream by popular demand."));
							event.getMessage().delete();
							
						} else {
							
							CanorBot.selfDestruct(120000, c.sendMessage("Song `" + info.title + 
									"` has been skipped by popular demand."));
							event.getMessage().delete();
							
						}
						
					} else {
						
						CanorBot.selfDestruct(120000, c.sendMessage(event.getAuthor().getName() + " has voted to skip this song. "
								+ "Current total: " + voteSkip.get(id) + "; total votes needed: 5."));
						event.getMessage().delete();
						
					}
					
				} else if (sub.equalsIgnoreCase("remove")) {
					
					if(voteRemoveUsers.containsValue(u)) {
						
						event.getMessage().delete();
						return;
						
					}
					
					long id = queue.getCurrentTrack().getID();
					
					if(voteRemove.containsKey(id)) {
						
						long curVotes = voteRemove.get(id);
						voteRemove.put(id, curVotes + 1);
						
					} else {
						
						voteRemove.put(id, 1L);
						
					}
					
					voteRemoveUsers.put(id, u);
					
					if(voteRemove.get(id) >= 5) {
						
						SongTrack track = queue.getCurrentTrack();
						AudioTrack audioTrack = track.getTrack();
						AudioTrackInfo info = audioTrack.getInfo();
						
						track.skip();
						
						if(info.isStream) {
							
							String streamer = CanorBot.getStreamer(audioTrack);
							CanorBot.selfDestruct(120000, c.sendMessage("Now ending stream of " + streamer + 
									"'s livestream by popular demand."));
							event.getMessage().delete();
							
						} else {
							
							CanorBot.selfDestruct(120000, c.sendMessage("Song `" + info.title + 
									"` has been skipped by popular demand."));
							event.getMessage().delete();
							
						}
						
					} else {
						
						CanorBot.selfDestruct(120000, c.sendMessage(event.getAuthor().getName() + " has voted to skip this song. "
								+ "Current total: " + voteRemove.get(id) + "; total votes needed: 5."));
						event.getMessage().delete();
						
					}
					
				}
				
			} else if (cmd.equalsIgnoreCase("/?info")) {
				
				MessageBuilder builder = new MessageBuilder(event.getClient());
				builder.withChannel(c);
				
				builder.appendContent("This music bot allows users to queue and play songs in Discord.\n");
				builder.appendContent("It supports YouTube complete URLs, YouTube video ids, YouTube search results, Vimeo videos,"
						+ "SoundCloud videos, BandCamp videos, Beam/Mixer streams, YouTube streams, Twitch streams, and "
						+ "files over the internet.\n\n");
				builder.appendContent("`/?vote skip` - Vote whether the current song should be skipped.\n");
				builder.appendContent("`/?vote remove` - Vote a song should be removed.\n");
				builder.appendContent("`/?add <video>` - Adds a video to the queue.\n");
				builder.appendContent("`/?remove <position>` - Removes a video from the queue at a specified position.\n");
				builder.appendContent("`/?queue` - Lists the current track and the next 15 songs.\n");
				builder.appendContent("`/?add <video>` - Adds a video to the queue.\n");
				builder.appendContent("`/?voice` - Joins the voice channel the user is currently in.\n");
				builder.appendContent("`/?unvoice` - Leaves the voice channel\n");
				builder.appendContent("`/?play` - Begins the queue\n");
				builder.appendContent("------------------------------------------------------------------------\n");
				builder.appendContent("These commands work only while the bot is playing music\n");
				builder.appendContent("------------------------------------------------------------------------\n");
				builder.appendContent("`/?pause` - Pauses the player\n");
				builder.appendContent("`/?resume` - Resumes the player if paused\n");
				builder.appendContent("`/?skip` - Skips the current track\n");
				builder.appendContent("`/?volume <new volume>` - Adjusts the volume\n");
				builder.appendContent("\nAdmins may use `/?leaveserver` to make the bot leave or `/?configure` to set it up.");
				
				CanorBot.selfDestruct(120000, builder.send());
				event.getMessage().delete();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean hasPermissions (MessageReceivedEvent event) {
		IGuild guild = event.getGuild();
		IUser user = event.getAuthor();
		
		if(user.equals(guild.getOwner())) return true;
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
	
	private boolean isRoleSufficient (IRole role, IRole thisRole) {
		for(Permissions perm : role.getPermissions()) {
			if(!thisRole.getPermissions().contains(perm)) {
				return false;
			}
		}
		
		return true;
	}
	
	@EventSubscriber
	public void onGuildJoined (GuildCreateEvent event) {
		bot.createNewGuild(event.getGuild());
	}

}
