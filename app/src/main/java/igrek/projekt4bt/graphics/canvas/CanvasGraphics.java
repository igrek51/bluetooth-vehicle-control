package igrek.projekt4bt.graphics.canvas;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import igrek.projekt4bt.R;
import igrek.projekt4bt.dispatcher.AbstractEvent;
import igrek.projekt4bt.dispatcher.EventDispatcher;
import igrek.projekt4bt.dispatcher.IEventConsumer;
import igrek.projekt4bt.dispatcher.IEventObserver;
import igrek.projekt4bt.events.ClearButtonEvent;
import igrek.projekt4bt.events.ControlMotorsEvent;
import igrek.projekt4bt.events.ShowInfoEvent;
import igrek.projekt4bt.graphics.canvas.enums.Align;
import igrek.projekt4bt.graphics.canvas.enums.Font;
import igrek.projekt4bt.logic.ControlCommand;

public class CanvasGraphics extends BaseCanvasGraphics implements IEventObserver {
	
	private List<String> infos = new ArrayList<>();
	private final int INFO_FONT_SIZE = 17;
	/**
	 * minimalny odstęp w ms między kolejnymi komunikatami zmiany sterowania
	 */
	private final int CONTROL_COMMANDS_INTERVAL = 300;
	
	private final float CONTROL_MAP_WIDTH = 0.7f;
	private final float CONTROL_DEAD_ZONE_H = 0.15f;
	private final float CONTROL_MAX_RANGE_H = 0.1f;
	private final float CONTROL_PWM_H = (1f - CONTROL_DEAD_ZONE_H - 2 * CONTROL_MAX_RANGE_H) / 2;
	
	private Context context;
	
	private long lastCommandTime = 0;
	private ControlCommand lastCommand = null;
	
	public CanvasGraphics(Context context) {
		super(context);
		this.context = context;
		registerEvents();
	}
	
	@Override
	public void registerEvents() {
		EventDispatcher.registerEventObserver(ShowInfoEvent.class, this);
		EventDispatcher.registerEventObserver(ClearButtonEvent.class, this);
	}
	
	@Override
	public void onEvent(AbstractEvent event) {
		event.bind(ShowInfoEvent.class, new IEventConsumer<ShowInfoEvent>() {
			@Override
			public void accept(ShowInfoEvent e) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
				infos.add(dateFormat.format(new Date()) + e.getMessage());
				clearOldInfos();
				invalidate();
			}
		});
		event.bind(ClearButtonEvent.class, new IEventConsumer<ClearButtonEvent>() {
			@Override
			public void accept(ClearButtonEvent e) {
				infos.clear();
				invalidate();
			}
		});
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	@Override
	public void init() {
		//		EventDispatcher.sendEvent(new GraphicsInitializedEvent(w, h, paint));
	}
	
	private int resourceColor(int colorId) {
		return ContextCompat.getColor(context, colorId);
	}
	
	@Override
	public void onRepaint() {
		
		drawBackground();
		
		drawControlMap();
		
		drawInfos();
	}
	
	private void drawBackground() {
		setColor(resourceColor(R.color.background));
		clearScreen();
	}
	
	private void drawInfos() {
		int y = 5;
		setFont(Font.FONT_NORMAL);
		setFontSize(INFO_FONT_SIZE);
		setColor(resourceColor(R.color.infoText));
		for (String info : infos) {
			drawText(info, 0, y, Align.TOP_LEFT);
			y += INFO_FONT_SIZE;
		}
	}
	
