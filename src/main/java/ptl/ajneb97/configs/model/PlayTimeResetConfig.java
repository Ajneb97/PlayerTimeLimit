package ptl.ajneb97.configs.model;

import java.time.ZoneId;

public class PlayTimeResetConfig {

    private Mode mode;
    private ZoneId timezone;
    private String hour;

    public PlayTimeResetConfig(Mode mode, ZoneId timezone, String hour) {
        this.mode = mode;
        this.timezone = timezone;
        this.hour = hour;
    }

    public Mode getMode() {
        return mode;
    }

    public ZoneId getTimezone() {
        return timezone;
    }

    public String getHour() {
        return hour;
    }

    public enum Mode {
        DAILY,
        WEEKLY,
        MONTHLY,
        COMMAND
    }
}
