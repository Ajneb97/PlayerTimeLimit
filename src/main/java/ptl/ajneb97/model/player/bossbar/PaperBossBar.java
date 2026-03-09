package ptl.ajneb97.model.player.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PaperBossBar implements BossBarHandler{

    private final BossBar bossBar;

    public PaperBossBar(Player player, String colorString, String styleString){
        if(styleString.equals("SOLID")){
            styleString = "PROGRESS";
        }else if(styleString.contains("SEGMENTED")){
            styleString = styleString.replace("SEGMENTED","NOTCHED");
        }

        bossBar = BossBar.bossBar(Component.empty(),0,BossBar.Color.valueOf(colorString),BossBar.Overlay.valueOf(styleString));
        player.showBossBar(bossBar);
    }

    @Override
    public void setVisible(Player player, boolean value) {
        if(player == null){
            return;
        }

        player.hideBossBar(bossBar);
    }

    @Override
    public void setTitle(String title) {
        bossBar.name(MiniMessage.miniMessage().deserialize(title));
    }

    @Override
    public void setProgress(double progress) {
        bossBar.progress((float)progress);
    }

    @Override
    public void removeAll() {

    }

}
