package com.virun.loginlibrary.network.internal;

/**
 * 통신 응답 Listener
 *
 * @author Webcash Smart
 * @since 2017. 7. 25.
**/
public interface OnNetworkListener {

    /**
     * 정상 응답 메소드
     * @param tranCode 통신 구분 Code
     * @param object 응답 Data
     */
    public void onNetworkResponse(String tranCode, Object object);

    /**
     * 오류 응답 메소드
     * @param tranCode 통신 구분 Code
     * @param object 오류 응답 Data
     */
    public void onNetworkError(String tranCode, Object object);
}
