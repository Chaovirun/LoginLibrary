package com.virun.loginlibrary.network;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.virun.loginlibrary.network.internal.NoSSLv3Factory;
import com.virun.loginlibrary.network.internal.OnNetworkListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * VolleyNetwork
 * <br><br>
 * - Volley Library 를 사용한 통신단
 *
 * @author Webcash Smart
 * @since 2017. 7. 20.
 **/
public class VolleyNetwork {

	/** Context */
	private Context mContext;

	/** 타임아웃 시간 (default = 5초) */
	private static int TIME_OUT	= 60 * 1000;

	/** 통신 시작 시간 ( 통신 요청 시간 ) */
	private long mStartTime;

	/** 통신 종료 시간 ( 통신 응답 받은 시간 ) */
	private long mFinishTime;

	/** VolleyNetwork Listener */
	private OnNetworkListener mOnNetworkListener;

	/** Charset (default = UTF-8) */
	private Charset CHARSET = Charset.forName("UTF-8");

	/** VolleyNetwork Volley RequestQueue */
	public static RequestQueue mRequestQueue = null;

	/** 공통 Headers */
	private Map<String, String> mHeaders = null;

	/** 통신 시 SSL 사용 여부 (default = false) */
	private boolean mUseSSL = false;

	/**
	 * 생성자
	 * <br><br>
	 * - Volley RequestQueue 초기화 <br>
	 * @param context Context
	 * @param onVolleyNetworkListener 통신 Callback 리스너 ( OnNetworkListener )
	 */
	public VolleyNetwork(Context context, OnNetworkListener onVolleyNetworkListener) {

		mContext = context;
		mOnNetworkListener = onVolleyNetworkListener;

		initRequestQueue();
	}

	/**
	 * 생성자
	 * <br><br>
	 * - Volley RequestQueue 초기화 <br>
	 *
	 * @param context Context
	 * @param onVolleyNetworkListener 통신 Callback 리스너 ( OnNetworkListener )
	 * @param timeout 타임아웃
	 */
	public VolleyNetwork(Context context, OnNetworkListener onVolleyNetworkListener, int timeout) {
		mContext = context;
		mOnNetworkListener = onVolleyNetworkListener;
		TIME_OUT = timeout;

		initRequestQueue();
	}

	/**
	 * 생성자
	 * <br><br>
	 * - Volley RequestQueue 초기화 <br>
	 *
	 * @param context Context
	 * @param onVolleyNetworkListener 통신 Callback 리스너 ( OnNetworkListener )
	 * @param timeout 타임아웃
	 * @param useSSL 사용 여부
	 */
	public VolleyNetwork(Context context, OnNetworkListener onVolleyNetworkListener, int timeout, boolean useSSL) {
		mContext = context;
		mOnNetworkListener = onVolleyNetworkListener;
		TIME_OUT = timeout;
		mUseSSL = useSSL;

		initRequestQueue();
	}

	/**
	 * charset 설정 <br>
	 *
	 * @param charset CHARSET 문자열
	 */
	public void setCharset(String charset) {
		CHARSET = Charset.forName(charset);
	}

	/**
	 * 현재 charset String 가져오기 <br>
	 */
	public String getCharset() {
		return CHARSET.name();
	}

