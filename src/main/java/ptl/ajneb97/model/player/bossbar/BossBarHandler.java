package ptl.ajneb97.model.player.bossbar;


import org.bukkit.entity.Player;

public interface BossBarHandler {

    void setVisible(Player player, boolean value);

    void setTitle(String title);

    void setProgress(double progress);

    void removeAll();
}
