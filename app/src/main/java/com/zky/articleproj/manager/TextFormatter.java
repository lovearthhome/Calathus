package com.zky.articleproj.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextFormatter {
	public static String getMusicTime(long duration) {
		return new SimpleDateFormat("mm:ss", Locale.CHINA).format(new Date(
				duration));
	}
}