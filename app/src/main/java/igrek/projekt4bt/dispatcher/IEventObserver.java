package igrek.projekt4bt.dispatcher;

public interface IEventObserver {
	
	void registerEvents();
	
	void onEvent(AbstractEvent event);
	
}
