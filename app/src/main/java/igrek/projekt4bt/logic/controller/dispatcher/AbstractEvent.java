package igrek.projekt4bt.logic.controller.dispatcher;

public abstract class AbstractEvent {
	
	/**
	 * wykonuje akcję (lambda dla zrzutowanego typu zdarzenia) jeśl zdarzenie jest podanego typu
	 * @param eventClazz klasa zdarzenia
	 * @param action     akcja do wykonania w reakcji na zdarzenie
	 * @param <T>        typ zdarzenia
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractEvent> void bind(Class<T> eventClazz, IEventConsumer<T> action) {
		if (eventClazz.isInstance(this)) {
			action.accept((T) this);
		}
	}
	
}