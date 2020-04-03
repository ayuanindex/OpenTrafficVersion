package com.realmax.opentrafficversion;

/**
 * @ProjectName: BaiduApiTest
 * @Package: com.realmax.baiduapitest
 * @ClassName: Values
 * @CreateDate: 2020/3/19 14:01
 */
public class Values {
    /**
     * 官网获取的 API Key 更新为你注册的
     */
    /*public static String CLIENT_ID = "miGMdcGzS9DAVdZezwNG29IE";*/
    public static String CLIENT_ID = "FV4E08IcfHTs71lZvyVWqzIw";
    /**
     * 官网获取的 Secret Key 更新为你注册的
     */
    /*public static String CLIENT_SECRET = "NifyVDUXLWIaCbIBCzEaLvCEVGKuRiA2";*/
    public static String CLIENT_SECRET = "egVNNuRlBE7l3vNCKAiKvcNR5Y19rZMp";

    public static String GRANT_TYPE = "client_credentials";

    public static String TOKEN = "";

    /**
     * 网络请求读写时长
     */
    public static final int REQUEST_TIME = 30;

    public final static String BASE_URL = "https://aip.baidubce.com/";
    public final static String BASE_URL_IMG = "https://aip.baidubce.com/upload/";

    /**
     * 通用文字识别
     */
    public final static String STR_ORC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    /**
     * 车牌识别
     */

    public final static String LICENSE_PLATE_ORC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";

    /**
     * 手写文字识别
     */
    public final static String HANDWRITING_ORC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/handwriting";
}
