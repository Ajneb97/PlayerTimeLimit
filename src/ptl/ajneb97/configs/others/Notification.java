package ptl.ajneb97.configs.others;

import java.util.List;

public class Notification {

	private int seconds;
	private List<String> message;
	public Notification(int seconds, List<String> message) {
		super();
		this.seconds = seconds;
		this.message = message;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	public List<String> getMessage() {
		return message;
	}
	public void setMessage(List<String> message) {
		this.message = message;
	}
	
}
