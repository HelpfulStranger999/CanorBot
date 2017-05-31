package com.gmail.helpfulstranger999.discord.musics;

public class LaunchProgram {

	public static void main(String[] args) {
		try {
			CanorBot bot = new CanorBot();
			bot.start();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
