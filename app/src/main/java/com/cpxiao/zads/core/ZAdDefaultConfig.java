package com.cpxiao.zads.core;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.cpxiao.BuildConfig;
import com.cpxiao.zads.ZAdsConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告本地默认配置
 *
 * @author cpxiao on 2017/08/10.
 */
public class ZAdDefaultConfig {

    private static final boolean DEBUG = ZAdsConfig.DEBUG;
    private static final String TAG = ZAdDefaultConfig.class.getSimpleName();

    /**
     * 获取本地默认配置
     *
     * @return arrayMap
     */
    public static ArrayMap<Integer, List<AdConfigBean>> getDefaultConfig(Context context) {
        if (DEBUG) {
            Log.d(TAG, "getDefaultConfig: ");
        }
        if (context == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("context == null");
            }
            return null;
        }
        String fileName = BuildConfig.APPLICATION_ID + ".json";
        if (DEBUG) {
            Log.d(TAG, "fileName = " + fileName);
        }

        InputStream inputStream = null;
        String json = null;
        try {
            inputStream = context.getAssets().open(fileName);
            json = readTextFromInputStream(inputStream);
        } catch (IOException e) {
            if (DEBUG) {
                Log.d(TAG, "getDefaultConfig: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return parseJson(json);
    }

    private static ArrayMap<Integer, List<AdConfigBean>> parseJson(String json) {
        if (DEBUG) {
            Log.d(TAG, "parseJson: ");
        }
        if (TextUtils.isEmpty(json)) {
            if (DEBUG) {
                throw new IllegalArgumentException("json is Empty!");
            }
            return null;
        }
        ArrayMap<Integer, List<AdConfigBean>> arr = new ArrayMap<>(5);
        try {
            JSONObject object = new JSONObject(json);
            JSONObject data = object.getJSONObject("data");
            JSONArray list = data.getJSONArray("data");
            if (list != null && list.length() > 0) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject obj = list.getJSONObject(i);
                    AdConfigBean ad = new AdConfigBean();
                    int adPosition = 0;
                    String ad_position = "ad_position";
                    if (obj.has(ad_position)) {
                        adPosition = obj.getInt(ad_position);
                    }
                    String ad_type = "ad_type";
                    if (obj.has(ad_type)) {
                        ad.adType = obj.getInt(ad_type);
                    }
                    String publish_id = "publish_id";
                    if (obj.has(publish_id)) {
                        ad.publishId = obj.getString(publish_id);
                    }
                    String place_id = "place_id";
                    if (obj.has(place_id)) {
                        ad.placeId = obj.getString(place_id);
                    }
                    String ad_size = "ad_size";
                    if (obj.has(ad_size)) {
                        ad.adSize = obj.getInt(ad_size);
                    }
                    List<AdConfigBean> adList = arr.get(adPosition);
                    if (adList == null) {
                        adList = new ArrayList<>();
                    }
                    adList.add(ad);
                    arr.put(adPosition, adList);
                }
            }
        } catch (JSONException e) {
            if (DEBUG) {
                Log.d(TAG, "parseJson: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            //            if (DEBUG) {
            //                e.printStackTrace();
            //            }
        }
        if (DEBUG) {
            Log.d(TAG, "广告id数量：" + arr.size());
        }
        return arr;
    }


    /**
     * 从输入流中读取文字
     *
     * @param inputSteam InputStream
     * @return String
     */
    private static String readTextFromInputStream(InputStream inputSteam) {
        if (inputSteam == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        int len;
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputSteam, "utf-8");
            while ((len = reader.read(buffer)) != -1) {
                String string = new String(buffer, 0, len);
                stringBuilder.append(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();

    }
}
