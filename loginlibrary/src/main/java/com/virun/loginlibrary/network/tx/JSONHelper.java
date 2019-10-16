package com.virun.loginlibrary.network.tx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSON Data 관리
 *
 * @author Webcash Smart
 * @since 2017. 7. 25.
**/
public class JSONHelper {

    /**
     * object Data 를 JSON 형식으로 파싱하여 리턴
     * <br><br>
     * - Map -> JSONObject 로 변환 <br>
     * - Iterable -> JSONArray 로 변환 <br>
     *
     * @param object Map, Iterable 형식 data
     * @return Object ( Map -> JSONObject, Iterable -> JSONArray, etc -> Object )
     * @throws JSONException
     */
    public static Object toJSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map<?, ?> map = (Map<?, ?>) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            return json;
        } else if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : ((Iterable<?>)object)) {
                json.put(value);
            }
            return json;
        } else {
            return object;
        }
    }

    /**
     * JSONObject Data 유무 <br>
     *
     * @param object JSONObject
     * @return true or false
     */
    public static boolean isEmptyObject(JSONObject object) {
        return object.names() == null;
    }


    /**
     * JSONObject의 key 에 해당하는 Data 를 HashMap 형식으로 리턴 <br>
     * @param object JSONObject
     * @param key key
     * @return HashMap<String, Object>
     * @throws JSONException
     */
    public static HashMap<String, Object> getHashMap(JSONObject object, String key) throws JSONException {
        return toHashMap(object.getJSONObject(key));
    }

    /**
     * JSONObject -> HashMap 으로 변환 <br>
     * @param object JSONObject
     * @return HashMap<String, Object>
     * @throws JSONException
     */
    public static HashMap<String, Object> toHashMap(JSONObject object) throws JSONException {
    	HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<?> keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    /**
     * JSONArray -> List 로 변환 <br>
     * @param array JSONArray
     * @return List<Object>
     * @throws JSONException
     */
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    /**
     * JSON format 에 따라 HashMap 또는 List 리턴 <br>
     * @param json JSONObject 또는 JSONArray
     * @return JSONObject -> HashMap , JSONArray -> List , json == null -> null , etc -> json ( Object )
     * @throws JSONException
     */
    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toHashMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
}
