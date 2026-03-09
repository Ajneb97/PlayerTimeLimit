package ptl.ajneb97.api;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ptl.ajneb97.PlayerTimeLimit;

public class ExpansionPlayerTimeLimit extends PlaceholderExpansion {

    private PlayerTimeLimit plugin;

    public ExpansionPlayerTimeLimit(PlayerTimeLimit plugin) {
    	this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return "Ajneb97";
    }

    @Override
    public String getIdentifier(){
        return "playertimelimit";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(identifier.equals("time_until_reset")){
            // %playertimelimit_time_until_reset%
            return PlayerTimeLimitAPI.getTimeUntilNextReset();
        }
        if(player == null){
            return "";
        }

        if(identifier.equals("time_left")){
            // %playertimelimit_time_left%
            return PlayerTimeLimitAPI.getTimeLeft(player);
        }else if(identifier.equals("total_time")){
            // %playertimelimit_total_time%
            return PlayerTimeLimitAPI.getTotalTime(player);
        }

        return null;
    }
}
