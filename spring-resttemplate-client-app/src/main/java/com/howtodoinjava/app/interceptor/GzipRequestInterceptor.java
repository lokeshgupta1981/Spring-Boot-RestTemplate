package com.howtodoinjava.app.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GzipRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 增加body大小判断，如果body大小小于1KB，则不进行压缩
        //if (body.length < 1024) {
        if (body.length < 24) {
            return execution.execute(request, body);
        }
        // 打印压缩前body的内容和长度
        String bodyStr = new String(body, "UTF-8");
        System.out.println("#### Body Original Content: ####\n" + bodyStr);
        System.out.println("#### Body Original Length: ####\n" + bodyStr.length());
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(body);
        }
        // 打印压缩后body的内容和长度
        //System.out.println("Body Compressed Content: \n" + byteArrayOutputStream.toByteArray());
        System.out.println("#### Body Compressed Content: ####\n" + new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8));
        System.out.println("#### Body Compressed Length: ####\n" + byteArrayOutputStream.toByteArray().length);

        request.getHeaders().add("Content-Encoding", "gzip");
        return execution.execute(request, byteArrayOutputStream.toByteArray());
    }
}

