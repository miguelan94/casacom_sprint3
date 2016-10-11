package com.streamnow.vulcanomobile.lib;

import com.loopj.android.http.*;

/**
 * Created by Miguel Estévez on 31/1/16.
 */
public class LDConnection
{
    private static final String BASE_URL_STRING = "http://route.livingservices.com/";
    //private static final String BASE_URL_STRING = "http://route.streamnow.ch/";
    private static String _currentUrlString = null;

    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public static void get(String endpoint, RequestParams requestParams, AsyncHttpResponseHandler responseHandler)
    {
        httpClient.setEnableRedirects(true);
        httpClient.get(getAbsoluteUrl(endpoint), requestParams, responseHandler);
    }

    public static void post(String endpoint, RequestParams requestParams, AsyncHttpResponseHandler responseHandler)
    {
        httpClient.setEnableRedirects(true);
        httpClient.post(getAbsoluteUrl(endpoint), requestParams, responseHandler);
    }

    public static String getAbsoluteUrl(String endpoint)
    {
        if( _currentUrlString != null )
        {
            return _currentUrlString + endpoint;
        }
        else
        {
            return BASE_URL_STRING + endpoint;
        }
    }

    public static boolean isSetCurrentUrl()
    {
        return _currentUrlString != null;
    }

    public static void setCurrentUrlString(String urlString)
    {
        _currentUrlString = urlString;
    }

    public static AsyncHttpClient getHttpClient(){
        return httpClient;
    }

}
