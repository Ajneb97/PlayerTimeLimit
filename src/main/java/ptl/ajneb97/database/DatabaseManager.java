package ptl.ajneb97.database;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.database.sqlite.SQLiteConnection;
import ptl.ajneb97.model.internal.GenericCallback;
import ptl.ajneb97.model.player.PlayerData;
import ptl.ajneb97.utils.TaskUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private PlayerTimeLimit plugin;
    private SQLiteConnection sqLiteConnection;

    public DatabaseManager(PlayerTimeLimit plugin){
        this.plugin = plugin;
    }

    public void configure(){
        sqLiteConnection = new SQLiteConnection(plugin);
        sqLiteConnection.setup();
    }

    public PlayerData getPlayer(String uuid){
        return sqLiteConnection.getPlayer(uuid);
    }

    public void savePlayer(PlayerData player){
        TaskUtils.runAsync(() -> sqLiteConnection.savePlayer(player), plugin);
    }

    public void savePlayers(boolean async){
        if(async){
            Map<UUID, PlayerData> playersCopy = new HashMap<>(plugin.getPlayerDataManager().getPlayers());
            TaskUtils.runAsync(() -> sqLiteConnection.savePlayers(playersCopy), plugin);
        }else{
            sqLiteConnection.savePlayers(plugin.getPlayerDataManager().getPlayers());
        }
    }

    public void resetPlayer(String playerName,GenericCallback<Boolean> callback){
        TaskUtils.runAsync(() -> {
            boolean result = sqLiteConnection.resetPlayer(playerName);
            TaskUtils.runSync(() -> callback.onDone(result), plugin);
        }, plugin);
    }

    public void resetPlayers(GenericCallback<Boolean> callback){
        TaskUtils.runAsync(() -> {
            sqLiteConnection.resetPlayers();
            TaskUtils.runSync(() -> callback.onDone(true), plugin);
        }, plugin);
    }

    public void closeConnection(){
        savePlayers(false);
        sqLiteConnection.close();
    }
}
