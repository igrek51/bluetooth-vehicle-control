package igrek.projekt4bt.graphics.canvas;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import igrek.projekt4bt.logic.controller.dispatcher.AbstractEvent;
import igrek.projekt4bt.logic.controller.dispatcher.IEventObserver;

public class QuickMenu implements IEventObserver {
	
	
	private View quickMenuView;
	
	private TextView tvTransposition;
	private Button btnTranspose0;
	
	public QuickMenu() {
		registerEvents();
	}
	
	@Override
	public void registerEvents() {
		
	}
	
	public void setQuickMenuView(View quickMenuView) {
		this.quickMenuView = quickMenuView;
		
		//		tvTransposition = (TextView) quickMenuView.findViewById(R.id.tvTransposition);
		//
		//		btnTranspose0 = (Button) quickMenuView.findViewById(R.id.btnTranspose0);
		//		btnTranspose0.setOnClickListener(new View.OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				Logs.info("click 0");
		//			}
		//		});
		
	}
	
	public void draw() {
		
		//		//dimmed background
		//		float w = canvas.getW();
		//		float h = canvas.getH();
		//
		//		canvas.setColor(0x000000, 130);
		//		canvas.fillRect(0, 0, w, h);
	}
	
	public boolean onScreenClicked(float x, float y) {
		
		return true;
	}
	
	
	@Override
	public void onEvent(AbstractEvent event) {
		
	}
}