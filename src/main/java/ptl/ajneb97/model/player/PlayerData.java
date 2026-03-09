package ptl.ajneb97.model.player;


import org.bukkit.Bukkit;
import ptl.ajneb97.model.player.bossbar.BossBarHandler;

import java.util.UUID;

public class PlayerData {

	private UUID uuid;
	private String name;
	private int currentTime; // Time without being reset
	private int totalTime;
	private String timeLimit;
	private MessageMode messageMode;

	private BossBarHandler bossBarHandler;

	
	public PlayerData(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.currentTime = 0;
		this.totalTime = 0;
		this.timeLimit = "default";
		this.messageMode = MessageMode.NONE;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public void increaseTime() {
		this.currentTime++;
		this.totalTime++;
	}

	public MessageMode getMessageMode() {
		return messageMode;
	}

	public void setMessageMode(MessageMode messageMode) {
		this.messageMode = messageMode;
	}

	public void resetTime() {
		this.setCurrentTime(0);
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void addPlayTime(int amount){
		this.currentTime = this.currentTime-amount;
	}

	public void takePlayTime(int amount){
		this.currentTime = this.currentTime+amount;
	}

	public BossBarHandler getBossBarHandler() {
		return bossBarHandler;
	}

	public void setBossBarHandler(BossBarHandler bossBarHandler) {
		this.bossBarHandler = bossBarHandler;
	}

	public void removeBossBar() {
		if(bossBarHandler != null) {
			bossBarHandler.removeAll();
			bossBarHandler.setVisible(Bukkit.getPlayer(uuid),false);
			bossBarHandler = null;
		}
	}

	public enum MessageMode {
		ENABLED,
		DISABLED,
		NONE;
	}
}
