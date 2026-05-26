package com.kset.common.utils.http;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KsetHttp {

    private static volatile OkHttpClient okHttpClient= null;

    private static volatile KsetHttp okHttp= null;

    private KsetHttp() {
        Dispatcher dispatcher=new Dispatcher();
        dispatcher.setMaxRequests(1000);
        dispatcher.setMaxRequestsPerHost(1000);
        ConnectionPool connectionPool=new ConnectionPool(300,5,TimeUnit.MINUTES);
        okHttpClient = new OkHttpClient.Builder().dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpLogInterceptor())
//                .addInterceptor(new RetryInterceptor())
                .build();

    }

    public static KsetHttp build() {
        if(okHttp==null){
            synchronized (KsetHttp.class){
                if(okHttp==null){
                    okHttp=new KsetHttp();
                }
            }
        }
        return okHttp;
    }

    /**
     * @Author kmb
     * @Description 异步请求
     * @Date 11:35 2020/7/2
     * @Param [request]
     * @return void
     **/
    public void enqueue(Request request , Callback callback){
        okHttpClient.newCall(request).enqueue(callback);
    }



    /**
     * @Author kmb
     * @Description 同步请求
     * @Date 11:35 2020/7/2
     * @Param [request]
     * @return java.lang.String
     **/
    public String execute(Request request) throws IOException {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            ResponseBody body=response.body();
            String result=new String(body.bytes(),"utf-8");
            response.close();
            return result;
        } catch (IOException e) {
            throw e;
        }
    }

    public String execute(String url, Map<String,String> headers, Map<String,String> formBody) throws IOException {
        FormBody.Builder builder=new FormBody.Builder();
        formBody.entrySet().forEach(t->{
            if(t.getValue()!=null){
                builder.add(t.getKey(), StringUtils.toEncodedString(t.getValue().getBytes(), Charset.defaultCharset()));
            }
        });
        Request request=new Request.Builder()
                .url(url)
                .post(builder.build())
                .headers(Headers.of(headers))
                .build();
        return execute(request);
    }


    public void enqueue(String url,Map<String,String> headers,Map<String,String> formBody, Callback callback){
        FormBody.Builder builder=new FormBody.Builder();
        formBody.forEach((key, value) -> {
            if (value != null) {
                builder.add(key, StringUtils.toEncodedString(value.getBytes(), Charset.defaultCharset()));
            }
        });
        Request request=new Request.Builder()
                .url(url)
                .post(builder.build())
                .headers(Headers.of(headers))
                .build();
        enqueue(request,callback);
    }
}
