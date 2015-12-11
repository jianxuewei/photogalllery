package com.xxk.photogalllery;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private GridView mGridView;
    //private TextView mTextView;
    //private ImageView mImageView;
    private ArrayList<Image> mImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        mGridView= (GridView) v.findViewById(R.id.gridview);
//        View v = inflater.inflate(R.layout.temp, container, false);
//        mImageView= (ImageView) v.findViewById(R.id.imageview);
        new BaiduConnectAsyncTask().execute("http://image.baidu.com/data/imgs?col=美女&tag=诱惑&sort=0&pn=20&rn=25&p=channel&from=1");
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
                ArrayList<String> list=new ArrayList<>();
                //ArrayList<Bitmap> bitmaps=new ArrayList<>();
                for (int i = 0; i < mImages.size(); i++) {
                    String str=mImages.get(i).getDesc();
                    list.add(str);
                    //bitmaps.add(bitmap);
                }
                //mImageView.setImageBitmap(bitmap);
                mGridView.setAdapter(new GalleryAdapter(bitmaps));
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity(),mImages.get(position).toString(),Toast.LENGTH_SHORT).show();
                    }
                });
                //mGridView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_gallery_item,list));
            }
        }
    }
    class GalleryAdapter extends ArrayAdapter<Bitmap>{


        public GalleryAdapter(ArrayList<Bitmap> list) {
            super(getActivity(),0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
            }
            ImageView view= (ImageView) convertView.findViewById(R.id.gallery_item_imageview);
            view.setImageBitmap(getItem(position));
            return convertView;
        }

    }

}
