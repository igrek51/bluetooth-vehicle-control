package igrek.projekt4bt.logic.app;


import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import igrek.projekt4bt.R;
import igrek.projekt4bt.events.ResizedEvent;
import igrek.projekt4bt.graphics.gui.GUI;
import igrek.projekt4bt.logger.Logs;
import igrek.projekt4bt.logic.controller.AppController;
import igrek.projekt4bt.logic.controller.dispatcher.AbstractEvent;
import igrek.projekt4bt.logic.controller.dispatcher.IEventConsumer;
import igrek.projekt4bt.logic.controller.dispatcher.IEventObserver;

public class App extends BaseApp implements IEventObserver {
	
	private GUI gui;
	
	private AppState state;
	
	public App(AppCompatActivity activity) {
		super(activity);
		
		registerServices();
		registerEvents();
		
		gui = new GUI(activity);
		state = AppState.NOT_CONNECTED;
		
		Logs.info("Aplikacja uruchomiona.");
	}
	
	public void registerServices() {
		
	}
	
	@Override
	public void registerEvents() {
		AppController.registerEventObserver(ResizedEvent.class, this);
	}
	
	@Override
	public void quit() {
		super.quit();
	}
	
	@Override
	public boolean optionsSelect(int id) {
		if (id == R.id.action_minimize) {
			minimize();
			return true;
		} else if (id == R.id.action_exit) {
			quit();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onKeyBack() {
		return true;
	}
	
	@Override
	public void menuInit(Menu menu) {
		super.menuInit(menu);
	}
	
	@Override
	public void onEvent(AbstractEvent event) {
		
		event.bind(ResizedEvent.class, new IEventConsumer<ResizedEvent>() {
			@Override
			public void accept(ResizedEvent e) {
				Logs.debug("Rozmiar grafiki 2D zmieniony: " + e.getW() + " x " + e.getH());
			}
		});
		
	}
}

