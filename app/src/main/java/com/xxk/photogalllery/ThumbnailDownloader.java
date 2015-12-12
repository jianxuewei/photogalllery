package com.xxk.photogalllery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xxk on 15/12/11.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    public static final String TAG="ThumbnailDownloader";
    private static int DOWNLOAD_MESSAGE_WHAT=0;
    Handler mHandler;
    Map<Token,String> requestMap= Collections.synchronizedMap(new HashMap<Token,String>());
    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    protected void onLooperPrepared() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==DOWNLOAD_MESSAGE_WHAT){
                    Token token= (Token) msg.obj;
                    Log.i(TAG,"Got a request for url:"+requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }

    private void handleRequest(final Token token) {
        final String url=requestMap.get(token);
        if(url==null){
            return;
        }
        try {
            byte[] byteArray=new FlickrFetchr().getUrlBytes(url);
            Bitmap bitmap= BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            Log.i(TAG,"bitmap created");
        } catch (IOException e) {
            Log.i(TAG,"Error downloading image",e);
        }
    }

    public void queueThumbDownload(Token token, String url){
        Log.d(TAG,"Got an url"+url);
        requestMap.put(token,url);
        mHandler.obtainMessage(DOWNLOAD_MESSAGE_WHAT,token)
                .sendToTarget();
    }
}
