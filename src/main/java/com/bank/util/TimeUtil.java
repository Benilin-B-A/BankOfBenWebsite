package com.bank.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {
	
	public static ZonedDateTime getDateTime(long time) {
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
	}
}
