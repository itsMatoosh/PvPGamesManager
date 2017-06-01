package pl.glonojad.pvpgamesmanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter {
	
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

	@SuppressWarnings("deprecation")
	public static String timeConversion(int totalSeconds) {
        int hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        int minutes = (totalSeconds - (hoursToSeconds(hours)))
                / SECONDS_IN_A_MINUTE;
        int seconds = totalSeconds
                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));
        if(hours > 0) {
        	return new SimpleDateFormat("HH:mm:ss").format(new Date(0, 0, 0, hours, minutes, seconds));	
        }
        if(minutes > 0) {
        	return new SimpleDateFormat("mm:ss").format(new Date(0, 0, 0, hours, minutes, seconds));	
        }
        if(seconds > 0) {
        	return new SimpleDateFormat("ss").format(new Date(0, 0, 0, hours, minutes, seconds));	
        }
        return null;
    }

    private static int hoursToSeconds(int hours) {
        return hours * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE;
    }

    private static int minutesToSeconds(int minutes) {
        return minutes * SECONDS_IN_A_MINUTE;
    }
}
