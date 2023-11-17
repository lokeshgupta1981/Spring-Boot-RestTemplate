package com.howtodoinjava.app.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Component
public class GZipServletFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 判断requset中body不为空，且请求头中包含gzip
        String contentEncoding = httpRequest.getHeader("Content-Encoding");

        if (httpRequest.getContentLength() > 0 && 
        contentEncoding != null && contentEncoding.contains("gzip")) {
            // 打印解压前的body大小
            System.out.println("#### before decompress: #### \n" + httpRequest.getContentLength());
            
            request = new GZipServletRequestWrapper(httpRequest);
        }
        chain.doFilter(request, response);
    }

    private static class GZipServletRequestWrapper extends HttpServletRequestWrapper {
        public GZipServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decompress(super.getInputStream()));
                       
            return new ServletInputStream() {
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new RuntimeException("Not implemented");
                }
            };
        }

        private byte[] decompress(ServletInputStream compressedStream) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(compressedStream)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipInputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }

            // 打印解压后body的内容和大小
            System.out.println("#### content after decompress: #### \n" + out.toString());
            System.out.println("#### size after decompress: #### \n" + out.toByteArray().length);

            return out.toByteArray();
        }
    }
}

