package igrek.projekt4bt.events;


import igrek.projekt4bt.dispatcher.AbstractEvent;
import igrek.projekt4bt.logic.ControlCommand;

public class ControlMotorsEvent extends AbstractEvent {
	
	private ControlCommand controls;
	
	public ControlMotorsEvent(ControlCommand controls) {
		this.controls = controls;
	}
	
	public ControlCommand getControls() {
		return controls;
	}
}
