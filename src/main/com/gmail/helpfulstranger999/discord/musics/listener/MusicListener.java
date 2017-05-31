package com.gmail.helpfulstranger999.discord.musics.listener;

import java.util.ArrayList;
import java.util.Arrays;

import com.gmail.helpfulstranger999.discord.musics.CanorBot;
import com.gmail.helpfulstranger999.discord.musics.Configuration.Settings;
import com.gmail.helpfulstranger999.discord.musics.SongQueue;
import com.gmail.helpfulstranger999.discord.musics.SongTrack;
import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

public class MusicListener {
	
	protected CanorBot bot;

	public MusicListener (CanorBot bot) {
		this.bot = bot;
	}
	
	protected boolean isJoined = false;
	protected boolean isPlaying = false;
	protected boolean hasPlayed = false;
	protected ArrayList<IVoiceChannel> vc = Lists.newArrayList();
	
	@EventSubscriber
	public void onMessageReceivedEvent (MessageReceivedEvent event) {
		try {
			IChannel c = event.getChannel();
			IUser u = event.getAuthor();
			IGuild g = event.getGuild();
			
			IVoiceChannel vc = getVoiceForGuild(g);
			TrackAdapter ta = bot.getAdapter(g);
			SongQueue queue = bot.getQueue(g);
			AudioPlayer player = bot.getPlayer(g);
			
			String msg = event.getMessage().getContent();
			ArrayList<String> msgs = new ArrayList<String>(Arrays.asList(msg.split("\\s+")));
			
			if(msg.equalsIgnoreCase("/?voice")){
				
				IVoiceChannel voice = u.getVoiceStateForGuild(g).getChannel();
					
				if(voice == null) {
						
					CanorBot.selfDestruct(60000, c.sendMessage(u.mention() + " You are not currently in a voice chat. "
							+ "Join one before performing this command."));
					event.getMessage().delete();
						
				} else {
						
					this.vc.add(voice);
					voice.join();
					event.getMessage().delete();
						
				}
				
			} else if (msg.equalsIgnoreCase("/?unvoice")) {
				
				if(vc == null) {
					
					event.getMessage().delete();
					return;
					
				}
				
				vc.leave();
				isJoined = false;
				event.getMessage().delete();
			
			} else if (msg.equalsIgnoreCase("/?play")) {
				
				if(vc == null) {
					
					event.getMessage().delete();
					return;
					
				}
				
				if(player.isPaused()) {
					
					event.getMessage().delete();
					return;
					
				}
					
				ta.beginQueue(player);					
				event.getMessage().delete();
				
			} else if (msg.equalsIgnoreCase("/?pause")) {
				
				if(vc == null) {
					
					event.getMessage().delete();
					return;
					
				}
				
				if(!hasPermissions(event)) {
					
					event.getMessage().delete();
					return;
					
				}
				
				player.setPaused(true);
				event.getMessage().delete();
				
			} else if (msg.equalsIgnoreCase("/?resume")) {
				
				if(vc == null) {
					
					event.getMessage().delete();
					return;
					
				}

				if(!hasPermissions(event)) {
					
					event.getMessage().delete();
					return;
					
				}
				
				player.setPaused(false);
				event.getMessage().delete();
				
			} else if (msg.equalsIgnoreCase("/?skip")) {
				
				if(!hasPermissions(event)) {
					
					event.getMessage().delete();
					return;
					
				}
				
				if(player.getPlayingTrack() != null) {
					
					SongTrack track = queue.getCurrentTrack();
					AudioTrack audioTrack = track.getTrack();
					AudioTrackInfo info = audioTrack.getInfo();
					
					track.skip();
					if(info.isStream) {
						String streamer = CanorBot.getStreamer(audioTrack);
						CanorBot.selfDestruct(30000, c.sendMessage("Now ending stream of " + streamer + "'s livestream."));
					} else {
						CanorBot.selfDestruct(30000, c.sendMessage("Song `" + info.title + "` has been skipped."));
					}
					
					event.getMessage().delete();
					
				}
				
			} else if (msgs.get(0).equalsIgnoreCase("/?volume")) {
				
				if(vc == null) {
					
					event.getMessage().delete();
					return;
					
				}
				
				int volume = Integer.parseInt(msgs.get(1));
				player.setVolume(volume);
				
				event.getMessage().delete();
				CanorBot.selfDestruct(60000, c.sendMessage("The volume has been updated to " + volume + "."));
				
			}
		} catch (Exception e) {
			System.out.println("An error has occurred: ");
			e.printStackTrace();
		}
	}
	
	public IVoiceChannel getVoiceForGuild (IGuild guild) {
		for(IVoiceChannel vc : this.vc) {
			if(vc.getGuild().equals(guild)) {
				return vc;
			}
		}
		return null;
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
	public void onDisconnectedEvent (DisconnectedEvent event) {
		if(event.getReason().equals(DisconnectedEvent.Reason.ABNORMAL_CLOSE)) {
			for(IVoiceChannel vc : vc) {
				do {
					vc.leave();
				} while (vc.isConnected());
			}
		}
	}

}