	private void drawControlMap() {
		
		setColor(resourceColor(R.color.controlArea));
		fillRect(w * (1f - CONTROL_MAP_WIDTH), 0, w, h);
		
		// granice obszarów
		setColor(resourceColor(R.color.outlineControlArea));
		//obwódka
		outlineRect(w * (1f - CONTROL_MAP_WIDTH), 0, w, h, 3);
		//podział na 3 części
		float x = w * (1f - CONTROL_MAP_WIDTH + CONTROL_MAP_WIDTH / 3);
		drawLine(x, 0, x, h);
		x = w * (1f - CONTROL_MAP_WIDTH + CONTROL_MAP_WIDTH * 2 / 3);
		drawLine(x, 0, x, h);
		//dead zone
		float y = h / 2 - CONTROL_DEAD_ZONE_H * h / 2;
		drawLine(w * (1f - CONTROL_MAP_WIDTH), y, w, y);
		y = h / 2 + CONTROL_DEAD_ZONE_H * h / 2;
		drawLine(w * (1f - CONTROL_MAP_WIDTH), y, w, y);
		// max speed (100 % pwm)
		y = h * CONTROL_MAX_RANGE_H;
		drawLine(w * (1f - CONTROL_MAP_WIDTH), y, w, y);
		y = h - h * CONTROL_MAX_RANGE_H;
		drawLine(w * (1f - CONTROL_MAP_WIDTH), y, w, y);
		
		// podpisy
		setFont(Font.FONT_NORMAL);
		setFontSize(INFO_FONT_SIZE);
		
		drawText("Dead zone (0%)", w * (1f - CONTROL_MAP_WIDTH / 2), h / 2, Align.CENTER);
		
		drawText("100%", w * (1f - CONTROL_MAP_WIDTH / 2), h * CONTROL_MAX_RANGE_H / 2, Align.CENTER);
		drawText("100%", w * (1f - CONTROL_MAP_WIDTH / 2), h - h * CONTROL_MAX_RANGE_H / 2, Align.CENTER);
		
		drawText("LEFT (0%)", w * (1f - CONTROL_MAP_WIDTH * 5 / 6), h / 2, Align.CENTER);
		drawText("RIGHT (0%)", w * (1f - CONTROL_MAP_WIDTH * 1 / 6), h / 2, Align.CENTER);
		
		drawText("LEFT (100%)", w * (1f - CONTROL_MAP_WIDTH * 5 / 6), h * CONTROL_MAX_RANGE_H / 2, Align.CENTER);
		drawText("LEFT (100%)", w * (1f - CONTROL_MAP_WIDTH * 5 / 6), h - h * CONTROL_MAX_RANGE_H / 2, Align.CENTER);
		
		drawText("RIGHT (100%)", w * (1f - CONTROL_MAP_WIDTH * 1 / 6), h * CONTROL_MAX_RANGE_H / 2, Align.CENTER);
		drawText("RIGHT (100%)", w * (1f - CONTROL_MAP_WIDTH * 1 / 6), h - h * CONTROL_MAX_RANGE_H / 2, Align.CENTER);
		
		y = (CONTROL_MAX_RANGE_H + (1f - CONTROL_MAX_RANGE_H * 2 - CONTROL_DEAD_ZONE_H) / 4) * h;
		drawText("FORWARD (PWM)", w * (1f - CONTROL_MAP_WIDTH / 2), y, Align.CENTER);
		y = (CONTROL_MAX_RANGE_H + CONTROL_DEAD_ZONE_H + (1f - CONTROL_MAX_RANGE_H * 2 - CONTROL_DEAD_ZONE_H) / 2 + (1f - CONTROL_MAX_RANGE_H * 2 - CONTROL_DEAD_ZONE_H) / 4) * h;
		drawText("BACKWARD (PWM)", w * (1f - CONTROL_MAP_WIDTH / 2), y, Align.CENTER);
	}
	
	private void clearOldInfos() {
		if (h == 0)
			return;
		int maxInfos = h / INFO_FONT_SIZE;
		while (infos.size() > maxInfos) {
			infos.remove(0);
		}
	}
	
	@Override
	protected void onTouchDown(MotionEvent event) {
		super.onTouchDown(event);
		touchChanged(event.getX(), event.getY());
	}
	
	@Override
	protected void onTouchMove(MotionEvent event) {
		touchChanged(event.getX(), event.getY());
	}
	
	@Override
	protected void onTouchUp(MotionEvent event) {
		touchReset();
	}
	
	private void touchChanged(float x, float y) {
		float MIN_X = w * (1f - CONTROL_MAP_WIDTH);
		if (x >= MIN_X) {
			touchControlChanged((x - MIN_X) / (CONTROL_MAP_WIDTH * w), y / h);
		}
	}
	
	private void touchControlChanged(float rx, float ry) {
		// rozpoznanie kierunku skręcania (lewo, prawo, brak)
		int yaw = 0;
		if (rx < 1f / 3) {
			yaw = -1; // lewo
		} else if (rx > 2f / 3) {
			yaw = +1; // prawo
		}
		
		// kierunek jazdy przód, tył, brak
		int throttle = 0;
		if (ry < CONTROL_MAX_RANGE_H + CONTROL_PWM_H) {
			throttle = 1; // do przodu
		} else if (ry > 1f - CONTROL_MAX_RANGE_H - CONTROL_PWM_H) {
			throttle = -1; // do tyłu
		}
		
		// moc - prędkość [0 - 1]
		float power = 0;
		if (throttle != 0) {
			float ry2 = ry;
			if (throttle == -1) {
				ry2 = 1f - ry;
			}
			if (ry2 <= CONTROL_MAX_RANGE_H) {
				power = 1f; // 100 %
			} else {
				power = 1f - (ry2 - CONTROL_MAX_RANGE_H) / CONTROL_PWM_H;
			}
		}
		
		controlParametersChanged(new ControlCommand(yaw, throttle, power));
	}
	
	private void controlParametersChanged(ControlCommand newCommand) {
		
		// wyślij, jeśli to pierwsza komenda, zmieniła się znacząco od poprzedniej lub gdy upłynął czas od wysłania ostatniej komendy
		if (lastCommand == null || (!lastCommand.equals(newCommand) && (lastCommand.changedStrongly(newCommand) || System
				.currentTimeMillis() > lastCommandTime + CONTROL_COMMANDS_INTERVAL))) {
			
			//			Logs.debug("yaw: " + newCommand.getYaw() + ", throttle: " + newCommand.getThrottle() + ", power: " + newCommand
			//					.getPower());
			
			lastCommand = newCommand;
			lastCommandTime = System.currentTimeMillis();
			
			EventDispatcher.sendEvent(new ControlMotorsEvent(newCommand));
		}
		
		
	}
	
	private void touchReset() {
		EventDispatcher.sendEvent(new ControlMotorsEvent(new ControlCommand(0, 0, 0f)));
	}
	
}
