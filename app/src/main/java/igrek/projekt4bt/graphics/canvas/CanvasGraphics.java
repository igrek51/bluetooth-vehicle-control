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
import igrek.projekt4bt.events.ShowInfoEvent;
import igrek.projekt4bt.graphics.canvas.enums.Align;
import igrek.projekt4bt.graphics.canvas.enums.Font;

public class CanvasGraphics extends BaseCanvasGraphics implements IEventObserver {
	
	private List<String> infos = new ArrayList<>();
	private final int INFO_FONT_SIZE = 17;
	
	private final float CONTROL_MAP_WIDTH = 0.7f;
	private final float CONTROL_DEAD_ZONE_H = 0.15f;
	private final float CONTROL_MAX_RANGE_H = 0.1f;
	
	private Context context;
	
	public CanvasGraphics(Context context) {
		super(context);
		this.context = context;
		registerEvents();
	}
	
	@Override
	public void registerEvents() {
		EventDispatcher.registerEventObserver(ShowInfoEvent.class, this);
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
		
		y = (CONTROL_MAX_RANGE_H + (1 - CONTROL_MAX_RANGE_H * 2 - CONTROL_DEAD_ZONE_H) / 4) * h;
		drawText("FORWARD (PWM)", w * (1f - CONTROL_MAP_WIDTH / 2), y, Align.CENTER);
		y = (CONTROL_MAX_RANGE_H + CONTROL_DEAD_ZONE_H + (1 - CONTROL_MAX_RANGE_H * 2 - CONTROL_DEAD_ZONE_H) / 2 + (1 - CONTROL_MAX_RANGE_H * 2 - CONTROL_DEAD_ZONE_H) / 4) * h;
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
	}
	
	@Override
	protected void onTouchMove(MotionEvent event) {
		
		//		if (event.getPointerCount() >= 2) {
		//
		//			if (pointersDst0 != null) {
		//				Float pointersDst1 = (float) Math.hypot(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
		//				float scale = (pointersDst1 / pointersDst0 - 1) * FONTSIZE_SCALE_FACTOR + 1;
		//				float fontsize1 = fontsize0 * scale;
		//				previewFontsize(fontsize1);
		//			}
		//
		//		} else {
		//
		//			scroll = startScroll + startTouchY - event.getY();
		//			float maxScroll = getMaxScroll();
		//			if (scroll < 0) scroll = 0; //za duże przeskrolowanie w górę
		//			if (scroll > maxScroll) scroll = maxScroll; // za duże przescrollowanie w dół
		//			repaint();
		//
		//		}
	}
	
	@Override
	protected void onTouchUp(MotionEvent event) {
		
		float deltaX = event.getX() - startTouchX;
		float deltaY = event.getY() - startTouchY;
		// monitorowanie zmiany przewijania
		float dScroll = -deltaY;
		//		if (Math.abs(dScroll) > MIN_SCROLL_EVENT) {
		//			AppController.sendEvent(new CanvasScrollEvent(dScroll, scroll));
		//		}
		//
		//		//włączenie autoscrolla - szybkie kliknięcie na dole
		//		float hypot = (float) Math.hypot(deltaX, deltaY);
		//		if (hypot <= GESTURE_CLICK_MAX_HYPOT) { //kliknięcie w jednym miejscu
		//			if (System.currentTimeMillis() - startTouchTime <= GESTURE_CLICK_MAX_TIME) { //szybkie kliknięcie
		//				if (onScreenClicked(event.getX(), event.getY())) {
		//					repaint();
		//				}
		//			}
		//		}
	}
	
	//	@Override
	//	protected void onTouchPointerUp(MotionEvent event) {
	//		AppController.sendEvent(new FontsizeChangedEvent(fontsize));
	//		pointersDst0 = null; //reset poczatkowej długości
	//		// reset na brak przewijania
	//		startScroll = scroll;
	//
	//		//pozostawienie pointera, który jest jeszcze aktywny
	//		Integer pointerIndex = 0;
	//		if (event.getPointerCount() >= 2) {
	//			for (int i = 0; i < event.getPointerCount(); i++) {
	//				if (i != event.getActionIndex()) {
	//					pointerIndex = i;
	//					break;
	//				}
	//			}
	//		}
	//		startTouchY = event.getY(pointerIndex);
	//	}
	
	//	@Override
	//	protected void onTouchPointerDown(MotionEvent event) {
	//		pointersDst0 = (float) Math.hypot(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
	//		fontsize0 = fontsize;
	//	}
	
}
