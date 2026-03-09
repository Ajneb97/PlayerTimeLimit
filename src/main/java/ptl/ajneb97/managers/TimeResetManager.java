package ptl.ajneb97.managers;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.model.PlayTimeResetConfig;
import ptl.ajneb97.utils.TaskUtils;
import ptl.ajneb97.utils.TimeUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class TimeResetManager {

    private PlayerTimeLimit plugin;
    private long lastResetMillis;
    private long nextResetMillis;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TimeResetManager(PlayerTimeLimit plugin){
        this.plugin = plugin;
    }

    public long getLastResetMillis() {
        return lastResetMillis;
    }

    public void setLastResetMillis(long lastResetMillis) {
        this.lastResetMillis = lastResetMillis;
    }

    public void initialize() {
        long now = System.currentTimeMillis();

        // Time reset already happened.
        if (lastResetMillis > 0 && now >= calculateNextResetMillis(lastResetMillis)) {
            performReset();
        }else if(lastResetMillis == 0){
            performReset();
        }

        nextResetMillis = calculateNextResetMillis(now);
    }

    public void checkAndReset() {
        long now = System.currentTimeMillis();

        if (now >= nextResetMillis) {
            performReset();
            nextResetMillis = calculateNextResetMillis(now);
        }
    }

    private void performReset() {
        lastResetMillis = System.currentTimeMillis();

        plugin.getPlayerDataManager().resetTimeForAllPlayers(plugin.getMessagesConfig(),(result) -> {});
        TaskUtils.runAsync(() -> plugin.getConfigsManager().getDataConfigManager().saveConfig(lastResetMillis), plugin);
    }

    private long calculateNextResetMillis(long nowMillis) {
        PlayTimeResetConfig playTimeResetConfig = plugin.getConfigsManager().getMainConfigManager().getPlayTimeReset();

        ZoneId zone = playTimeResetConfig.getTimezone();
        LocalTime resetHour = LocalTime.parse(playTimeResetConfig.getHour());

        LocalDateTime now = Instant.ofEpochMilli(nowMillis)
                .atZone(zone)
                .toLocalDateTime();
        LocalDateTime nextReset;

        switch (playTimeResetConfig.getMode()) {
            case DAILY -> {
                nextReset = now.toLocalDate().atTime(resetHour);
                if (!now.isBefore(nextReset)) {
                    nextReset = nextReset.plusDays(1);
                }
            }

            case WEEKLY -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                nextReset = now
                        .with(weekFields.dayOfWeek(), 1)
                        .toLocalDate()
                        .atTime(resetHour);
                if (!now.isBefore(nextReset)) {
                    nextReset = nextReset.plusWeeks(1);
                }
            }

            case MONTHLY -> {
                nextReset = now
                        .withDayOfMonth(1)
                        .toLocalDate()
                        .atTime(resetHour);

                if (!now.isBefore(nextReset)) {
                    nextReset = nextReset.plusMonths(1);
                }
            }

            default -> {
                return -1; // COMMAND
            }
        }

        return nextReset.atZone(zone).toInstant().toEpochMilli();
    }

    private long getTimeUntilNextResetMillis(){
        if (nextResetMillis == -1) return -1; // COMMAND
        return Math.max(0, nextResetMillis - System.currentTimeMillis());
    }

    public String getTimeUntilNextReset(){
        long timeUntilNextResetMillis = getTimeUntilNextResetMillis();
        return TimeUtils.getTime(timeUntilNextResetMillis/1000,plugin.getMessagesManager());
    }

    public String getNextResetDateFormatted() {
        if (nextResetMillis == -1) {
            return "N/A";
        }

        PlayTimeResetConfig config = plugin.getConfigsManager().getMainConfigManager().getPlayTimeReset();
        ZoneId zone = config.getTimezone();
        ZonedDateTime nextReset = Instant.ofEpochMilli(nextResetMillis).atZone(zone);

        return nextReset.format(dateTimeFormatter);
    }

    public void configReload(){
        nextResetMillis = calculateNextResetMillis(System.currentTimeMillis());
    }
}
