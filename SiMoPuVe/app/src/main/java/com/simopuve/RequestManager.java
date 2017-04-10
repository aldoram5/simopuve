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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simopuve.model.PDVSurvey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by aldorangel on 3/29/17.
 */

public class RequestManager {

    private String TAG = RequestManager.class.getSimpleName();

    public static final String LOGIN_SERVICE_URL = "http://simopuve-aldoram5.rhcloud.com/rest/tests/login";
    public static final String PDV_UPLOAD_SERVICE_URL = "http://simopuve-aldoram5.rhcloud.com/rest/tests/survey";

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
                Log.e(TAG,error.toString());
                listener.onFailure(error);
            }
        });
        rq.add(jsonArrayRequest);
    }

    public void uploadPDV(PDVSurvey survey, final JSONObjectCallbackListener listener){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(java.util.Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(java.util.Date src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.getTime());
            }
        });

        gsonBuilder.registerTypeAdapter(java.util.Date.class, new JsonDeserializer<Date>() {
            @Override
            public java.util.Date deserialize(com.google.gson.JsonElement p1, java.lang.reflect.Type p2,
                                              com.google.gson.JsonDeserializationContext p3) {
                return new java.util.Date(p1.getAsLong());
            }
        });
        Gson gson = gsonBuilder.create();

        JsonObjectRequest jsonArrayRequest = null;
        Log.d(TAG, gson.toJson(survey));
        try {
            jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, PDV_UPLOAD_SERVICE_URL,new JSONObject( gson.toJson(survey)), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    listener.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,error.toString());
                    listener.onFailure(error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rq.add(jsonArrayRequest);
    }
}
