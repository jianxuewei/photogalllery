package com.xxk.photogalllery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by xxk on 15/11/29.
 */
public class Fragment_PhotoGallery extends Fragment {
    private GridView mGridView;
    private TextView mTextView;
    private ImageView mImageView;
    private ArrayList<Image> mImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
//        mGridView= (GridView) v.findViewById(R.id.gridview);
        View v = inflater.inflate(R.layout.temp, container, false);
        mTextView = (TextView) v.findViewById(R.id.tv);
        mImageView= (ImageView) v.findViewById(R.id.imageview);
        new BaiduConnectAsyncTask().execute("http://image.baidu.com/data/imgs?col=美女&tag=诱惑&sort=0&pn=10&rn=10&p=channel&from=1");
        return v;
    }
    private class BaiduConnectAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {


            try {
                return new FlickrFetchr().getUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<Image> items=new ArrayList<>();
            JSONArray imageJsonArray = null;
            StringBuilder sb=new StringBuilder();
            try {
                imageJsonArray = new JSONObject(result).getJSONArray("imgs");
                for (int i = 0; i < imageJsonArray.length(); i++) {


                    JSONObject object = ((JSONObject) imageJsonArray.get(i));
                    if (object != null) {
                        String id = object.getString("id");
                        String desc = object.getString("desc");
                        String downloadUrl = object.getString("downloadUrl");
                        String thumbnailUrl = object.getString("thumbnailUrl");
                        Image image = new Image();
                        image.setId(id);

                        image.setDesc(desc);
                        image.setThumbnailUrl(thumbnailUrl);
                        image.setDownloadUrl(downloadUrl);
                        items.add(image);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mImages=items;
            for (int i = 0; i < mImages.size(); i++) {
                Image item=mImages.get(i);

                String str=item.getId()+":"+item.toString()+","+"\n";
                sb.append(str);
            }

            mTextView.setText(sb.toString());
            new GetBitmapAsynctask().execute(mImages.get(4).getDownloadUrl());
        }
    }
    private class GetBitmapAsynctask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                byte[] bitmap=new FlickrFetchr().getUrlBytes(params[0]);
                return BitmapFactory.decodeByteArray(bitmap,0,bitmap.length);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

}
