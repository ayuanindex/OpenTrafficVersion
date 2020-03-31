package com.realmax.opentrafficversion;

import com.realmax.opentrafficversion.bean.TokenBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @ProjectName: BaiduApiTest
 * @Package: com.realmax.baiduapitest.util
 * @ClassName: ApiService
 * @CreateDate: 2020/3/19 13:15
 */
public interface ApiService {
    @GET("oauth/2.0/token")
    Observable<TokenBean> getToken(
            @Query("grant_type") String grant_type,
            @Query("client_id") String client_id,
            @Query("client_secret") String client_secret
    );


    @GET
    Observable<ResponseBody> getImg(@Url String url);
}
