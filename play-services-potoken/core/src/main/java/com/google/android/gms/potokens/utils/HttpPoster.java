package com.google.android.gms.potokens.utils;


import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.SSLException;

/**
 * Created by Frank on 2016/9/21.
 */
public class HttpPoster {

    public static String httpGet(String strurl, String userAgent) throws IOException {
        String result = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = new URL(strurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(7 * 1000);
            conn.setReadTimeout(7 * 1000);
            conn.setRequestMethod("GET");
            if (null != userAgent) {
                conn.setRequestProperty("User-Agent", userAgent);
            }
            is = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            StringBuffer res = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            result = res.toString();
        } catch (SSLException e) {
        } catch (FileNotFoundException e) {
//            BugHelper.postCatchedException(e);
        } catch (Error e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
                if (null != conn) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static String httpPost(String strurl, byte entity[], String userAgent) throws IOException {
        String result = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = new URL(strurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(7 * 1000);
            conn.setReadTimeout(7 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);// 允许输出数据
            if (null != userAgent) {
                conn.setRequestProperty("User-Agent", userAgent);
            }
            conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
            OutputStream outStream = conn.getOutputStream();
            outStream.write(entity);
            outStream.flush();
            outStream.close();
            is = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            StringBuffer res = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            result = res.toString();
        } catch (SSLException e) {
        } catch (FileNotFoundException e) {
//            BugHelper.postCatchedException(e);
        } catch (Error e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
                if (null != conn) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static byte[] httpPostForGmsTest(String strurl, byte entity[], String userAgent) throws IOException {
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = new URL(strurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(7 * 1000);
            conn.setReadTimeout(7 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);// 允许输出数据
            if (null != userAgent) {
                conn.setRequestProperty("User-Agent", userAgent);
            }
            conn.setRequestProperty("Content-Type", "application/x-protobuf");
            conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
            OutputStream outStream = conn.getOutputStream();
            outStream.write(entity);
            outStream.flush();
            outStream.close();
            is = conn.getInputStream();
            return IOUtils.toByteArray(is);
        } catch (SSLException e) {
            Logutils.errorMsg(""+e.toString());
        } catch (FileNotFoundException e) {
//            BugHelper.postCatchedException(e);
            Logutils.errorMsg(""+e.toString());
        } catch (Error e) {
            Logutils.errorMsg(""+e.toString());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
                if (null != conn) {
                    conn.disconnect();
                    conn = null;
                }
            } catch (Exception e) {
                Logutils.errorMsg(""+e.toString());
            }
        }
        return null;
    }

}
