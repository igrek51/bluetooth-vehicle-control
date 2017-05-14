package igrek.projekt4bt.events;


import igrek.projekt4bt.dispatcher.AbstractEvent;

public class ShowInfoEvent extends AbstractEvent {
	
	private String message;
	
	public ShowInfoEvent(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
