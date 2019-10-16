package com.virun.loginlibrary.network.tx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 통신 Data 관리
 * <br><br>
 * - 송신 Data HashMap 으로 구성되어 관리
 * - 수신 Data JSONArray 로 구성되어 관리
 *
 * @author Webcash Smart
 * @since 2017. 7. 25.
**/
public class TxMessage {

	/** 송신부 Data */
	protected HashMap<String, Object> mSendMessage;

	/** 수신부 Data */
	protected JSONArray mRecvMessage;

	/** 수신부 Data 현재 Index */
	protected int mRecvIdx = 0;

	/**
	 * 생성자
	 */
	public TxMessage() {}

	/**
	 * 송신 Data 초기화
	 * <br><br>
	 * - HashMap 생성 <br>
	 * @throws Exception
	 */
	public void initSendMessage() throws Exception {
		mSendMessage = new HashMap<String, Object>();
	}

	/**
	 * HashMap 으로 구성된 송신 Data 리턴한다. <br>
	 * @return HashMap<String, Object> 송신 Data
	 */
	public HashMap<String, Object> getSendMessage() {
		return mSendMessage;
	}

	/**
	 * 수신 Data 초기화 <br>
	 * @param obj 수신부 Data
	 * @throws JSONException
	 * @throws Exception
	 */
	public void initRecvMessage(Object obj) throws JSONException, Exception {

		//[2018.04.19] "null" 예외처리 추가
		if(obj.equals(JSONObject.NULL) || obj.equals("")) {
			mRecvMessage = new JSONArray("[]");
		}else {
			mRecvMessage = (JSONArray) obj;
		}
	}

	/**
	 * 현재 인덱스 리턴 <br>
	 * @return 현재 Index (mRecvIdx)
	 */
	public int getIndex() {
		return mRecvIdx;
	}

	/**
	 * 수신 data index (mRecvIdx) 에러 여부 리턴
	 * <br><br>
	 * - EOR (End Of Record) 여부 리턴
	 *
	 * @return true (EOR) / false (Not EOR)
	 * @throws JSONException
	 */
	public boolean isRecvEOR() throws JSONException {
		if(mRecvIdx == getLength())
			return true;
		return false;
	}

	/**
	 * 수신 Data 처음 Index 로 이동
	 */
	public void moveFirst() {
		mRecvIdx = 0;
	}

	/**
	 * 수신 Data 다음 Index 로 이동
	 */
	public void moveNext() {
		mRecvIdx++;
	}

	/**
	 * 수신 Data 마지막 Index 로 이동
	 * @throws JSONException
	 */
	public void moveLast() throws JSONException {
		mRecvIdx = getLength() - 1;
	}

	/**
	 * 수신 Data 이전 Index 로 이동
	 */
	public void movePrev() {
		mRecvIdx--;
	}

	/**
	 * 수신 Data 특정 위치로 이동 <br>
	 * @param pos 이동할 Index
	 */
	public void move(int pos) {
		mRecvIdx = pos;
	}

	/**
	 * 수신부 JSON Data 내 현재 위치 (mRecvIdx) 에  Key 에 해당하는 Value 존재 여부 <br>
	 * @param key Key
	 * @return true (존재) / false (미존재)
	 * @throws JSONException
	 */
	public boolean has(String key) throws JSONException {
		return mRecvMessage.getJSONObject(mRecvIdx).has(key);
	}

	/**
	 * 수신부 JSON Data 내 현재 위치 (mRecvIdx) 에 해당 Key 의 boolean Data 리턴 <br>
	 * @param key Key
	 * @return 현재 위치의 Key 에 따른 true or false value
	 * @throws JSONException
	 */
	public boolean getBoolean(String key) throws JSONException {
		if(has(key) == false) return false;
		return mRecvMessage.getJSONObject(mRecvIdx).getBoolean(key);
	}

	/**
	 *  수신부 JSON Data 내 현재 위치 (mRecvIdx) 에 해당 Key 의 Value object 리턴 <br>
	 * @param key Key
	 * @return 현재 위치의 Key 에 따른 Object Value
	 * @throws JSONException
	 */
	public Object getRecord(String key) throws JSONException {
		if(has(key) == false) return new JSONArray("[]");
		return mRecvMessage.getJSONObject(mRecvIdx).get(key);
	}

	/**
	 * 수신부 JSON Data 내 현재 위치 (mRecvIdx) 에 해당 Key 의 JSONArray 리턴 <br>
	 * @param key Key
	 * @return 현재 위치의 Key 에 따른 JSONArray Value
	 * @throws JSONException
	 */
	public JSONArray getArray(String key) throws JSONException {
		if(has(key) == false) return new JSONArray("[]");
		return mRecvMessage.getJSONObject(mRecvIdx).getJSONArray(key);
	}

	/**
	 * 수신부 JSON Data 내 현재 위치 (mRecvIdx) 에 해당 Key 의 String 리턴 <br>
	 * @param key Key
	 * @return 현재 위치의 Key 에 따른 String Value
	 * @throws JSONException
	 */
	public String getString(String key) throws JSONException {

		if(!has(key)){
			return "";
		}
//		return mRecvMessage.getJSONObject(mRecvIdx).getString(key);

		//[2018.04.19] "null" 예외처리 추가
		JSONObject obj = mRecvMessage.getJSONObject(mRecvIdx);
		if(obj != null) {
			if(obj.getString(key) != null) {
				return obj.isNull(key)? "" : obj.getString(key);
			}
		}
		return "";
	}

	/**
	 * 수신부 JSON Data 내 현재 위치 (mRecvIdx) 에 해당 Key 의 String Data 설정 <br>
	 * @param key Key
	 * @param value 설정할 String Value
	 * @throws JSONException
	 */
	public void setString(String key, String value) throws JSONException {
		mRecvMessage.getJSONObject(mRecvIdx).put(key, value);
	}

	/**
	 * 수신부 JSON Data 내 특정 위치와 해당 Key 의 String 리턴 <br>
	 * @param key Key
	 * @param idx 수신부 JSON Data 의 특정 위치 Index
	 * @return Key 와 Idx 에 따른 String Value
	 * @throws JSONException
	 */
	public String getStringIdx(String key, int idx) throws JSONException {

		if(!has(key)) {
			return "";
		}
		//return mRecvMessage.getJSONObject(idx).getString(key);

		//[2018.04.19] "null" 예외처리 추가
		JSONObject obj = mRecvMessage.getJSONObject(idx);
		if(obj != null) {
			if(obj.getString(key) != null) {
				return obj.isNull(key)? "" : obj.getString(key);
			}
		}
		return "";
	}

	/**
	 * 수신부 JSON 내 현재 위치 (mRecvIdx) 에 해당 Key 의 int 리턴 <br>
	 * @param key Key
	 * @return 현재 위치의 Key 에 따른 Int Value
	 * @throws JSONException
	 */
	public int getInt(String key) throws JSONException {
		if(has(key) == false) return 0;
		return mRecvMessage.getJSONObject(mRecvIdx).getInt(key);
	}

	/**
	 * 수신부 JSON Data LENGTH
	 * @return 수신부 JSON Data LENGTH
	 * @throws JSONException
	 */
	public int getLength() throws JSONException {
		return mRecvMessage.length();
	}

	/**
	 *  수신부 JSON DATA String 형으로 변환하여 리턴
	 * @return 수신부 JSON DATA String
	 * @throws JSONException
	 */
	public String getMessageToString() throws JSONException {
		return mRecvMessage.toString();
	}
}