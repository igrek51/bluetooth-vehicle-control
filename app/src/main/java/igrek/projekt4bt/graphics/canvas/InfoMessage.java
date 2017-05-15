package igrek.projekt4bt.graphics.canvas;


import java.util.Date;

public class InfoMessage {
	
	private String message;
	private Date time;
	private ShowInfoType type;
	
	public enum ShowInfoType {
		
		OK,
		
		ERROR,
		
		BT_RECEIVED;
	}
	
	public InfoMessage(String message, ShowInfoType type) {
		this.message = message;
		this.time = new Date();
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Date getTime() {
		return time;
	}
	
	public ShowInfoType getType() {
		return type;
	}
}
