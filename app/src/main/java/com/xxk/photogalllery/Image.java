package com.xxk.photogalllery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xxk on 15/12/10.
 */
public class Image {
    private String id;
    private String desc;
    private String downloadUrl;
    private String thumbnailUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String toString() {
        return desc;
    }
//    public Image(JSONObject object) throws JSONException {
//        setId(object.getString("id"));
//        setDesc(object.getString("desc"));
//        setDownloadUrl(object.getString("downloadUrl"));
//        setThumbnailUrl(object.getString("thumbnailUrl"));
//    }
}
