package io.github.yusukeiwaki.githubviewer;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

/**
 */
public class OkHttpHelper {
    private static OkHttpClient sClient = null;

    public static OkHttpClient getHttpClient() {
        if (sClient == null) {
            sClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
        }
        return sClient;
    }
}
