package ptl.ajneb97.configs.model;

import java.util.List;

public class WorldWhitelistSystemConfig {
    private boolean enabled;
    private List<String> worlds;
    private String teleportCoordinatesOnKick;

    public WorldWhitelistSystemConfig(boolean enabled, List<String> worlds, String teleportCoordinatesOnKick) {
        this.enabled = enabled;
        this.worlds = worlds;
        this.teleportCoordinatesOnKick = teleportCoordinatesOnKick;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public String getTeleportCoordinatesOnKick() {
        return teleportCoordinatesOnKick;
    }
}
