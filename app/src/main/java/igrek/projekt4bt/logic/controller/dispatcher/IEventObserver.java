package igrek.projekt4bt.logic.controller.dispatcher;

public interface IEventObserver {
	
	void registerEvents();
	
	void onEvent(AbstractEvent event);
	
}
