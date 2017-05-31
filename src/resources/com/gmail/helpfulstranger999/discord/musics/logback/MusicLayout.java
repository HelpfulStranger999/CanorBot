package com.gmail.helpfulstranger999.discord.musics.logback;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class MusicLayout extends LayoutBase<ILoggingEvent> {

	@Override
	public String doLayout(ILoggingEvent event) {
		LocalDateTime date = Instant.ofEpochMilli(event.getTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		StringBuffer builder = new StringBuffer();
		
		builder.append(date.getHour());
		builder.append(":");
		builder.append(date.getMinute());
		builder.append(":");
		builder.append(date.getSecond());
		builder.append(".");
		builder.append(date.getNano());
		
		builder.append(" [");
		builder.append(event.getClass().getSimpleName());
		builder.append("] ");
		
		builder.append(event.getLevel());
		builder.append(" - ");
		builder.append(event.getFormattedMessage());
		
		return builder.toString();
	}

}
