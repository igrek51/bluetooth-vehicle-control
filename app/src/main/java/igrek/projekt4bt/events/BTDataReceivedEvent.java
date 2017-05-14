package igrek.projekt4bt.events;


import igrek.projekt4bt.dispatcher.AbstractEvent;

public class BTDataReceivedEvent extends AbstractEvent {
	
	private String message;
	
	public BTDataReceivedEvent(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
