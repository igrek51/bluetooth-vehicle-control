package igrek.projekt4bt.dispatcher;

public interface IEventConsumer<T> {
	void accept(T e);
}