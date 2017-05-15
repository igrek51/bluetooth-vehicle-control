package igrek.projekt4bt.events;


import igrek.projekt4bt.dispatcher.AbstractEvent;
import igrek.projekt4bt.graphics.canvas.InfoMessage;

public class ShowInfoEvent extends AbstractEvent {
	
	private InfoMessage infoMessage;
	
	public ShowInfoEvent(String message, InfoMessage.ShowInfoType type) {
		this.infoMessage = new InfoMessage(message, type);
	}
	
	public InfoMessage getInfoMessage() {
		return infoMessage;
	}
}
