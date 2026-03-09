package ptl.ajneb97.configs.model;

public class BossBarConfig {
    private boolean enabled;
    private String bossBarColor;
    private String bossBarStyle;

    public BossBarConfig(boolean enabled, String bossBarColor, String bossBarStyle) {
        this.enabled = enabled;
        this.bossBarColor = bossBarColor;
        this.bossBarStyle = bossBarStyle;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getBossBarColor() {
        return bossBarColor;
    }

    public String getBossBarStyle() {
        return bossBarStyle;
    }
}
