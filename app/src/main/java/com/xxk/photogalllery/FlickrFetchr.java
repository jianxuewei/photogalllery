package com.xxk.photogalllery;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xxk on 15/12/5.
 */
public class FlickrFetchr {
    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url= new URL(urlSpec);
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        StringBuilder sb=new StringBuilder();
        try {
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream in=connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                Log.d("baidu","error");
                return null;
            }
            int buffReader=0;
            byte[] buff=new byte[1024];
            while ((buffReader=in.read(buff))!=-1){
//                String str=new String(buff,0,buffReader);
//                sb.append(str);
                out.write(buff,0,buffReader);
            }
            in.close();

            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String getUrl(String urlSpec) throws IOException {
        return new  String(getUrlBytes(urlSpec));
    }

//    public  ArrayList<Image> getImageFromJsonArray(String connect) throws JSONException, IOException {
//        Log.d("baidu",connect);
//        String result=getUrlBytes(connect);
//        Log.d("baidu",result);
//        ArrayList<Image> items=new ArrayList<>();
//        JSONArray imageJsonArray = new JSONObject(result).getJSONArray("imgs");
//        for (int i = 0; i < imageJsonArray.length(); i++) {
//
//
//            JSONObject object = ((JSONObject) imageJsonArray.get(i));
//            if (object != null) {
//                String id = object.getString("id");
//                String desc = object.getString("desc");
//                String downloadUrl = object.getString("downloadUrl");
//                String thumbnailUrl = object.getString("thumbnailUrl");
//                Image image = new Image();
//                image.setId(id);
//
//                image.setDesc(desc);
//                image.setThumbnailUrl(thumbnailUrl);
//                image.setDownloadUrl(downloadUrl);
//                items.add(image);
//
//                //sb.append("id:"+id+",desc:"+desc+"\n");
//            }
//        }
//        return items;
//
//    }

}
