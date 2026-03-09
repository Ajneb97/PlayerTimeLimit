package ptl.ajneb97.model.player.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ptl.ajneb97.managers.MessagesManager;

public class SpigotBossBar implements BossBarHandler{

    private final BossBar bossBar;

    public SpigotBossBar(Player player,String colorString,String styleString){
        BarColor color = BarColor.valueOf(colorString);
        BarStyle style = BarStyle.valueOf(styleString);

        bossBar = Bukkit.getServer().createBossBar("", color, style, new BarFlag[0]);
        bossBar.removeAll();
        bossBar.addPlayer(player);
        bossBar.setProgress(0);
        bossBar.setVisible(true);
    }

    @Override
    public void setVisible(Player player,boolean value) {
        bossBar.setVisible(value);
    }

    @Override
    public void setTitle(String title) {
        bossBar.setTitle(MessagesManager.getLegacyColoredMessage(title));
    }

    @Override
    public void setProgress(double progress) {
        bossBar.setProgress(progress);
    }

    @Override
    public void removeAll() {
        bossBar.removeAll();
    }
}
