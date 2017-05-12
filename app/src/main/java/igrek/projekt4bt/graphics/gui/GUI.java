package igrek.projekt4bt.graphics.gui;


import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import igrek.projekt4bt.R;
import igrek.projekt4bt.graphics.canvas.CanvasGraphics;

public class GUI extends GUIBase {
	
	private CanvasGraphics canvas = null;
	
	public GUI(AppCompatActivity activity) {
		super(activity);
	}
	
	public void showControlPanel() {
		
		setFullscreen(true);
		
		activity.setContentView(R.layout.control_view);
		
		canvas = new CanvasGraphics(activity);

		FrameLayout mainFrame = (FrameLayout) activity.findViewById(R.id.mainFrame);

		mainFrame.removeAllViews();

		mainFrame.addView(canvas);
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View menuView = inflater.inflate(R.layout.control_menu, null);
		menuView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mainFrame.addView(menuView);

		canvas.setControlMenuView(menuView);
	}
	
}

