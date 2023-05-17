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
    private int width;
    private int height;

    private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

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
            // 이미 url을 통해 불러온 적이 있다면 이전 bitmap을 삭제
            if(bitmapHash.containsKey(urlStr)) {
                Bitmap oldBitmap = bitmapHash.remove(urlStr);
            }

            URL url = new URL(urlStr);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

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


        if(bitmap!=null){
            int bitmap_width  = bitmap.getWidth();
            int bitmap_height = bitmap.getHeight();

            int resize_size = 1000;
            if(bitmap_width!=0 && bitmap_height!=0){
                if(bitmap_width >= bitmap_height){
                    int ratio = (bitmap_height*resize_size)/bitmap_width;
                    bitmap = Bitmap.createScaledBitmap(bitmap, resize_size, ratio, true);
                }
                //사진의 세로길이가 더 길면
                else{
                    int ratio = (bitmap_width*resize_size)/bitmap_height;
                    bitmap = Bitmap.createScaledBitmap(bitmap, ratio, resize_size , true);
                }
            }

            imageView.setPadding(10,10,10,10);
            imageView.setClipToOutline(true);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }

    }


}
