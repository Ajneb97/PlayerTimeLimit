package ptl.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.MainConfigManager;
import ptl.ajneb97.configs.model.BossBarConfig;
import ptl.ajneb97.configs.model.WorldWhitelistSystemConfig;
import ptl.ajneb97.libs.actionbar.ActionBarAPI;
import ptl.ajneb97.model.player.PlayerData;
import ptl.ajneb97.model.player.bossbar.BossBarHandler;
import ptl.ajneb97.model.player.bossbar.PaperBossBar;
import ptl.ajneb97.model.player.bossbar.SpigotBossBar;
import ptl.ajneb97.utils.ActionUtils;
import java.util.List;

public class PlayerTimeManager {

    private PlayerTimeLimit plugin;

    public PlayerTimeManager(PlayerTimeLimit plugin){
        this.plugin = plugin;
    }

    public void updateTimePlayer(Player player, MainConfigManager mainConfigManager){
        World world = player.getWorld();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        if(!isValidWorld(world,mainConfigManager)) {
            playerDataManager.removeBossBar(player);
            return;
        }

        playerDataManager.increaseTime(player);

        int remainingTime = playerDataManager.getRemainingTime(player.getUniqueId());

        sendNotification(player,mainConfigManager,remainingTime);
        checkPlayerTime(player,mainConfigManager,remainingTime);
    }

    public void sendInfoBars(Player player, PlayerDataManager playerDataManager, MainConfigManager mainConfigManager){
        String timeLeftString = playerDataManager.getTimeLeft(player);
        boolean isMessageEnabled = playerDataManager.isMessageEnabled(player);
        if(isMessageEnabled){
            sendActionBar(player,mainConfigManager,timeLeftString);
            sendBossBar(player,playerDataManager,mainConfigManager,timeLeftString);
        }
    }

    private void sendActionBar(Player player,MainConfigManager mainConfigManager,String timeLeftString){
        if(!mainConfigManager.isActionBar()){
            return;
        }

        MessagesManager msgManager = plugin.getMessagesManager();
        String actionBarMessage = msgManager.getActionBarMessage();
        actionBarMessage = actionBarMessage.replace("%time%", timeLeftString);
        ActionBarAPI.sendActionBar(player, actionBarMessage);
    }

    public void sendBossBar(Player player,PlayerDataManager playerDataManager,MainConfigManager mainConfigManager,String timeLeftString){
        BossBarConfig bossBarConfig = mainConfigManager.getBossBar();
        if(!bossBarConfig.isEnabled()){
            return;
        }

        PlayerData playerData = playerDataManager.getPlayer(player);
        if(playerData == null){
            return;
        }

        MessagesManager msgManager = plugin.getMessagesManager();

        BossBarHandler bossBarHandler = playerData.getBossBarHandler();
        if(bossBarHandler == null){
            // Create it
            if(mainConfigManager.isUseMiniMessage()){
                bossBarHandler = new PaperBossBar(
                        player,bossBarConfig.getBossBarColor(),bossBarConfig.getBossBarStyle()
                );
            }else{
                bossBarHandler = new SpigotBossBar(
                        player,bossBarConfig.getBossBarColor(),bossBarConfig.getBossBarStyle()
                );
            }

            playerData.setBossBarHandler(bossBarHandler);
        }

        double ratio = playerDataManager.getRemainingLimitRatio(player);

        bossBarHandler.setTitle(msgManager.getBossBarMessage().replace("%time%",timeLeftString));
        bossBarHandler.setProgress(ratio);
    }

    private void sendNotification(Player player,MainConfigManager mainConfigManager,int remainingTime){
        List<String> notificationActions = mainConfigManager.getNotifications().get(remainingTime);
        if(notificationActions == null) {
            return;
        }

        for(String action : notificationActions) {
            ActionUtils.executeAction(player,action,plugin);
        }
    }

    private void checkPlayerTime(Player player,MainConfigManager mainConfigManager,int remainingTime){
        if(remainingTime != 0) {
            return;
        }

        // Player has completed their time.
        WorldWhitelistSystemConfig worldWhitelistSystemConfig = mainConfigManager.getWorldWhitelistSystem();
        if(worldWhitelistSystemConfig.isEnabled()){
            worldWhitelistSystemKick(player,worldWhitelistSystemConfig);
            return;
        }

        List<String> msg = plugin.getMessagesConfig().getStringList("kickMessage");
        StringBuilder finalMessage = new StringBuilder();
        for(String line : msg) {
            finalMessage.append(line).append("\n");
        }

        ActionUtils.kick(player,finalMessage.toString());
    }

    public boolean isValidWorld(World world,MainConfigManager mainConfigManager) {
        WorldWhitelistSystemConfig worldWhitelistSystemConfig = mainConfigManager.getWorldWhitelistSystem();
        if(!worldWhitelistSystemConfig.isEnabled()) {
            return true;
        }

        List<String> worlds = worldWhitelistSystemConfig.getWorlds();
        return worlds.contains(world.getName());
    }

    private void worldWhitelistSystemKick(Player player,WorldWhitelistSystemConfig worldWhitelistSystemConfig) {
        String coordinates = worldWhitelistSystemConfig.getTeleportCoordinatesOnKick();
        try {
            String[] sep = coordinates.split(";");
            World world = Bukkit.getWorld(sep[0]);
            double x = Double.parseDouble(sep[1]);
            double y = Double.parseDouble(sep[2]);
            double z = Double.parseDouble(sep[3]);
            float yaw = Float.parseFloat(sep[4]);
            float pitch = Float.parseFloat(sep[5]);

            player.teleport(new Location(world,x,y,z,yaw,pitch));

            MessagesManager msgManager = plugin.getMessagesManager();
            List<String> msg = plugin.getMessagesConfig().getStringList("kickMessage");
            for(String m : msg) {
                msgManager.sendMessage(player,m,false);
            }
        }catch(Exception e) {
            player.sendMessage(PlayerTimeLimit.prefix+"&cError! Impossible to teleport &7"+player.getName()
                    +" &cto this coordinates: &7"+coordinates);
        }
    }

    public void manageOnTeleport(Player player, PlayerTeleportEvent event){
        Location from = event.getFrom();
        Location to = event.getTo();
        if(from.getWorld().equals(to.getWorld())){
            return;
        }

        WorldWhitelistSystemConfig worldWhitelistSystemConfig = plugin.getConfigsManager().getMainConfigManager().getWorldWhitelistSystem();
        if(!worldWhitelistSystemConfig.isEnabled()){
            return;
        }

        List<String> worlds = worldWhitelistSystemConfig.getWorlds();
        if(!worlds.contains(to.getWorld().getName())) {
            return;
        }

        if(!plugin.getPlayerDataManager().hasTimeLeft(player)){
            MessagesManager msgManager = plugin.getMessagesManager();
            List<String> msg = plugin.getMessagesConfig().getStringList("joinErrorMessage");
            for(String m : msg) {
                msgManager.sendMessage(player,m,false);
            }
            event.setCancelled(true);
        }
    }
}
