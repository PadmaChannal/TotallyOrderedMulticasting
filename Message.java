
public class Message {
	private int fromPID,toPID;
	private long timeSent;
	private String type;
	public Message(){
		
	}
	public Message(String type,int fromPID, long timeSent) {
		
		this.fromPID = fromPID;
		
		this.timeSent = timeSent;
		this.type = type;
	}
	@Override
	public String toString() {
		return "Message [fromPID=" + fromPID + ", timeSent=" + timeSent + ", type=" + type + "]";
	}
	public int getFromPID() {
		return fromPID;
	}
	public void setFromPID(int fromPID) {
		this.fromPID = fromPID;
	}
	public int getToPID() {
		return toPID;
	}
	public void setToPID(int toPID) {
		this.toPID = toPID;
	}
	public long getTimeSent() {
		return timeSent;
	}
	public void setTimeSent(long timeSent) {
		this.timeSent = timeSent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
