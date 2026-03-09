package ptl.ajneb97.utils;

import ptl.ajneb97.managers.MessagesManager;

public class TimeUtils {

	public static String getTime(long seconds, MessagesManager msgManager) {
		if (seconds == -1) {
			return msgManager.getTimeInfinite();
		}

		if (seconds == 0) {
			return seconds + msgManager.getTimeSeconds();
		}

		long totalMin = seconds / 60;
		long totalHour = totalMin / 60;
		long totalDay = totalHour / 24;

		StringBuilder time = new StringBuilder();

		if (seconds > 59) {
			seconds = seconds - 60 * totalMin;
		}

		if (totalMin > 59) {
			totalMin = totalMin - 60 * totalHour;
		}

		if (totalHour > 24) {
			totalHour = totalHour - 24 * totalDay;
		}

		if (totalDay > 0) {
			time.append(totalDay).append(msgManager.getTimeDays()).append(" ");
		}

		if (totalHour > 0) {
			time.append(totalHour).append(msgManager.getTimeHours()).append(" ");
		}

		if (totalMin > 0) {
			time.append(totalMin).append(msgManager.getTimeMinutes()).append(" ");
		}

		if (seconds > 0) {
			time.append(seconds).append(msgManager.getTimeSeconds());
		}

		return time.toString().trim();
	}
}
