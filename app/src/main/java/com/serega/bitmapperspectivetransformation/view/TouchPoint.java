package com.serega.bitmapperspectivetransformation.view;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class TouchPoint {
	private float x;
	private float y;
	private final Region region;

	public static final float RADIUS = 25;

	TouchPoint(float x, float y) {
		this.x = x;
		this.y = y;

		region = new Region();
		Path p = new Path();
		p.addCircle(x, y, RADIUS, Path.Direction.CCW);
		RectF r = new RectF();
		p.computeBounds(r, true);
		region.setPath(p, new Region(new Rect((int) r.left, (int) r.top, (int) r.right, (int) r.bottom)));
	}

	boolean contains(int x, int y) {
		return region.contains(x, y);
	}

	void set(float x, float y){
		this.x = x;
		this.y = y;

		Path p = new Path();
		p.addCircle(x, y, RADIUS, Path.Direction.CCW);
		RectF r = new RectF();
		p.computeBounds(r, true);
		region.setPath(p, new Region(new Rect((int) r.left, (int) r.top, (int) r.right, (int) r.bottom)));
	}

	float getX(){
		return x;
	}

	float getY(){
		return y;
	}


}
