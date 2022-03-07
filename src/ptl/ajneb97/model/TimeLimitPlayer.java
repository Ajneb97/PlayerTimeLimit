package ptl.ajneb97.model;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TimeLimitPlayer {

	private String uuid;
	private String name;
	private Player player;
	private int currentTime; //El tiempo que lleva en el dia y sin reiniciarse
	private int totalTime; //El tiempo que ha jugado el jugador por siempre
	private boolean messageEnabled;
	
	private BossBar bossBar;
	
	public TimeLimitPlayer(String uuid, String name) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.currentTime = 0;
		this.totalTime = 0;
		this.messageEnabled = false;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
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
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public boolean isMessageEnabled() {
		return messageEnabled;
	}
	public void setMessageEnabled(boolean messageEnabled) {
		this.messageEnabled = messageEnabled;
	}
	
	public BossBar getBossBar() {
		return bossBar;
	}
	public void setBossBar(BossBar bossBar) {
		this.bossBar = bossBar;
	}
	public void eliminarBossBar() {
		if(bossBar != null) {
			bossBar.removeAll();
			bossBar.setVisible(false);
			bossBar = null;
		}
	}
	
	public void resetTime() {
		this.setCurrentTime(0);
	}
	
	public void takeTime(int time) {
		currentTime = currentTime+time;
	}
	
	public void addTime(int time) {
		currentTime = currentTime-time;
	}
}
