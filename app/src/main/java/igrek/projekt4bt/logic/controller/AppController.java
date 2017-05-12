package igrek.projekt4bt.logic.controller;


import igrek.projekt4bt.logic.controller.dispatcher.AbstractEvent;
import igrek.projekt4bt.logic.controller.dispatcher.EventDispatcher;
import igrek.projekt4bt.logic.controller.dispatcher.IEventObserver;
import igrek.projekt4bt.logic.controller.services.IService;
import igrek.projekt4bt.logic.controller.services.ServicesRegistry;

public class AppController {
	
	private ServicesRegistry servicesRegistry;
	
	private EventDispatcher eventDispatcher;
	
	private static AppController instance = null;
	
	/**
	 * Reset instacji rejestru usług i wyczyszczenie listenerów eventów
	 */
	public AppController() {
		servicesRegistry = new ServicesRegistry();
		eventDispatcher = new EventDispatcher();
		instance = this;
	}
	
	private static AppController getInstance() {
		if (instance == null) {
			new AppController();
		}
		return instance;
	}
	
	
	public static <T extends IService> void registerService(T service) {
		getInstance().servicesRegistry.registerService(service);
	}
	
	public static <T extends IService> T getService(Class<T> clazz) {
		return getInstance().servicesRegistry.getService(clazz);
	}
	
	//TODO rejestracja event observerów dla pojedynczych instancji klas (automatyczne przejmowanie eventów)
	
	public static void registerEventObserver(Class<? extends AbstractEvent> eventClass, IEventObserver observer) {
		getInstance().eventDispatcher.registerEventObserver(eventClass, observer);
	}
	
	//TODO poprawić strukturę aplikacji tak, aby nie musieć czyścić observerów
	@Deprecated
	public static void clearEventObservers(Class<? extends AbstractEvent> eventClass) {
		getInstance().eventDispatcher.clearEventObservers(eventClass);
	}
	
	public static void sendEvent(AbstractEvent event) {
		getInstance().eventDispatcher.sendEvent(event);
	}
}
