package com.katbutler.encore.util;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    boolean cropWithCircle = false;
    
    public DownloadImageTask(ImageView bmImage, boolean cropWithCircle) {
        this.bmImage = bmImage;
        this.cropWithCircle = cropWithCircle;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = new URL(urldisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
            
            if (cropWithCircle)
            	return ImageUtils.createCroppedCircledBitmap(bitmap);
            else 
            	return bitmap;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
    	if (result != null)
    		bmImage.setImageBitmap(result);
    }
}