	package com.gmail.helpfulstranger999.discord.musics.listener;

import static com.gmail.helpfulstranger999.discord.musics.Configuration.DEFAULT_QUEUE;
import static com.gmail.helpfulstranger999.discord.musics.Configuration.DEFAULT_USER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gmail.helpfulstranger999.discord.musics.CanorBot;
import com.gmail.helpfulstranger999.discord.musics.Configuration.Settings;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;

public class ConfigurerListener {
	
	protected CanorBot bot;

	public ConfigurerListener (CanorBot bot) {
		this.bot = bot;
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
			
			if (cmd.equalsIgnoreCase("/?quit")) {
				
				if(u.getPermissionsForGuild(g).contains(Permissions.MANAGE_SERVER) 
						|| u.equals(event.getClient().getApplicationOwner())
						|| u.getLongID() == 296763206540460033L
						|| u.equals(g.getOwner())) {
					
					event.getMessage().delete();
					bot.stop();
				
				}
				
			} else if (cmd.equalsIgnoreCase("/?reboot")) {
				
				if(hasPermissions(event)) {
					
					event.getMessage().delete();
					bot.stop();
					bot.start();
				}
				
			} else if (cmd.equalsIgnoreCase("/?configure")) {
				
				if(u.getPermissionsForGuild(g).contains(Permissions.MANAGE_SERVER) || u.equals(event.getClient().getApplicationOwner())) {
					
					String sub = msgs.get(1);
					if(sub.equalsIgnoreCase("set")) {
						
						if(msgs.size() < 3) {
							
							event.getMessage().delete();
							return;
							
						}
						
						if(msgs.get(2).equalsIgnoreCase("info")) {
							
							CanorBot.selfDestruct(60000, c.sendMessage("Syntax: /?configure set <mod role id> <request streams "
									+ "role id> [user limit] [queue limit]\nUse /?configure roles info to find role ids"));
							event.getMessage().delete();
							return;
							
						} else {
							
							if(msgs.size() < 4) {
								
								event.getMessage().delete();
								return;
								
							}
							
							String mroleStr = msgs.get(2);
							String sroleStr = msgs.get(3);
							String ulimitStr = msgs.size() >= 5 ? msgs.get(4) : "" + DEFAULT_USER;
							String qlimitStr = msgs.size() >= 6 ? msgs.get(5) : "" + DEFAULT_QUEUE;
							
							if(!StringUtils.isNumeric(mroleStr)) {
								
								event.getMessage().delete();
								return;
								
							}
							
							if(!StringUtils.isNumeric(sroleStr)) {
								
								event.getMessage().delete();
								return;
								
							}
							
							if(!StringUtils.isNumeric(ulimitStr)) {
								
								event.getMessage().delete();
								return;
								
							}
							
							if(!StringUtils.isNumeric(qlimitStr)) {
								
								event.getMessage().delete();
								return;
							
							}
							
							long mrole = Long.parseLong(mroleStr);
							long srole = Long.parseLong(sroleStr);
							int ulimit = Integer.parseInt(ulimitStr);
							int qlimit = Integer.parseInt(qlimitStr);
							
							bot.getConfig().configNewSettings(g.getLongID(), mrole, srole, qlimit, ulimit);
							
							CanorBot.selfDestruct(60000, c.sendMessage("Successfully configured the bot!"));
							event.getMessage().delete();
							
						}
						
					} else if (sub.equalsIgnoreCase("roles")) {
						
						if(msgs.size() < 3) {
							
							event.getMessage().delete();
							return;
							
						}
						
						String rolesSub = msgs.get(2);
						if(rolesSub.equalsIgnoreCase("list")) {
							
							MessageBuilder builder = new MessageBuilder(event.getClient());
							builder.withChannel(c);
							
							for(IRole role : g.getRoles()) {
								builder.appendContent(role.getName() + " - " + role.getLongID() + "\n");
							}
							
							event.getMessage().delete();
							CanorBot.selfDestruct(120000, builder.send());
							
						} else if (rolesSub.equalsIgnoreCase("get")) {
							
							if(msgs.size() < 4) {
								
								event.getMessage().delete();
								return;
								
							}
							
							String rolename = msgs.get(3);
							List<IRole> roles = g.getRolesByName(rolename);
							
							if(roles.isEmpty()) {
								
								event.getMessage().delete();
								CanorBot.selfDestruct(60000, c.sendMessage("Could not find specified role."));
								return;
								
							} else {
								
								event.getMessage().delete();
								IRole role = roles.get(0);
								CanorBot.selfDestruct(60000, c.sendMessage(role.mention() + " - " + role.getLongID()));
								return;
						
							}
							
						} else if (rolesSub.equalsIgnoreCase("info")) {
							
							event.getMessage().delete();
							CanorBot.selfDestruct(120000, c.sendMessage("`/?configure roles list` - Lists all roles and their ids\n"
									+ "`/?configure roles get <name>` - Finds a role by its name."));
							
						}
						
					} else if (sub.equalsIgnoreCase("info")) {
						
						MessageBuilder builder = new MessageBuilder(event.getClient());
						builder.withChannel(c);
						
						builder.appendContent("`/?configure roles info` - Fetch roles on server\n");
						builder.appendContent("`/?configure set info` - Sets the roles, limits on the server\n");
						
						event.getMessage().delete();
						CanorBot.selfDestruct(120000, builder.send());
						
					} else if(sub.equalsIgnoreCase("settings")) {
						
						MessageBuilder builder = new MessageBuilder(event.getClient());
						builder.withChannel(c);
						
						Settings s = bot.getSettings(g);
						
						if(s == null) {
							
							builder.appendContent("You have not yet configured the settings on this server.");
							
						} else {
							
							builder.appendContent("These are your current settings:\n");
							builder.appendContent("Users can request a maximum of `" + s.getPerUserLimit() + "` songs at any given time.");
							builder.appendContent("The maximum number of songs in the queue at any given time is `" + s.getQueueLimit() + "`.");
							builder.appendContent(g.getRoleByID(s.getModRoleID()).mention() + " have the mod role within the bot.");
							builder.appendContent(g.getRoleByID(s.getStreamRoleID()).mention() + " can request streams within the bot");
							
						}
						
						builder.send();
						event.getMessage().delete();
						
					}
					
				}
				
			} else if (cmd.equalsIgnoreCase("/?leaveserver")) {
				
				if(hasPermissions(event, Permissions.ADMINISTRATOR)) {
					
					event.getMessage().delete();
					g.leave();
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasPermissions(MessageReceivedEvent event) {
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
	
	public boolean hasPermissions (MessageReceivedEvent event, Permissions perm) {
		if(event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(perm)) {
			return true;
		} else {
			return hasPermissions(event);
		}
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
