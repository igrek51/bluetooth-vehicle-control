package igrek.projekt4bt.events;


import igrek.projekt4bt.dispatcher.AbstractEvent;

public class ResizedEvent extends AbstractEvent {
	
	private int w;
	private int h;
	
	public ResizedEvent(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
}
