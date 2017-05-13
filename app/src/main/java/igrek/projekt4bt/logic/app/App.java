package igrek.projekt4bt.logic.app;


import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.FrameLayout;

import igrek.projekt4bt.R;
import igrek.projekt4bt.bluetooth.BTAdapter;
import igrek.projekt4bt.events.ResizedEvent;
import igrek.projekt4bt.graphics.canvas.CanvasGraphics;
import igrek.projekt4bt.logger.Logs;
import igrek.projekt4bt.logic.controller.AppController;
import igrek.projekt4bt.logic.controller.dispatcher.AbstractEvent;
import igrek.projekt4bt.logic.controller.dispatcher.IEventConsumer;
import igrek.projekt4bt.logic.controller.dispatcher.IEventObserver;

public class App extends BaseApp implements IEventObserver {
	
	private BTAdapter bt;
	
	private CanvasGraphics canvas;
	
	public App(AppCompatActivity activity) {
		super(activity);
		
		registerEvents();
		
		setFullscreen(true);
		
		activity.setContentView(R.layout.control_view);
		
		canvas = new CanvasGraphics(activity);
		
		FrameLayout mainFrame = (FrameLayout) activity.findViewById(R.id.mainFrame);
		mainFrame.removeAllViews();
		mainFrame.addView(canvas);
		
		keepScreenOn(activity);
		
		bt = new BTAdapter();
		
		Logs.info("Aplikacja uruchomiona.");
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
	
	protected void setFullscreen(boolean full) {
		int fullscreen_flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
		if (full) {
			activity.getWindow().setFlags(fullscreen_flag, fullscreen_flag);
		} else {
			activity.getWindow().clearFlags(fullscreen_flag);
		}
	}
}

