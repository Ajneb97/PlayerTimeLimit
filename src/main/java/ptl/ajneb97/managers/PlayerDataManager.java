package ptl.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.model.internal.GenericCallback;
import ptl.ajneb97.model.player.PlayerData;
import ptl.ajneb97.utils.MiniMessageUtils;
import ptl.ajneb97.utils.TimeUtils;

import java.util.*;

public class PlayerDataManager {

    private PlayerTimeLimit plugin;
    private Map<UUID, PlayerData> players;
    private Map<String,UUID> playerNames;

    public PlayerDataManager(PlayerTimeLimit plugin){
        this.plugin = plugin;
        this.players = new HashMap<>();
        this.playerNames = new HashMap<>();
    }

    public Map<UUID,PlayerData> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerData p){
        players.put(p.getUuid(),p);
        playerNames.put(p.getName(), p.getUuid());
    }

    public void createPlayer(Player player){
        PlayerData playerData = new PlayerData(player.getUniqueId(),player.getName());
        addPlayer(playerData);
    }

    public PlayerData getPlayer(Player player){
        return players.get(player.getUniqueId());
    }

    private void updatePlayerName(String oldName,String newName,UUID uuid){
        if(oldName != null){
            playerNames.remove(oldName);
        }
        playerNames.put(newName,uuid);
    }

    public PlayerData getPlayerByUUID(UUID uuid){
        return players.get(uuid);
    }

    private UUID getPlayerUUID(String name){
        return playerNames.get(name);
    }

    public PlayerData getPlayerByName(String name){
        UUID uuid = getPlayerUUID(name);
        return players.get(uuid);
    }

    public void removePlayer(PlayerData playerData){
        players.remove(playerData.getUuid());
        playerNames.remove(playerData.getName());
    }

    public void removePlayerByUUID(UUID uuid){
        players.remove(uuid);
    }

    public void managePreLogin(AsyncPlayerPreLoginEvent event){
        if(!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)){
            return;
        }

        UUID uuid = event.getUniqueId();
        String playerName = event.getName();

        // Load player data from database if exists
        PlayerData playerData = plugin.getDatabaseManager().getPlayer(uuid.toString());
        if(playerData != null){
            addPlayer(playerData);
            if(playerData.getName() == null || !playerData.getName().equals(playerName)){
                updatePlayerName(playerData.getName(),playerName,uuid);
                playerData.setName(playerName);
            }
        }

        // Verify time
        int remainingTime = getRemainingTime(uuid);
        if(remainingTime != 0){
            return;
        }

        if(plugin.getConfigsManager().getMainConfigManager().getWorldWhitelistSystem().isEnabled()) {
            return;
        }

        // Kick player
        if(playerData != null){
            removePlayer(playerData);
        }

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        List<String> msg = messagesConfig.getStringList("joinErrorMessage");
        StringBuilder finalMessage = new StringBuilder();
        for(String line : msg) {
            finalMessage.append(line).append("\n");
        }

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        if(PlayerTimeLimitAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()){
            MiniMessageUtils.preLoginKickMessage(event,finalMessage.toString());
        }else{
            event.setKickMessage(MessagesManager.getLegacyColoredMessage(finalMessage.toString()));
        }
    }

    public void manageJoin(Player player){
        // Player should be already loaded from AsyncPlayerPreLoginEvent. If not, create it.
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            createPlayer(player);
        }
        if(isReadyForTimeTracking(player)) {
            updateTimeLimit(player);
        }
    }

    public void manageLeave(Player player){
        // Save player data into file and remove from map
        PlayerData playerData = getPlayer(player);
        if(playerData != null){
            plugin.getDatabaseManager().savePlayer(playerData);
            playerData.removeBossBar();
            removePlayer(playerData);
        }
    }

    public void updateTimeLimit(Player player){
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return;
        }

        Map<String,Integer> timeLimits = plugin.getConfigsManager().getMainConfigManager().getTimeLimits();
        if(player.isOp()){
            if(!playerData.getTimeLimit().equals("op")){
                playerData.setTimeLimit("op");
            }
            return;
        }

        String timeLimitName = "default";
        int timeLimitAmount = timeLimits.get("default");
        for(Map.Entry<String,Integer> entry : timeLimits.entrySet()){
            String key = entry.getKey();
            if(key.equals("default") || key.equals("op")){
                continue;
            }

            int time = entry.getValue();
            String permission = "playertimelimit.limit."+key;
            if(player.hasPermission(permission) && time > timeLimitAmount){
                timeLimitAmount = time;
                timeLimitName = entry.getKey();
            }
        }

        if(!playerData.getTimeLimit().equals(timeLimitName)){
            playerData.setTimeLimit(timeLimitName);
        }
    }

    public boolean isReadyForTimeTracking(Player player) {
        if (player == null || !player.isOnline()) {
            return false;
        }
        return plugin.getAuthMeSupport() == null || plugin.getAuthMeSupport().isReadyForTimeTracking(player);
    }

    public int getTimeLimit(UUID uuid) {
        PlayerData playerData = getPlayerByUUID(uuid);
        Map<String,Integer> timeLimits = plugin.getConfigsManager().getMainConfigManager().getTimeLimits();
        int timeLimitDefault = timeLimits.get("default");
        if(playerData == null){
            return timeLimitDefault;
        }

        String timeLimit = playerData.getTimeLimit();
        if(timeLimit == null){
            return timeLimitDefault;
        }
        if(timeLimits.containsKey(timeLimit)){
            return timeLimits.get(timeLimit);
        }

        return timeLimitDefault;
    }

    public int getCurrentTime(Player player){
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return 0;
        }
        return playerData.getCurrentTime();
    }

    public int getRemainingTime(UUID uuid){
        int timeLimit = getTimeLimit(uuid);
        if(timeLimit == 0){
            return -1;
        }else{
            PlayerData playerData = getPlayerByUUID(uuid);
            if(playerData == null){
                return timeLimit;
            }

            int remainingTime = timeLimit-playerData.getCurrentTime();
            return Math.max(remainingTime,0);
        }
    }

    public double getRemainingLimitRatio(Player player){
        int timeLimit = getTimeLimit(player.getUniqueId());
        if(timeLimit == 0){
            return 1;
        }

        PlayerData playerData = getPlayer(player);
        int remainingTime = playerData != null ? timeLimit-playerData.getCurrentTime() : timeLimit;

        double ratio = (double) remainingTime/timeLimit;
        if(ratio < 0) {
            ratio = 0;
        }else if(ratio > 1) {
            ratio = 1;
        }

        return ratio;
    }

    public String getTimeLeft(Player player) {
        MessagesManager msgManager = plugin.getMessagesManager();
        int remainingTime = getRemainingTime(player.getUniqueId());
        return TimeUtils.getTime(remainingTime,msgManager);
    }

    public boolean hasTimeLeft(Player player) {
        int remainingTime = getRemainingTime(player.getUniqueId());
        if(remainingTime == -1){
            return true;
        }
        return remainingTime > 0;
    }

    public String getTotalTime(Player player) {
        MessagesManager msgManager = plugin.getMessagesManager();
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return TimeUtils.getTime(0,msgManager);
        }

        return TimeUtils.getTime(playerData.getTotalTime(),msgManager);
    }

    public boolean isMessageEnabled(Player player){
        boolean messageEnabledByDefault = plugin.getConfigsManager().getMainConfigManager().isInformationMessageEnabledByDefault();
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return messageEnabledByDefault;
        }
        if(messageEnabledByDefault){
            return playerData.getMessageMode() != PlayerData.MessageMode.DISABLED;
        }else{
            return playerData.getMessageMode() == PlayerData.MessageMode.ENABLED;
        }
    }

    public String setMessageEnabled(Player player){
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return null;
        }

        if(playerData.getMessageMode() == PlayerData.MessageMode.NONE){
            boolean messageEnabledByDefault = plugin.getConfigsManager().getMainConfigManager().isInformationMessageEnabledByDefault();
            if(messageEnabledByDefault){
                playerData.setMessageMode(PlayerData.MessageMode.DISABLED);
            }else{
                playerData.setMessageMode(PlayerData.MessageMode.ENABLED);
            }
        }else{
            if(playerData.getMessageMode() == PlayerData.MessageMode.ENABLED){
                playerData.setMessageMode(PlayerData.MessageMode.DISABLED);
            }else{
                playerData.setMessageMode(PlayerData.MessageMode.ENABLED);
            }
        }

        if(playerData.getMessageMode() == PlayerData.MessageMode.ENABLED){
            String timeLeft = getTimeLeft(player);
            plugin.getPlayerTimeManager().sendBossBar(player,this,plugin.getConfigsManager().getMainConfigManager(),timeLeft);
            return "messageEnabled";
        }else{
            removeBossBar(player);
            return "messageDisabled";
        }
    }

    public void increaseTime(Player player){
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return;
        }

        playerData.increaseTime();
    }

    public void addPlayTime(Player player, int time) {
        int timeLimit = getTimeLimit(player.getUniqueId());
        if(timeLimit == 0) {
            return;
        }

        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return;
        }

        playerData.addPlayTime(time);
    }

    public void takePlayTime(Player player, int time) {
        int timeLimit = getTimeLimit(player.getUniqueId());
        if(timeLimit == 0) {
            return;
        }

        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return;
        }

        playerData.takePlayTime(time);

        // Prevents overflow.
        int remainingTime = timeLimit-playerData.getCurrentTime();
        if(remainingTime <= 0) {
            playerData.setCurrentTime(timeLimit);
        }
    }

    public void removeBossBar(Player player){
        PlayerData playerData = getPlayer(player);
        if(playerData == null){
            return;
        }

        playerData.removeBossBar();
    }

    public void removeBossBars(){
        for(Player player : Bukkit.getOnlinePlayers()){
            removeBossBar(player);
        }
    }

    public void resetTimeForPlayer(String playerName, FileConfiguration messagesConfig, GenericCallback<String> callback){
        PlayerData playerData = getPlayerByName(playerName);
        if(playerData == null){
            // Check offline
            plugin.getDatabaseManager().resetPlayer(playerName, result -> {
                String returnMessage;
                if(result){
                    returnMessage = messagesConfig.getString("commandResetTimeCorrect").replace("%player%",playerName);
                }else{
                    returnMessage = messagesConfig.getString("playerDoesNotExists");
                }

                callback.onDone(returnMessage);
            });
        }else{
            // Check online
            playerData.resetTime();
            callback.onDone(messagesConfig.getString("commandResetTimeCorrect").replace("%player%",playerName));
        }
    }

    public void resetTimeForAllPlayers(FileConfiguration messagesConfig, GenericCallback<String> callback){
        players.values().forEach(PlayerData::resetTime);
        plugin.getDatabaseManager().resetPlayers((result) -> {
            callback.onDone(messagesConfig.getString("commandResetTimeAllCorrect"));
        });
    }

    public void configReload(){
        removeBossBars();
    }
}
