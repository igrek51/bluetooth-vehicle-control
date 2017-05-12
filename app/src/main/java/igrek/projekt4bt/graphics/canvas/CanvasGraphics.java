package igrek.projekt4bt.graphics.canvas;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import igrek.projekt4bt.events.GraphicsInitializedEvent;
import igrek.projekt4bt.graphics.Colors;
import igrek.projekt4bt.graphics.canvas.enums.Font;
import igrek.projekt4bt.logic.controller.AppController;
import igrek.projekt4bt.logic.controller.services.IService;

public class CanvasGraphics extends BaseCanvasGraphics implements IService {
	
	private QuickMenu quickMenu;
	
	public CanvasGraphics(Context context) {
		super(context);
		AppController.registerService(this);
	}
	
	@Override
	public void reset() {
		super.reset();
		quickMenu = new QuickMenu();
	}
	
	@Override
	public void init() {
		setFont(Font.FONT_NORMAL);
		AppController.sendEvent(new GraphicsInitializedEvent(w, h, paint));
	}
	
	@Override
	public void onRepaint() {
		
		drawBackground();
		
		quickMenu.draw();
	}
	
	private void drawBackground() {
		setColor(Colors.background);
		clearScreen();
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
	
	//	private boolean onScreenClicked(float x, float y) {
	//
	//		if (quickMenu.isVisible()) {
	//
	//			return quickMenu.onScreenClicked(x, y);
	//
	//		} else {
	//
	//			Autoscroll autoscroll = AppController.getService(Autoscroll.class);
	//			if (autoscroll.isRunning()) {
	//				AppController.sendEvent(new AutoscrollStopUIEvent());
	//			} else {
	//
	//				if (y >= h * GESTURE_AUTOSCROLL_BOTTOM_REGION) {  //kliknięcie na dole ekranu
	//
	//					AppController.sendEvent(new AutoscrollStartUIEvent());
	//
	//				} else {
	//
	//					quickMenu.setVisible(true);
	//
	//				}
	//			}
	//			return true;
	//		}
	//	}
	
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
	
	public void setQuickMenuView(View quickMenuView) {
		quickMenu.setQuickMenuView(quickMenuView);
	}
}
