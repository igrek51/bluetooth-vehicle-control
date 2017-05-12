package igrek.projekt4bt.logic.controller.dispatcher;

public interface IEventConsumer<T> {
	void accept(T e);
}