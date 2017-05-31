package com.gmail.helpfulstranger999.discord.musics;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteJDBCLoader;

import com.google.common.collect.Maps;

import sx.blah.discord.handle.obj.IGuild;

public class Configuration {
	
	protected Connection conn = null;
	protected HashMap<Long, Settings> conf = Maps.newHashMap();
	
	public static final int DEFAULT_QUEUE = 1048576;
	public static final int DEFAULT_USER = 10;

	public Configuration() throws Exception {
		File file = new File("bot.sqlite");
		if(file.exists()) {
			load();
		} else {
			loadFirstTime();
		}
	}
	
	public void loadFirstTime () throws Exception {
		start();
		preparedUpdate(conn, "CREATE TABLE IF NOT EXISTS SETTINGS "
				+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "GUILD BIGINT UNIQUE NOT NULL, "
				+ "QUEUELIMIT INT DEFAULT 1048576, "
				+ "USERLIMIT INT DEFAULT 10, "
				+ "MROLE BIGINT DEFAULT 0, "
				+ "STREAMROLE BIGINT DEFAULT 0);");
	}
	
	public void load () throws Exception {
		start();
		ResultSet set = preparedQuery(conn, "SELECT * FROM SETTINGS;");
		while(set.next()) {
			long g = set.getLong("GUILD");
			int ql = set.getInt("QUEUELIMIT");
			int ul = set.getInt("USERLIMIT");
			long mrole = set.getLong("MROLE");
			long srole = set.getLong("STREAMROLE");
			Settings s = new Settings(g, ql, ul, mrole, srole);
			conf.put(g, s);
		}
	}
	
	public void unload () throws SQLException {
		stop();
		conf.clear();
	}
	
	public void reload () throws Exception {
		unload();
		load();
	}
	
	public void start () throws Exception {
		generateConnection();
	}
	
	public void stop () throws SQLException {
		if(conn == null) return;
		if(!conn.isClosed()) {
			conn.close();
		}
		conn = null;
	}
	
	public void restart () throws Exception {
		stop();
		start();
	}
	
	public Connection generateConnection () throws Exception {
		SQLiteJDBCLoader.initialize();
		SQLiteDataSource source = new SQLiteDataSource();
		source.setUrl("jdbc:sqlite:bot.sqlite");
		this.conn = source.getConnection();
		return conn;
	}
	
	protected static ResultSet preparedQuery (Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(sql).executeQuery();
	}
	
	protected static int preparedUpdate (Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(sql).executeUpdate();
	}
	
	public Settings getSettings (IGuild guild) {
		return (conf.containsKey(guild.getLongID())) ? conf.get(guild.getLongID()) : null;
	}
	
	public Settings configNewSettings (long guild, long mrole, long srole, int queue, int user) throws SQLException {
		preparedUpdate(conn, "INSERT INTO SETTINGS (GUILD, QUEUELIMIT, USERLIMIT, MROLE, STREAMROLE) VALUES (" + 
				guild + ", " + queue + ", " + user + ", " + mrole + ", " + srole + ");");
		Settings s = new Settings(guild, queue, user, mrole, srole);
		conf.put(guild, s);
		return s;
	}
	
	public class Settings {
		
		protected final long guild;
		
		protected int queuelimit = 1048576;
		protected int userlimit = 10;
		
		protected long modroleid = 0;
		protected long streamroleid = 0;
		
		protected Settings (long guild, int queuelimit, int userlimit, long modrole, long streamrole) {
			this.guild = guild;
			this.queuelimit = queuelimit;
			this.userlimit = userlimit;
			this.modroleid = modrole;
			this.streamroleid = streamrole;
		}
		
		public int getQueueLimit () {
			return this.queuelimit;
		}
		
		public int getPerUserLimit () {
			return this.userlimit;
		}
		
		public long getModRoleID () {
			return this.modroleid;
		}
		
		public long getStreamRoleID () {
			return this.streamroleid;
		}
		
		public long getGuildID () {
			return this.guild;
		}
		
		public void updateQueueLimit (int queuelimit) throws SQLException {
			int queue = queuelimit <= 1048576 ? queuelimit : 1048576;
			preparedUpdate(conn, "UPDATE SETTINGS SET QUEUELIMIT = " + queue + " WHERE GUILD = " + guild);
			this.queuelimit = queue;
		}
		
		public void updateUserLimit (int userlimit) throws SQLException {
			preparedUpdate(conn, "UPDATE SETTINGS SET USERLIMIT = " + userlimit + " WHERE GUILD = " + guild);
			this.userlimit = userlimit;
		}
		
		public void updateModRoleID (long modroleid) throws SQLException {
			preparedUpdate(conn, "UPDATE SETTINGS SET MROLE = " + modroleid + " WHERE GUILD = " + guild);
			this.modroleid = modroleid;
		}
		
		public void updateStreamRoleID (long streamroleid) throws SQLException {
			preparedUpdate(conn, "UPDATE SETTINGS SET STREAMROLE = " + streamroleid + " WHERE GUILD = " + guild);
			this.streamroleid = streamroleid;
		}
		
	}

}
