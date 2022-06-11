package ptl.ajneb97.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import ptl.ajneb97.PlayerTimeLimit;

public class BypassTimes {

	public static boolean isBypassNow(PlayerTimeLimit plugin) {

		if (plugin.getConfig().contains("bypass_time")) {
			for (String name : plugin.getConfig().getConfigurationSection("bypass_time").getKeys(false)) {

				SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
				LocalDateTime now = LocalDateTime.now();
				String currentTime = dtf.format(now);

				try {
					Date curTime = parser.parse(currentTime);
					Date start = parser.parse((String) plugin.getConfig().getConfigurationSection("bypass_time")
							.getConfigurationSection(name).get("start"));
					Date end = parser.parse((String) plugin.getConfig().getConfigurationSection("bypass_time")
							.getConfigurationSection(name).get("end"));

					if (curTime.after(start) && curTime.before(end)) {
						return true;
					}

				} catch (Exception e) {
					System.out.println(
							"[PlayerTimeLimit] There were some errors within the bypass calculation. Please double-check your config file");
				}
				return false;
			}

		}

		return false;
	}

}
