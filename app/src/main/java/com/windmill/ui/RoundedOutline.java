package com.windmill.ui;
import android.util.SparseIntArray;
import android.view.*;
import android.graphics.*;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import java.util.HashMap;

public final class RoundedOutline extends ViewOutlineProvider {
	private static final HashMap<Integer, RoundedOutline> cache = new HashMap<>();
	//public static final ViewOutlineProvider instance = new RoundedOutline();
	public static final int RADIUS_FOLLOW_VIEW_HEIGHT = -1;
	public static final int RADIUS_FOLLOW_VIEW_WIDTH = -2;
	private final float radius;

	/*public RoundedOutline(){
		radius = RADIUS_FOLLOW_VIEW_HEIGHT;
	}*/

	private RoundedOutline(float radius){
		this.radius = radius;
	}

	@NonNull
	public static RoundedOutline get(float radius){
		Integer key = (int) radius;
		RoundedOutline outline = cache.get(key);
		if (outline == null) {
			cache.put(key, outline = new RoundedOutline(radius));
		}
		return outline;
	}

	@Override
	public void getOutline(@NonNull View view, @NonNull Outline outline) {
		int height = view.getHeight();
		int width = view.getWidth();
		float radius = this.radius;
		int toDivide = 0;
		if (radius == RADIUS_FOLLOW_VIEW_HEIGHT) {
			toDivide = height;
		} else if (radius == RADIUS_FOLLOW_VIEW_WIDTH) {
			toDivide = width;
		}
		if (toDivide != 0){
			radius = toDivide >> 1;
		}
		outline.setRoundRect(0, 0, view.getWidth(), height, radius);
	}

}
