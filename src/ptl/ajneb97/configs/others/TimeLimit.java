package ptl.ajneb97.configs.others;

public class TimeLimit {

	private String name;
	private int time;
	public TimeLimit(String name, int time) {
		super();
		this.name = name;
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
}
