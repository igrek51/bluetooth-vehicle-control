package igrek.projekt4bt.logic;


import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import igrek.projekt4bt.R;
import igrek.projekt4bt.bluetooth.BTAdapter;
import igrek.projekt4bt.dispatcher.AbstractEvent;
import igrek.projekt4bt.dispatcher.EventDispatcher;
import igrek.projekt4bt.dispatcher.IEventConsumer;
import igrek.projekt4bt.dispatcher.IEventObserver;
import igrek.projekt4bt.events.ClearButtonEvent;
import igrek.projekt4bt.events.ConnectButtonEvent;
import igrek.projekt4bt.events.DisconnectButtonEvent;
import igrek.projekt4bt.events.ReloadButtonEvent;
import igrek.projekt4bt.events.ResizedEvent;
import igrek.projekt4bt.events.ShootButtonEvent;
import igrek.projekt4bt.events.ShowInfoEvent;
import igrek.projekt4bt.events.StatusButtonEvent;
import igrek.projekt4bt.events.TestButtonEvent;
import igrek.projekt4bt.graphics.canvas.CanvasGraphics;
import igrek.projekt4bt.graphics.canvas.InfoMessage;
import igrek.projekt4bt.logger.Logs;

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
		
		showInfo("Aplikacja uruchomiona.");
		
		bindButtons();
	}
	
	private void showInfo(String message) {
		Logs.info(message);
		EventDispatcher.sendEvent(new ShowInfoEvent(message, InfoMessage.ShowInfoType.OK));
	}
	
	private void bindButtons() {
		activity.findViewById(R.id.buttonConnect).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new ConnectButtonEvent());
			}
		});
		activity.findViewById(R.id.buttonDisconnect).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new DisconnectButtonEvent());
			}
		});
		activity.findViewById(R.id.buttonReload).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new ReloadButtonEvent());
			}
		});
		activity.findViewById(R.id.buttonShoot).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new ShootButtonEvent());
			}
		});
		activity.findViewById(R.id.buttonStatus).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new StatusButtonEvent());
			}
		});
		activity.findViewById(R.id.buttonTest).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new TestButtonEvent());
			}
		});
		activity.findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventDispatcher.sendEvent(new ClearButtonEvent());
			}
		});
	}
	
	@Override
	public void registerEvents() {
		EventDispatcher.registerEventObserver(ResizedEvent.class, this);
	}
	
	@Override
	public void quit() {
		bt.disconnect();
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
	
	private void setFullscreen(boolean full) {
		int fullscreen_flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
		if (full) {
			activity.getWindow().setFlags(fullscreen_flag, fullscreen_flag);
		} else {
			activity.getWindow().clearFlags(fullscreen_flag);
		}
	}
}

