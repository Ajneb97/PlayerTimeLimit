package ptl.ajneb97.managers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

public class MensajesManager {
	
	private String prefix;
	private String actionBarMessage;
	private String bossBarMessage;
	private String timeSeconds;
	private String timeMinutes;
	private String timeHours;
	private String timeDays;
	private String timeInfinite;
	
	public MensajesManager(String prefix) {
		this.prefix = prefix;
	}

	public String getActionBarMessage() {
		return actionBarMessage;
	}

	public void setActionBarMessage(String actionBarMessage) {
		this.actionBarMessage = actionBarMessage;
	}

	public String getBossBarMessage() {
		return bossBarMessage;
	}

	public void setBossBarMessage(String bossBarMessage) {
		this.bossBarMessage = bossBarMessage;
	}

	public String getTimeSeconds() {
		return timeSeconds;
	}

	public void setTimeSeconds(String timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	public String getTimeMinutes() {
		return timeMinutes;
	}

	public void setTimeMinutes(String timeMinutes) {
		this.timeMinutes = timeMinutes;
	}

	public String getTimeHours() {
		return timeHours;
	}

	public void setTimeHours(String timeHours) {
		this.timeHours = timeHours;
	}

	public String getTimeDays() {
		return timeDays;
	}

	public void setTimeDays(String timeDays) {
		this.timeDays = timeDays;
	}

	public String getTimeInfinite() {
		return timeInfinite;
	}

	public void setTimeInfinite(String timeInfinite) {
		this.timeInfinite = timeInfinite;
	}

	public void enviarMensaje(CommandSender jugador,String mensaje,boolean prefix) {
		if(!mensaje.isEmpty()) {
			if(prefix) {
				jugador.sendMessage(getMensajeColor(this.prefix+mensaje));
			}else {
				jugador.sendMessage(getMensajeColor(mensaje));
			}
		}
	}
	
	public static String getMensajeColor(String texto) {
		if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") ) {
			Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
			Matcher match = pattern.matcher(texto);
			
			while(match.find()) {
				String color = texto.substring(match.start(),match.end());
				texto = texto.replace(color, ChatColor.of(color)+"");
				
				match = pattern.matcher(texto);
			}
		}
		
		texto = ChatColor.translateAlternateColorCodes('&', texto);
		return texto;
	}
}
