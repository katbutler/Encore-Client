package com.katbutler.encore.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class ImageUtils {

	/**
	 * Take a Bitmap and crop it with a circle
	 * 
	 * @param bitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap createCroppedCircledBitmap(Bitmap bitmap) {
		Bitmap finalPic;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			finalPic = Bitmap.createBitmap(bitmap.getHeight(),
					bitmap.getHeight(), Config.ARGB_8888);
		} else {
			finalPic = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getWidth(), Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(finalPic);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		float r = 0;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			r = bitmap.getHeight() / 2;
		} else {
			r = bitmap.getWidth() / 2;
		}

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(r, r, r, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return finalPic;
	}

	public static int dpToPx(int dp, DisplayMetrics displayMetrics) {
		// DisplayMetrics displayMetrics =
		// getContext().getResources().getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public static int pxToDp(int px, DisplayMetrics displayMetrics) {
		// DisplayMetrics displayMetrics =
		// getContext().getResources().getDisplayMetrics();
		int dp = Math.round(px
				/ (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

}
