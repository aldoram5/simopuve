package com.simopuve;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by aldorangel on 3/29/17.
 */

public class RequestManager {

    private String TAG = RequestManager.class.getSimpleName();

    public static final String LOGIN_SERVICE_URL = "http://simopuve-aldoram5.rhcloud.com/rest/tests/login";

    ///Volley required
    private RequestQueue rq;
    private Context context;
    private ImageLoader mImageLoader;

    //Singleton
    private static RequestManager instance;

    private RequestManager(){

        rq = Volley.newRequestQueue(SIMOPUVEApplication.getAppContext());
        mImageLoader = new ImageLoader(this.rq, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(20);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    //Helper Interfaces
    public interface JSONObjectCallbackListener{
        void onSuccess(JSONObject response);
        void onFailure(VolleyError error);
    }

    public interface JSONArrayCallbackListener{
        void onSuccess(JSONArray response);
        void onFailure(VolleyError error);
    }

    //Singleton
    public static synchronized RequestManager getInstance(){
        if (instance == null) {
            instance = new RequestManager();
        }
        return instance;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void authenticateUser(final JSONObjectCallbackListener listener){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_SERVICE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getLocalizedMessage());
                listener.onFailure(error);
            }
        });
        rq.add(jsonArrayRequest);
    }
}
