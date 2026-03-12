package ptl.ajneb97.database.sqlite;

import org.bukkit.Bukkit;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.model.player.PlayerData;
import java.io.File;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SQLiteConnection {

    private PlayerTimeLimit plugin;
    private Connection connection;
    private String url;

    private final String SAVE_PLAYER_SQL = "INSERT INTO playertimelimit_players " +
            "(UUID, PLAYER_NAME, TOTAL_TIME, CURRENT_TIME, TIME_LIMIT, MESSAGE_MODE) " +
            "VALUES (?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT(UUID) DO UPDATE SET " +
            "PLAYER_NAME = excluded.PLAYER_NAME, " +
            "TOTAL_TIME = excluded.TOTAL_TIME, " +
            "CURRENT_TIME = excluded.CURRENT_TIME, " +
            "TIME_LIMIT = excluded.TIME_LIMIT, " +
            "MESSAGE_MODE = excluded.MESSAGE_MODE";

    public SQLiteConnection(PlayerTimeLimit plugin){
        this.plugin = plugin;
    }

    public void setup(){
        try {
            File file = new File(plugin.getDataFolder(), "players.db");
            url = "jdbc:sqlite:" + file.getAbsolutePath();
            connect();

            createTables();

            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&aSuccessfully connected to the SQLite Database."));

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+"&cError while connecting to the SQLite Database."));
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(url);
    }

    private Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()){
            connect();
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS playertimelimit_players" +
                " (UUID TEXT NOT NULL, " +
                " PLAYER_NAME TEXT, " +
                " TOTAL_TIME INT, " +
                " CURRENT_TIME INT, " +
                " TIME_LIMIT TEXT, " +
                " MESSAGE_MODE TEXT, " +
                " PRIMARY KEY ( UUID ))";

        String index = "CREATE INDEX IF NOT EXISTS idx_player_name " +
                "ON playertimelimit_players(PLAYER_NAME)";

        try{
            PreparedStatement statement = getConnection().prepareStatement(sql);
            statement.executeUpdate();
            statement.close();

            PreparedStatement statement2 = getConnection().prepareStatement(index);
            statement2.executeUpdate();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getPlayer(String uuid){
        PlayerData playerData = null;

        String sql = "SELECT * FROM playertimelimit_players WHERE UUID = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)){
            statement.setString(1, uuid);

            try (ResultSet result = statement.executeQuery()) {
                if(result.next()){
                    String playerName = result.getString("PLAYER_NAME");

                    playerData = new PlayerData(UUID.fromString(uuid),playerName);
                    playerData.setTotalTime(result.getInt("TOTAL_TIME"));
                    playerData.setCurrentTime(result.getInt("CURRENT_TIME"));
                    playerData.setTimeLimit(result.getString("TIME_LIMIT"));
                    playerData.setMessageMode(PlayerData.MessageMode.valueOf(result.getString("MESSAGE_MODE")));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return playerData;
    }

    public void savePlayer(PlayerData player) {
        try (PreparedStatement statement = getConnection().prepareStatement(SAVE_PLAYER_SQL)) {
            statement.setString(1, player.getUuid().toString());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getTotalTime());
            statement.setInt(4, player.getCurrentTime());
            statement.setString(5, player.getTimeLimit());
            statement.setString(6, player.getMessageMode().name());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayers(Map<UUID, PlayerData> players){
        try{
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            try(PreparedStatement statement = conn.prepareStatement(SAVE_PLAYER_SQL)){
                for(PlayerData player : players.values()){
                    statement.setString(1, player.getUuid().toString());
                    statement.setString(2, player.getName());
                    statement.setInt(3, player.getTotalTime());
                    statement.setInt(4, player.getCurrentTime());
                    statement.setString(5, player.getTimeLimit());
                    statement.setString(6, player.getMessageMode().name());

                    statement.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean resetPlayer(String playerName){
        String sql = "UPDATE playertimelimit_players SET CURRENT_TIME = 0 WHERE PLAYER_NAME = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)){
            statement.setString(1, playerName);
            int rows = statement.executeUpdate();
            return rows > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public void resetPlayers() {
        String sql = "UPDATE playertimelimit_players SET CURRENT_TIME = 0";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
