package igrek.projekt4bt.logic.controller.dispatcher;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import igrek.projekt4bt.logger.Logs;

public class EventDispatcher {
	
	private Map<Class<? extends AbstractEvent>, List<IEventObserver>> eventObservers;
	
	private List<AbstractEvent> eventsQueue;
	
	private boolean dispatching = false;
	
	public EventDispatcher() {
		eventObservers = new HashMap<>();
		eventsQueue = new ArrayList<>();
	}
	
	public void registerEventObserver(Class<? extends AbstractEvent> eventClass, IEventObserver observer) {
		synchronized (eventObservers) {
			List<IEventObserver> observers = eventObservers.get(eventClass);
			if (observers == null) {
				observers = new ArrayList<>();
			}
			if (!observers.contains(observer)) {
				observers.add(observer);
			}
			eventObservers.put(eventClass, observers);
		}
	}
	
	public void unregisterEventObserver(IEventObserver observer) {
		synchronized (eventObservers) {
			for (Class<? extends AbstractEvent> clazz : eventObservers.keySet()) {
				List<IEventObserver> observers = eventObservers.get(clazz);
				if (observers != null) {
					observers.remove(observer);
				}
			}
		}
	}
	
	public void sendEvent(AbstractEvent event) {
		synchronized (eventsQueue) {
			eventsQueue.add(event);
		}
		dispatchEvents();
	}
	
	public void clearEventObservers(Class<? extends AbstractEvent> eventClass) {
		List<IEventObserver> observers = eventObservers.get(eventClass);
		if (observers != null) {
			observers.clear();
		}
		eventObservers.put(eventClass, null);
	}
	
	private void dispatchEvents() {
		if (dispatching)
			return;
		dispatching = true;
		
		synchronized (eventsQueue) {
			while (!eventsQueue.isEmpty()) {
				dispatch(eventsQueue.get(0));
				eventsQueue.remove(0);
			}
		}
		
		dispatching = false;
	}
	
	private void dispatch(AbstractEvent event) {
		synchronized (eventObservers) {
			List<IEventObserver> observers = eventObservers.get(event.getClass());
			if (observers == null || observers.isEmpty()) {
				Logs.warn("no observer for event " + event.getClass().getName());
			}
			if (observers != null) {
				for (IEventObserver observer : observers) {
					observer.onEvent(event);
				}
			}
		}
	}
}

