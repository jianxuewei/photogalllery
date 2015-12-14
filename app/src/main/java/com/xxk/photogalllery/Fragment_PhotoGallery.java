package com.xxk.photogalllery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by xxk on 15/11/29.
 */
public class Fragment_PhotoGallery extends Fragment {
    private static final String TAG="Fragment_PhotoGallery";
    private GridView mGridView;
    private Button mButton;
    private int mPage=0;
    //private ImageView mImageView;
    private ArrayList<Image> mImages = new ArrayList<>();
    ThumbnailDownloader<ImageView> mThumbnailDownloader;
    private ArrayList<Bitmap> mBitmaps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mThumbnailDownloader=new ThumbnailDownloader<>(new Handler());
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        mThumbnailDownloader.setOnListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
        Log.i(TAG,"BACKGROUND thread started");

        PollyService.setAlarmService(getActivity(),true);

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mGridView= (GridView) v.findViewById(R.id.gridview);
        mButton= (Button) v.findViewById(R.id.button_next_page);

        mPage=0;
        new BaiduConnectAsyncTask().execute(makeUrl(mPage));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BaiduConnectAsyncTask().execute(makeUrl(++mPage));
            }
        });
        return v;
    }
    String makeUrl(int page){
        String url="http://image.baidu.com/data/imgs?col=美女&tag=诱惑&sort=0&pn="+(page*26+1)+"&rn=26&p=channel&from=1";
        return url;
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
            JSONArray imageJsonArray;
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
                Log.d("baidu",str);
                sb.append(str);
            }

            //mTextView.setText(sb.toString());
            ArrayList<String> listThumbnailUrl=new ArrayList<>();
            for (int i = 0; i < mImages.size(); i++) {
                listThumbnailUrl.add(mImages.get(i).getThumbnailUrl());
            }
            new GetBitmapAsynctask().execute(listThumbnailUrl);
        }
    }
    private class GetBitmapAsynctask extends AsyncTask<ArrayList<String>,Void,ArrayList<Bitmap>>{

        @Override
        protected ArrayList<Bitmap>  doInBackground(ArrayList<String>... params) {
            ArrayList<Bitmap> bitmaps=new ArrayList<>();
            try {
                for (int i = 0; i < params[0].size(); i++) {
                    byte[] bitmap=new FlickrFetchr().getUrlBytes(params[0].get(i));
                    //return BitmapFactory.decodeByteArray(bitmap,0,bitmap.length);
                    bitmaps.add(BitmapFactory.decodeByteArray(bitmap,0,bitmap.length));
                }
                return bitmaps;


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {


            if(bitmaps!=null){
                mBitmaps=bitmaps;
                ArrayList<String> list=new ArrayList<>();
                //ArrayList<Bitmap> bitmaps=new ArrayList<>();
                for (int i = 0; i < mImages.size(); i++) {
                    String str=mImages.get(i).getDesc();
                    list.add(str);
                    //bitmaps.add(bitmap);
                }
                //mImageView.setImageBitmap(bitmap);
                //mGridView.setAdapter(new GalleryAdapter(bitmaps));
                mGridView.setAdapter(new GalleryAdapter(mImages));
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Image item=mImages.get(position);

                        Toast.makeText(getActivity(),item.toString(),Toast.LENGTH_SHORT).show();
                        Uri photoPageUri= Uri.parse(item.getDownloadUrl());
                        //Intent i=new Intent(Intent.ACTION_VIEW,photoPageUri);
                        //Uri photoPageUri=Uri.parse("http://www.baidu.com");
                        Intent i =new Intent(getActivity(),Activity_Photopage.class);
                        i.setData(photoPageUri);

                        startActivity(i);

                    }
                });
                //mGridView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_gallery_item,list));
            }
        }
    }
    class GalleryAdapter extends ArrayAdapter<Image>{


        public GalleryAdapter(ArrayList<Image> list) {
            super(getActivity(),0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
            }
            ImageView imageView= (ImageView) convertView.findViewById(R.id.gallery_item_imageview);
            //imageView.setImageBitmap(mBitmaps.get(position));
            Image item=getItem(position);
            mThumbnailDownloader.queueThumbDownload(imageView,item.getThumbnailUrl());
            return convertView;
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.clearQueue();
        mThumbnailDownloader.quit();
        Log.i(TAG,"Background thread destroyed");
    }
}
