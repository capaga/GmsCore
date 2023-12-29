package org.microg.gms.backup;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.backup.AppBackupDetail;
import com.google.android.gms.backup.BackupRequest;
import com.google.android.gms.backup.BackupResponse;
import com.google.android.gms.backup.BackupStateRequestConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BackupHttpRequestUtil {
    private static final String TAG = "BackupHttpRequestUtil";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();    //设置十六进制字符数组
    private long androidId;
    private String authToken;

    public BackupHttpRequestUtil(long androidId, String authToken) {
        this.androidId = androidId;
        this.authToken = authToken;
        Log.d(TAG, "BackupHttpRequestUtil: androidId=>" + this.androidId + " authToken=>" + this.authToken);
    }

    /**
     * 发送POST或PUT请求(全量备份时使用PUT请求)
     */
    private static byte[] doPostOrPut(String link, byte[] postData, HashMap<String, String> headers, String method) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod(method);
            for (String header : headers.keySet()) {
                conn.setRequestProperty(header, headers.get(header));
            }
            if (method.equals("PUT")) {
                conn.setChunkedStreamingMode(postData.length);
            }
            conn.getOutputStream().write(postData);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            InputStream inputStream = conn.getInputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            inputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, "doPostOrPut: error", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * 构造请求头
     */
    private static HashMap<String, String> generateRequestHeaders(int postBodyLength) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Gms-Backup/220920044 (gzip)");
        headers.put("Content-Type", "application/octet-stream");
        headers.put("Host", "android.googleapis.com");
        headers.put("Content-Length", String.valueOf(postBodyLength));
        headers.put("Accept-Encoding", "gzip");
        return headers;
    }

    /**
     * 构造获取备份详情请求的数据
     */
    private byte[] generateAppBackupStateRequestBody(boolean getKVBackupInfo, boolean getFullBackupInfo, boolean getPhotosBackupInfo) {
        BackupStateRequestConfig backupStateRequestConfig = BackupStateRequestConfig.newBuilder()
                .setGetKVBackupInfo(getKVBackupInfo)
                .setGetFullBackupInfo(getFullBackupInfo)
                .setGetPhotosBackupInfo(getPhotosBackupInfo).build();
        byte[] encode = BackupRequest.newBuilder()
                .setAndroidId(androidId)
                .setAndroidId2(androidId)
                .setAuthToken(authToken)
                .setBackupStateRequestConfig(backupStateRequestConfig)
                .build()
                .toByteArray();
        return encode;
    }


    /**
     * 解析获取备份详情请求的返回数据
     */
    private List<AppBackupDetail> parseAppBackupStateResponseBody(byte[] responseBody) throws IOException {
        return BackupResponse.parseFrom(responseBody).getAppBackupDetailList();
    }


    /**
     * 发送获取备份详情请求
     */
    public List<AppBackupDetail> getAppBackupState() {
        try {
            String link = "https://android.googleapis.com/backup";
            byte[] postBody = generateAppBackupStateRequestBody(true, true, true);
            Log.d(TAG, "getAppBackupState: postData=>" + bytesToHex(postBody));

            HashMap<String, String> headers = generateRequestHeaders(postBody.length);
            byte[] retData = doPostOrPut(link, postBody, headers, "POST");
            if (retData == null || retData.length == 0) {
                Log.e(TAG, "getAppBackupState: returnBody is null");
                return null;
            }
            Log.d(TAG, "getAppBackupState: " + "返回Body=>" + bytesToHex(retData));
            return parseAppBackupStateResponseBody(retData);
        } catch (IOException e) {
            Log.e(TAG, "getAppBackupState: error", e);
        }
        return null;
    }


    /**
     * 发送清除备份数据请求
     */
    public BackupResponse clearBackupData() throws IOException {
        try {
            String link = "https://android.googleapis.com/backup/cleardevice";
            byte[] postBody = generateClearBackupDataRequestBody();
            Log.d(TAG, "clearBackupData: postData=>" + bytesToHex(postBody));
            HashMap<String, String> headers = generateRequestHeaders(postBody.length);
            byte[] retData = doPostOrPut(link, postBody, headers, "POST");
            if (retData == null || retData.length == 0) {
                Log.e(TAG, "clearBackupData: returnBody is null");
                return null;
            }
            Log.d(TAG, "clearBackupData: " + "返回Body=>" + bytesToHex(retData));
            return BackupResponse.parseFrom(retData);
        } catch (Exception e) {
            Log.e(TAG, "clearBackupData: error", e);
        }
        return null;
    }


    /**
     * 构造清除备份数据请求的数据
     */
    private byte[] generateClearBackupDataRequestBody() {
        BackupRequest.ClearBackupData clearBackupData = BackupRequest.ClearBackupData.newBuilder().setIsClear(true).build();
        return BackupRequest.newBuilder()
                .setAndroidId(androidId)
                .setAndroidId2(androidId)
                .setStorageType(2)
                .setAuthToken(authToken)
                .setClearBackupData(clearBackupData)
                .build().toByteArray();
    }


    /**
     * 发送全量备份请求
     */
    public String putFullBackupData(String packageName, byte[] backupData) {
        try {
            String link = "https://android.googleapis.com/backup/upload";
            String boundary = generateRandomString(70);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("authorization", "GoogleLogin auth=" + authToken);
            headers.put("x-goog-upload-protocol", "multipart");
            headers.put("content-type", "multipart/related; boundary=" + boundary);
            headers.put("Transfer-Encoding", "chunked");
            headers.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android " + Build.VERSION.SDK_INT + "; " + Build.MODEL + " Build/" + Build.DISPLAY + ")");
            headers.put("Host", "android.googleapis.com");

            byte[] backupDataPart1 = generateFullBackupDataPart1(androidId, authToken, packageName, boundary);
            byte[] backupDataPart2 = generateFullBackupDataPart2(backupData, boundary);
            String dataSha1DigestBase64String = getDataSha1DigestBase64String(backupData);
            byte[] backupDataPart3 = generateFullBackupDataPart3(dataSha1DigestBase64String, boundary);
            byte[] postBody = new byte[backupDataPart1.length + backupDataPart2.length + backupDataPart3.length];
            System.arraycopy(backupDataPart1, 0, postBody, 0, backupDataPart1.length);
            System.arraycopy(backupDataPart2, 0, postBody, backupDataPart1.length, backupDataPart2.length);
            System.arraycopy(backupDataPart3, 0, postBody, backupDataPart1.length + backupDataPart2.length, backupDataPart3.length);
            Log.d(TAG, "putFullBackupData: postData=>" + bytesToHex(postBody));

            byte[] returnData = doPostOrPut(link, postBody, headers, "PUT");
            if (returnData == null || returnData.length == 0) {
                Log.e(TAG, "putFullBackupData: returnBody is null");
                return null;
            }
            Log.d(TAG, "putFullBackupData: " + "返回Body=>" + bytesToHex(returnData));
            return returnData.toString();
        } catch (Exception e) {
            Log.e(TAG, "putFullBackupData: error", e);
        }
        return null;
    }

    /**
     * 发送KV备份请求
     */
    public BackupResponse postKVBackupData(BackupRequest.BackupData backupData) {
        try {
            String link = "https://android.googleapis.com/backup/backup";

            byte[] postBody = generateKVBackupDataRequestBody(backupData);
            Log.d(TAG, "postKVBackupData: postData=>" + bytesToHex(postBody));

            HashMap<String, String> headers = generateRequestHeaders(postBody.length);

            byte[] returnData = doPostOrPut(link, postBody, headers, "POST");
            if (returnData == null || returnData.length == 0) {
                Log.e(TAG, "postKVBackupData: returnBody is null");
                return null;
            }
            Log.d(TAG, "postKVBackupData: " + "返回Body=>" + bytesToHex(returnData));
            return BackupResponse.parseFrom(returnData);
        } catch (Exception e) {
            Log.e(TAG, "postKVBackupData: error", e);
        }
        return null;
    }

    /**
     * 构造键值备份请求的数据
     */
    private byte[] generateKVBackupDataRequestBody(BackupRequest.BackupData backupData) {
        byte[] bytes = BackupRequest.newBuilder()
                .setAndroidId(androidId)
                .setAndroidId2(androidId)
                .setAuthToken(authToken)
                .setStorageType(2)
                .addBackupData(backupData)
                .build().toByteArray();
        return bytes;
    }


    /**
     * 构造全量备份请求的第一部分数据
     */
    private static byte[] generateFullBackupDataPart1(long androidId, String authToken, String packageName, String boundary) {
        BackupRequest.BackupData backupData = BackupRequest.BackupData.newBuilder()
                .setPackageName(packageName)
                .build();
        byte[] bytes = BackupRequest.newBuilder()
                .setAndroidId(androidId)
                .setAndroidId2(androidId)
                .setAuthToken(authToken)
                .setRandomUUID(UUID.randomUUID().toString())
                .addBackupData(backupData)
                .setStorageType(3)
                .build().toByteArray();
        StringBuilder part1 = new StringBuilder();
        part1.append("--").append(boundary).append("\r\n");
        part1.append("Content-Type: text/plain\r\n");
        part1.append("\r\n");
        part1.append(Base64.encodeToString(bytes, Base64.NO_WRAP)).append("\r\n");
        byte[] retBytes = part1.toString().getBytes();
        Log.d(TAG, "generateFullBackupDataPart1: retBytes=>" + bytesToHex(retBytes));
        return retBytes;
    }

    /**
     * 构造全量备份请求的第二部分数据
     */
    private static byte[] generateFullBackupDataPart2(byte[] backupData, String boundary) {
        StringBuilder header = new StringBuilder();
        header.append("--").append(boundary).append("\r\n");
        header.append("content-type: application/octet-stream\r\n\r\n");
        byte[] headerBytes = header.toString().getBytes();
        byte[] footerBytes = ("\r\n").getBytes();
        byte[] retData = new byte[headerBytes.length + backupData.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, retData, 0, headerBytes.length);
        System.arraycopy(backupData, 0, retData, headerBytes.length, backupData.length);
        System.arraycopy(footerBytes, 0, retData, headerBytes.length + backupData.length, footerBytes.length);
        Log.d(TAG, "generateFullBackupDataPart2: retBytes=>" + bytesToHex(retData) + " length: " + retData.length);
        Log.d(TAG, "generateFullBackupDataPart2: backupDataLength: " + backupData.length);
        Log.d(TAG, "generateFullBackupDataPart2: retDataLength: " + retData.length);
        return retData;
    }

    /**
     * 构造全量备份请求的第三部分数据
     */
    private static byte[] generateFullBackupDataPart3(String dataSha1DigestBase64String, String boundary) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(boundary).append("\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append("X-Goog-Hash: sha1=" + dataSha1DigestBase64String + "\r\n");
        stringBuilder.append("--").append(boundary).append("--");
        byte[] retBytes = stringBuilder.toString().getBytes();
        Log.d(TAG, "generateFullBackupDataPart3: retBytes=>" + bytesToHex(retBytes));
        return retBytes;
    }

    /**
     * 工具函数,用于打印返回数据时进行字节转换
     */
    public static String bytesToHex(byte[] bytes) {         //字节转十六进制字符函数
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;

            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 工具函数
     * 获取指定长度的随机字符串
     */
    private static String generateRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 工具函数
     * 计算数据的SHA1摘要, 并转换为Base64字符串
     */
    private static String getDataSha1DigestBase64String(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            return Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "getDataSha1DigestBase64String: error", e);
        }
        return null;
    }

    public void setAndroidIdAndAuthToken(long androidId, String authToken) {
        synchronized (BackupHttpRequestUtil.class){
            this.androidId = androidId;
            this.authToken = authToken;
        }
    }
}

