package com.kset.common.utils.http;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpLogInterceptor implements Interceptor {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String reqBody = readRequestBody(request.body());

        if (log.isDebugEnabled()) {
            log.debug("http req method={} url={} headers={} body={}",
                    request.method(), request.url(), request.headers(), reqBody);
        }

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        String rspBody = responseBody != null ? readResponseBody(responseBody) : null;

        if (log.isDebugEnabled()) {
            log.debug("http rsp code={} url={} tookMs={} body={}",
                    response.code(), response.request().url(), tookMs, rspBody);
        }

        return response;
    }

    private static String readRequestBody(RequestBody requestBody) throws IOException {
        if (requestBody == null) {
            return null;
        }
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readString(charset(requestBody.contentType()));
    }

    private static String readResponseBody(ResponseBody responseBody) throws IOException {
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();
        return buffer.clone().readString(charset(responseBody.contentType()));
    }

    private static Charset charset(MediaType contentType) {
        if (contentType == null) {
            return UTF8;
        }
        try {
            Charset resolved = contentType.charset(UTF8);
            return resolved != null ? resolved : UTF8;
        } catch (UnsupportedCharsetException e) {
            log.warn("Unsupported charset in content-type: {}", contentType, e);
            return UTF8;
        }
    }
}
