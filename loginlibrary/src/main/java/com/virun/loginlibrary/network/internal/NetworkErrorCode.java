package com.virun.loginlibrary.network.internal;

/**
 * NetworkErrorCode
 * <br><br>
 * 통신 오류 코드 정보 <br>
 *
 * @author Webcash Smart
 * @since 2017.07
 */
public class NetworkErrorCode {

    /**
     * 공통에서 전문발송 데이타를 만드는중에러
     */
    public static final String TRNS_ERRCD_MAKE  		= "T001";

    /**
     * gate 페이지로 전문전송시 timeout
     */
    public static final String TRNS_ERRCD_TIMEOUT  		= "T002";

    /**
     * gate 페이지로 전문전송시 gate 페이지 오류
     */
    public static final String TRNS_ERRCD_PAGEERR  		= "T003";

    /**
     * 전문결과 받은후 공통에서 json 파싱중 에러 (전문 형식 잘못됨)
     */
    public static final String TRNS_ERRCD_PASER  		= "T004";

    /**
     * 인터넷 연결안됨
     */
    public static final String TRNS_ERRCD_INTERNET  	= "T005";

    /**
     * 전문전송중 알수 없는 오류
     */
    public static final String TRNS_ERRCD_UNKNOWN  		= "T999";

    /**
     * 공통웹브라우져에서 잘못된 링크정보를 받은경우
     */
    public static final String APP_ERRCD_BROWSER_URL  	= "A002";
    /**
     * APP에서 알수없는 오류 발생
     */
    public static final String APP_ERRCD_UNKNOWN  		= "A999";

    /**
     * PUSH UUID 생성 실패
     */
    public static final String PUSH_ERRCD_FAIL_UUID 	= "P001";

    /**
     * PUSH 등록 오류
     */
    public static final String PUSH_ERRCD_UNKNOWN	= "P002";

    /**
     * PUSH GCM id 발급 실패
     */
    public static final String PUSH_ERRCD_FAIL_REGID 	= "P003";
}