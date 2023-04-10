package com.example.medi_nion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.URL;
import java.util.HashMap;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String urlStr;
    private ImageView imageView;

    public static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

    public ImageLoadTask(String urlStr, ImageView imageView){
        this.urlStr = urlStr;
        this.imageView = imageView;
    }

    public ImageLoadTask(String urlStr){
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;

        try {
            Log.d("taks??", "i'm here 4");
            // 이미 url을 통해 불러온 적이 있다면 이전 bitmap을 삭제
            if(bitmapHash.containsKey(urlStr)) {
                Bitmap oldBitmap = bitmapHash.remove(urlStr);
                if(oldBitmap != null){
                    oldBitmap.recycle();
                    oldBitmap = null;
                }
            }

            URL url = new URL(urlStr);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            Log.d("taks...:)", bitmapHash.keySet().toString());
            bitmapHash.put(urlStr, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        Log.d("imageView", "set!");
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();


    }

}
