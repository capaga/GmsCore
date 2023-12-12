package org.microg.gms.games.signin.utils;


import android.util.Log;

import org.microg.common.beans.NamePack;
import org.microg.common.beans.NameSigner;
import org.microg.common.beans.RequestClients;
import org.microg.gms.common.Constants;
import org.microg.gms.common.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class GrpcUtils {
    private static final String TAG = GrpcUtils.class.getSimpleName();
    public static String GET_DISPLAY_BRAND = "https://clientauthconfig.googleapis.com:443/google.identity.clientauthconfig.v1.ClientAuthConfig/GetDisplayBrand";
    private static final int GRPC_HEADER_LENGTH = 5;
    public static byte[] getClientRequest(String PackageName, String singer) {
        RequestClients.Builder builder = new RequestClients.Builder();
        NameSigner build = new NameSigner.Builder().singer((singer)).PackageName(PackageName).build();
        NamePack namePackBuild = new NamePack.Builder().builder_(build).build();

        RequestClients requestClients = builder.a(namePackBuild).build();

        byte[] requestClientsBytes = requestClients.encode();

        ByteBuffer es = ByteBuffer.allocate(GRPC_HEADER_LENGTH);
        es.put((byte) 0);
        es.putInt(requestClientsBytes.length);

        byte[] result = new byte[requestClientsBytes.length + GRPC_HEADER_LENGTH];
        System.arraycopy(es.array(), 0, result, 0, GRPC_HEADER_LENGTH);

        System.arraycopy(requestClientsBytes, 0, result, es.array().length, requestClientsBytes.length);
        return result;

    }


    public static void startClientConfigGrpc(String url, String packAgeName, String singer, HttpCallback callback) {
        try {
            Log.d(TAG, "startClientConfigGrpc: " + url + " packAgeName:" +  packAgeName + " singer:" + singer);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.addRequestProperty("User-Agent", "grpc-java-okhttp/1.44.0-SNAPSHOT");
            connection.addRequestProperty("Content-Type", "application/grpc");
            connection.addRequestProperty("Te", "trailers");
            connection.addRequestProperty("X-Goog-Api-Key", "AIzaSyAP-gfH3qvi6vgHZbSYwQ_XHqV_mXHhzIk");
            connection.addRequestProperty("X-Android-Package", "com.google.android.gms");
            connection.addRequestProperty("X-Android-Cert", Constants.GMS_PACKAGE_SIGNATURE_SHA1);
            connection.addRequestProperty("Grpc-Accept-Encoding", "gzip");
            connection.addRequestProperty("Grpc-Timeout", String.valueOf(1000 * 10));
            OutputStream outputStream = connection.getOutputStream();
            byte[] mains = GrpcUtils.getClientRequest(packAgeName.trim(), singer.trim());
            outputStream.write(mains);
            outputStream.close();
            Log.d(TAG, "startClientConfigGrpc: " + connection.getResponseCode());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String error = connection.getResponseMessage();
                try {
                    error = new String(Utils.readStreamToEnd(connection.getErrorStream()));
                } catch (IOException e) {
                    // Ignore
                }
                callback.onError(error);

            }
            byte[] bytes = Utils.readStreamToEnd(connection.getInputStream());
            Log.d(TAG, "startClientConfigGrpc date length:" + bytes.length);
            if (bytes == null || bytes.length == 0) {
                Log.d(TAG, "startClientConfigGrpc: 222");
                callback.onError("http error");
            } else {
                callback.onSuccess(bytes);
            }


        } catch (IOException e) {
            Log.e(TAG, "startClientConfigGrpc: " + e);
        }

    }

    public interface HttpCallback {
        void onSuccess(byte[] ds);

        void onError(String msg);
    }

}
