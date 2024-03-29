package com.bank.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String getDateTime(long time) {
		ZonedDateTime zDT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
		return zDT.format(formatter);
	}
}
