package com.serega.bitmapperspectivetransformation.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.serega.bitmapperspectivetransformation.R;

import java.util.ArrayList;
import java.util.List;

public class PerspectiveView extends View {
	/**
	 * Original Image to transform
	 */
	private Bitmap testImage;
	private final Matrix matrix = new Matrix();
	private final Paint imagePaint = new Paint();
	private final Paint pointPaint = new Paint();

	private final List<TouchPoint> points = new ArrayList<>(4);

	private final float[] src = new float[8];
	private final float[] dst = new float[8];


	public PerspectiveView(Context context) {
		super(context);
		init();
	}

	public PerspectiveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PerspectiveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(21)
	public PerspectiveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		testImage = BitmapFactory.decodeResource(getResources(), R.mipmap.image_wall);
		pointPaint.setColor(Color.RED);
		pointPaint.setAntiAlias(true);

		setOnTouchListener(new OnTouchListener() {
			private float touchX;
			private float touchY;
			private static final int NOT_FOUND = -1;
			private int movePointIndex = NOT_FOUND;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						touchX = event.getX();
						touchY = event.getY();
						for (int i = 0; i < points.size(); i++) {
							if(points.get(i).contains((int)touchX, (int)touchY)){
								movePointIndex = i;
							}
						}
						return true;

					case MotionEvent.ACTION_MOVE:
						if(movePointIndex != NOT_FOUND) {
							touchX = event.getX();
							touchY = event.getY();
							points.get(movePointIndex).set(touchX, touchY);
							prepareNewMatrix();

							invalidate();
						}

						return true;

					case MotionEvent.ACTION_UP:
						movePointIndex = NOT_FOUND;
						return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		setInitialState(w, h);
	}

	private void setInitialState(float w, float h){
		//Top Left point
		src[0] = 0;
		src[1] = 0;

		//Top Right point
		src[2] = testImage.getWidth();
		src[3] = 0;

		//Bottom Left point
		src[4] = 0;
		src[5] = testImage.getHeight();

		//Bottom Right point
		src[6] = testImage.getWidth();
		src[7] = testImage.getHeight();

		float left = w / 2 - testImage.getWidth() / 3;
		float top = h / 2 - testImage.getHeight() / 3;
		float right = w / 2 + testImage.getWidth() / 3;
		float bottom = h / 2 + testImage.getHeight() / 3;

		dst[0] = left;
		dst[1] = top;
		dst[2] = right;
		dst[3] = top;
		dst[4] = left;
		dst[5] = bottom;
		dst[6] = right;
		dst[7] = bottom;

		matrix.setPolyToPoly(src, 0, dst, 0, src.length / 2);

		points.add(new TouchPoint(left, top));
		points.add(new TouchPoint(right, top));
		points.add(new TouchPoint(left, bottom));
		points.add(new TouchPoint(right, bottom));
	}

	private void prepareNewMatrix(){
		TouchPoint leftTop = points.get(0);
		TouchPoint rightTop = points.get(1);
		TouchPoint leftBottom = points.get(2);
		TouchPoint rightBottom = points.get(3);

		dst[0] = leftTop.getX();
		dst[1] = leftTop.getY();
		dst[2] = rightTop.getX();
		dst[3] = rightTop.getY();
		dst[4] = leftBottom.getX();
		dst[5] = leftBottom.getY();
		dst[6] = rightBottom.getX();
		dst[7] = rightBottom.getY();

		matrix.setPolyToPoly(src, 0, dst, 0, src.length / 2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.drawColor(Color.LTGRAY);
		canvas.concat(matrix);
		canvas.drawBitmap(testImage, 0, 0, imagePaint);
		canvas.restore();

		for(TouchPoint t : points){
			canvas.drawCircle(t.getX(), t.getY(), TouchPoint.RADIUS, pointPaint);
		}

	}
}