	/**
	 * Volley RequestQueue 초기화 <br>
	 */
	private void initRequestQueue() {
		try {

			// mRequestQueue의 값이 없을 경우만 생성한다.
			if (mRequestQueue == null) {
				/**
				 * -[2016.9.13] :: kkb ::
				 * 공통단 CompileVersion 21 -> 23 변경으로 Volley 통신단 수정
				 * - HttpClient -> HttpUrlConnection 방식으로 Volley 통신단 변경
				 * - Cookie 동기화 및 관리 변경
				 */
				CookieHandler.setDefault(new java.net.CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));

				HttpStack httpStack;
				if( mUseSSL )
					httpStack = new HurlStack(null, new NoSSLv3Factory());
//					httpStack = new HurlStack(null, createSslSocketFactory());
				else
					httpStack = new HurlStack();

				mRequestQueue = Volley.newRequestQueue(mContext, httpStack);

				// TODO 공통 Header 기본 세팅
				mHeaders = new HashMap<String, String>();
				mHeaders.put("charset", CHARSET.name());
				mHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset="+CHARSET.name());
			}

		} catch (Exception e) {
//			PrintLog.printException(mContext, "VolleyNetwork", e);
			e.printStackTrace();
		}
	}

	/**
	 * SSL 통신을 위한 SSLSocketFactory 생성, 반환 <br>
	 * @return SSLSocketFactory
	 */
	private static SSLSocketFactory createSslSocketFactory() {
		TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}
		}};

		SSLContext sslContext = null;
		SSLSocketFactory sslSocketFactory = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, byPassTrustManagers, new SecureRandom());
			sslSocketFactory = sslContext.getSocketFactory();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		return sslSocketFactory;
	}

	/**
	 * 공통 Headers 설정 <br>
	 */
	public Map<String, String> setComHeaders(Map<String, String> headers) {
		mHeaders = headers;
		return mHeaders;
	}

	/**
	 * 통신 타임아웃 설정 <br>
	 * @param timeout 타임아웃 시간
	 */
	public void setVolleyTimeout(int timeout) {
		TIME_OUT = timeout;
	}
	/**
	 * Volley 통신 요청
	 * <br><br>
	 * - Volley 통신 (JsonObjectRequest) <br>
	 * - 요청 시 전달 파라미터 JSONObject <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkResponse (String tranCode, JSONObject result); 로 Callback <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkError (String tranCode, VolleyError error); 로 Callback <br>
	 *
	 * @param tranCode : HTTP 전문 코드
	 * @param postType : HTTP 통신 타입 (true : POST , false : GET)
	 * @param url : HTTP URL
	 * @param jsonObject : 요청 JSON 데이터 (JSONObject)
	 * @param session : 세션 (true : 세션 동기화, false : 세션 동기화 안함)
	 */
	public void requestVolleyNetwork(final String tranCode, final boolean postType, final String url, JSONObject jsonObject, boolean session) {
		try {
			// 세션동기화일 경우 cookie가 세팅된 requestQueue 재생성
			if (session) {
				setLoginVolley();
				//syncSessionWithWebView();
			}

			// requestTypePost 에 따라 세팅
			int requestType = Request.Method.POST;
			if (!postType) requestType = Request.Method.GET;

//			DevLog.devLog("VolleyNetwork", "requestVolleyNetwork url ::    " + url);
//			DevLog.devLog("VolleyNetwork", "requestVolleyNetwork json ::    " + jsonObject.toString());

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, url, jsonObject,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject result) {
							try {
								mFinishTime = System.currentTimeMillis();
								long runningTime = mFinishTime - mStartTime;

								// Listener 로 result 리턴
								mOnNetworkListener.onNetworkResponse(tranCode, result);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							try {
								mFinishTime = System.currentTimeMillis();
								long runningTime = mFinishTime - mStartTime;

								// Listener 로 result 리턴
								mOnNetworkListener.onNetworkError(tranCode, error);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
			) {
				@Override
				public Map<String, String> getHeaders()
						throws AuthFailureError {
					return mHeaders;
				}

//						@Override
//						protected Response<JSONObject> parseNetworkResponse(
//								NetworkResponse response) {
//							try {
//								Log.v("kkb","Volley Response : " + new String(response.data, "utf-8"));
//							} catch (UnsupportedEncodingException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							return super.parseNetworkResponse(response);
//						}
			};

			// volley timeout setting
			jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT,
					0,
//					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			mRequestQueue.getCache().clear();
			mRequestQueue.add(jsonObjectRequest);
			mStartTime = System.currentTimeMillis();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Volley 통신 요청
	 * <br><br>
	 * - Volley 통신 (JsonObjectRequest) <br>
	 * - 요청 시 전달 파라미터 JSONArray <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkResponse (String tranCode, JSONObject result); 로 Callback <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkError (String tranCode, VolleyError error); 로 Callback <br>
	 *
	 * @param tranCode : HTTP 전문 코드
	 * @param postType : HTTP 통신 타입 (true : POST , false : GET)
	 * @param url : HTTP URL
	 * @param jsonArray : 요청 JSON 데이터 (JSONArray)
	 * @param session : 세션 (true : 세션 동기화, false : 세션 동기화 안함)
	 */
	public void requestVolleyNetwork(final String tranCode, final boolean postType, final String url, JSONArray jsonArray, boolean session) {
		try {
			// 세션동기화일 경우 cookie가 세팅된 requestQueue 재생성
			if (session) {
				setLoginVolley();
				//syncSessionWithWebView();
			}

			// requestTypePost 에 따라 세팅
			int requestType = Request.Method.POST;
			if (!postType) requestType = Request.Method.GET;

//			DevLog.devLog("VolleyNetwork", "requestVolleyNetwork url ::    " + url);
//			DevLog.devLog("VolleyNetwork", "requestVolleyNetwork json ::    " + jsonArray.toString());

			JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
				@Override
				public void onResponse(JSONArray array) {
					try {
						mFinishTime = System.currentTimeMillis();
						long runningTime = mFinishTime - mStartTime;

						// Listener 로 result 리턴
						mOnNetworkListener.onNetworkResponse(tranCode, array);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError volleyError) {
					try {
						mFinishTime = System.currentTimeMillis();
						long runningTime = mFinishTime - mStartTime;

						// Listener 로 result 리턴
						mOnNetworkListener.onNetworkError(tranCode, volleyError);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}){
				@Override
				public Map<String, String> getHeaders()
						throws AuthFailureError {
					return mHeaders;
				}};

			// volley timeout setting
			jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT,
					0,
//					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

			mRequestQueue.getCache().clear();
			mRequestQueue.add(jsonArrayRequest);
			mStartTime = System.currentTimeMillis();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Volley 통신 요청
	 * <br><br>
	 * - Volley 통신 (StringRequest)<br>
	 * - 요청 시 전달 파라미터 없음 <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkResponse (String tranCode, JSONObject result); 로 Callback <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkError (String tranCode, VolleyError error); 로 Callback <br>
	 *
	 * @param tranCode	: HTTP 전문 코드
	 * @param postType	: HTTP 통신 타입 (true:POST, false:GET)
	 * @param url		: HTTP URL
	 * @param session	: 세션 (true:세션 동기화, false:세션 동기화 안함)
	 */
	public void requestVolleyNetwork(final String tranCode, final boolean postType, final String url, boolean session) {
		try {

			// 세션동기화일 경우 cookie가 세팅된 requestQueue 재생성
			if (session) {
				setLoginVolley();
				//syncSessionWithWebView();
			}

//			DevLog.devLog("VolleyNetwork", "requestVolleyNetwork url ::    " + url);
			// requestTypePost 에 따라 세팅
			int requestType = Request.Method.POST;
			if (!postType) requestType = Request.Method.GET;

			// Request
			StringRequest request = new StringRequest(requestType, url, new Response.Listener<String>() {

				@Override
				public void onResponse(String result) {

					try {
						mFinishTime = System.currentTimeMillis();
						long runningTime = mFinishTime - mStartTime;

						// Listener 로 result 리턴
						mOnNetworkListener.onNetworkResponse(tranCode, result);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			},

					// 에러응답
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError e) {
							e.printStackTrace();

							mFinishTime = System.currentTimeMillis();
							long runningTime = mFinishTime - mStartTime;
							// Listener 로 error 리턴
							mOnNetworkListener.onNetworkError(tranCode, e);
						}
					}){

				@Override
				protected Response<String> parseNetworkResponse(
						NetworkResponse response) {
					try {
						return Response.success(new String (response.data, CHARSET),HttpHeaderParser.parseCacheHeaders(response));
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}

				}
			};

			// volley timeout setting
			request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT,
					0,
//					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

			mRequestQueue.getCache().clear();
			mRequestQueue.add(request);

			mStartTime = System.currentTimeMillis();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Volley 통신 요청
	 * <br><br>
	 * - Volley 통신 (StringRequest)<br>
	 * - 요청 시 전달 파라미터 Map<String,String> <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkResponse (String tranCode, JSONObject result); 로 Callback <br>
	 * - 응답 성공 : mOnNetworkListener.onNetworkError (String tranCode, VolleyError error); 로 Callback <br>
	 *
	 * @param tranCode :  HTTP 전문 코드
	 * @param postType : HTTP 통신 타입 (true : POST , false : GET)
	 * @param url : HTTP URL
	 * @param param : request 전문 data (Map<String,String>)
	 * @param session  : 세션 (true : 세션 동기화, false : 세션 동기화 안함)
	 */
	public void requestVolleyNetwork(final String tranCode, final boolean postType, final String url, final Map<String,String> param ,boolean session) {
		try {

			// 세션동기화일 경우 cookie가 세팅된 requestQueue 재생성
			if (session) {
				setLoginVolley();
				//syncSessionWithWebView();
			}

			// requestTypePost 에 따라 세팅
			int requestType = Request.Method.POST;
			if (!postType) requestType = Request.Method.GET;

//			DevLog.eLog("VolleyNetwork", "request VolleyNetwork url ::    " + url);
//			DevLog.eLog("VolleyNetwork", "request VolleyNetwork param ::    " + param.toString());

			// Request
			StringRequest request = new StringRequest(requestType, url
					, new Response.Listener<String>() {
				@Override
				public void onResponse(String result) {

					try {
						mFinishTime = System.currentTimeMillis();
						long runningTime = mFinishTime - mStartTime;
						// Listener 로 result 리턴
						mOnNetworkListener.onNetworkResponse(tranCode, result);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			},
					// 에러응답
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError e) {
							e.printStackTrace();

							mFinishTime = System.currentTimeMillis();
							long runningTime = mFinishTime - mStartTime;

							// Listener 로 error 리턴
							mOnNetworkListener.onNetworkError(tranCode, e);
						}
					})
			{
				@Override
				protected Response<String> parseNetworkResponse(
						NetworkResponse response) {
					try {
//						DevLog.eLog("VolleyNetwork", "response ::    " + new String( response.data));
						return Response.success(new String (response.data, CHARSET),HttpHeaderParser.parseCacheHeaders(response));
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}

				}

				@Override
				protected String getParamsEncoding() {
//					DevLog.eLog("VolleyNetwork", "getParamsEncoding : " +CHARSET.name() );
					return CHARSET.name();
				}

				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					//+ 네트워크 파라미터
					return checkParams(param);
//					return param;
				}

				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
//					DevLog.eLog("VolleyNetwork", "getHeaders : " +mHeaders.toString() );
					return mHeaders;
				}

				private Map<String, String> checkParams(Map<String, String> map){
					Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
						if(pairs.getValue()==null){
							map.put(pairs.getKey(), "");
						}
					}
					return map;
				}

			};

			// volley timeout setting
			request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT,
					0,
//					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

			mRequestQueue.getCache().clear();
			mRequestQueue.add(request);

			mStartTime = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * HttpClient 세션 동기화 통신
	 * <br><br>
	 * - 세션 동기화가 필요 할 경우 requestQueue 세팅 <br>
	 */
	private void setLoginVolley() {
		try {

			CookieHandler.setDefault(new java.net.CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
			HttpStack httpStack = new HurlStack();
			mRequestQueue = Volley.newRequestQueue(mContext, httpStack);
		} catch (Exception e) {
//			PrintLog.printException(mContext, "VolleyNetwork", e);
			e.printStackTrace();
		}
	}


	/**
	 * WebView 쿠키 동기화
	 * <br><br>
	 * - Build.VERSION_CODES.LOLLIPOP 기준으로 쿠키 동기화 구분 처리
	 *
	 * @param domain : 도메인 uri
	 */
	public void syncSessionWithWebView(String domain) {
		try {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);

			CookieStore cookieStore = ((java.net.CookieManager)CookieHandler.getDefault()).getCookieStore();
			URI uri = null;
			try {
				uri = new URI(domain);
			} catch (URISyntaxException e) { e.printStackTrace(); }
			String url = uri.toString();

			List<HttpCookie> cookies = cookieStore.get(uri);
			for (HttpCookie cookie : cookies) {
				String setCookie = new StringBuilder(cookie.toString())
						.append("; domain=").append(cookie.getDomain())
						.append("; path=").append(cookie.getPath())
						.toString();

//				DevLog.devLog("kkb", setCookie);

				cookieManager.setCookie(url, setCookie);
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				CookieManager.getInstance().flush();
//				DevLog.devLog("kdb", "syncSessionWithWebView :: " + CookieManager.getInstance().getCookie(domain));
			} else {
				try {
					CookieSyncManager.getInstance();
				} catch (Exception e) {
					CookieSyncManager.createInstance(mContext);
				}
				CookieSyncManager.getInstance().sync();
//				DevLog.devLog("kdb", "syncSessionWithWebView :: " + CookieManager.getInstance().getCookie(domain));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * WebView 쿠키 동기화
	 * <br><br>
	 * - Build.VERSION_CODES.LOLLIPOP 기준으로 쿠키 동기화 구분 처리
	 */
	public void syncSessionWithWebView() {
		// TODO 도메인 세팅 필요
		String domain = "";
		syncSessionWithWebView(domain);
	}

	public void cancelRequest(){
		if(mRequestQueue!=null){
			mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
				@Override
				public boolean apply(Request<?> request) {
					return true;
				}
			});
		}
	}
}