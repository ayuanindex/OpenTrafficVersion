package com.realmax.opentrafficversion.bean;

/**
 * @ProjectName: BaiduApiTest
 * @Package: com.realmax.baiduapitest.bean
 * @ClassName: TestBean
 * @CreateDate: 2020/3/19 13:31
 */
public class TokenBean {

    /**
     * access_token : 24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567
     * expires_in : 2592000
     */

    private String access_token;
    private int expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
