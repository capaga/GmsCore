package org.microg.gms.games.signin.utils;

import android.accounts.Account;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.microg.common.beans.DownloadFileList;
import org.microg.common.beans.ListConfig;
import org.microg.common.beans.OtherConfig;
import org.microg.common.beans.PeopleConfig;
import org.microg.common.beans.PeopleRequest;
import org.microg.common.beans.PeopleResponse;
import org.microg.common.beans.ProfileConfig;
import org.microg.common.beans.ProfileNameConfig;
import org.microg.common.beans.RequestType;
import org.microg.gms.common.AccountManagerUtils;
import org.microg.gms.common.Constants;
import org.microg.gms.games.signin.ImageFileDownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class PeopleUtils {
    private static final String TAG = PeopleUtils.class.getSimpleName();
    private final static String GET_PEOPLE_V2_URL = "https://people-pa.googleapis.com/google.internal.people.v2.InternalPeopleService/GetPeople";
    private final static String NAME = "me";
    private final static String[] LIST_CONFIG =
            {"person.email", "person.name", "person.photo", "person.read_only_profile_info", "person.metadata", "person.age_range_repeated"};
    private final static String UNKNOW_OTHER = "\u0005\u0007\u0001";
    private final static String PROFILE_SYNC_GMS = "profile-sync-gms";
    private final static int REQUEST_TYPE = 2;

    private static final int GRPC_HEADER_LENGTH = 5;


    private static byte[] getPeopleRequest() {
        RequestType requestType_build = new RequestType
                .Builder().level(PeopleUtils.REQUEST_TYPE).build();
        ProfileNameConfig profileConfig_build = new ProfileNameConfig
                .Builder().name(PeopleUtils.PROFILE_SYNC_GMS).build();
        ListConfig listConfig_build = new ListConfig
                .Builder().names(Arrays.asList(PeopleUtils.LIST_CONFIG)).build();
        OtherConfig otherConfig_build = new OtherConfig
                .Builder().config(profileConfig_build).build();
        PeopleConfig peopleConfig_build = new PeopleConfig
                .Builder().config(listConfig_build).other(String.valueOf(UNKNOW_OTHER))
                .type(requestType_build).build();

        PeopleRequest.Builder builder = new PeopleRequest.Builder();
        PeopleRequest peopleRequest = builder.name(PeopleUtils.NAME)
                .config(peopleConfig_build).other_config(otherConfig_build).build();

        byte[] requestBytes = peopleRequest.encode();

        ByteBuffer es = ByteBuffer.allocate(GRPC_HEADER_LENGTH);
        es.put((byte) 0);
        es.putInt(requestBytes.length);
        byte[] result = new byte[requestBytes.length + GRPC_HEADER_LENGTH];

        System.arraycopy(es.array(), 0, result, 0, GRPC_HEADER_LENGTH);
        System.arraycopy(requestBytes, 0, result, es.array().length, requestBytes.length);

        return result;
    }

    public static void startPeopleConfigGrpc(Context context, String token) {
        String userAgent = "com.google.android.gms/%s (Linux; U; Android %s; %s; %s; Build/%s; Cronet/109.0.5414.80) grpc-java-cronet/1.54.0-SNAPSHOT";
        String versionCode = Build.VERSION.RELEASE; // 12
        String localEn = "zh_CN"; //  zh_CN
        String model = Build.MODEL; // 22021211RC
        String buildId = Build.ID; // SKQ1.211006.001

        try {
            Class<?> aClass = Build.class;
            Method getString = aClass.getDeclaredMethod("getString", String.class);
            localEn = (String) getString.invoke(null, "ro.product.locale");
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "startPeopleConfigGrpc: " + e);
        } catch (InvocationTargetException e) {
            Log.d(TAG, "startPeopleConfigGrpc: " + e);
        } catch (IllegalAccessException e) {
            Log.d(TAG, "startPeopleConfigGrpc: " + e);
        }

        String userAgentResult = String.format(userAgent, Constants.GMS_VERSION_CODE, versionCode, localEn, model, buildId);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GET_PEOPLE_V2_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.addRequestProperty("user-agent", userAgentResult);
            connection.addRequestProperty("content-type", "application/grpc");
            connection.addRequestProperty("te", "trailers");

            connection.addRequestProperty("authorization", token);
            connection.addRequestProperty("x-auth-time", "" + new Date().getTime());
            connection.addRequestProperty("grpc-accept-encoding", "gzip");
            OutputStream outputStream = connection.getOutputStream();

            byte[] mains = PeopleUtils.getPeopleRequest();
            outputStream.write(mains);
            outputStream.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                byte[] bytes = readStreamToEnd(connection.getInputStream());
                byte[] result = new byte[bytes.length - GRPC_HEADER_LENGTH];
                System.arraycopy(bytes, GRPC_HEADER_LENGTH, result, 0, bytes.length - GRPC_HEADER_LENGTH);

                saveProfileInfo(context, result);
            }

        } catch (Exception e) {
            Log.d(TAG, "startPeopleConfigGrpc: " + e);
        }
    }

    public static byte[] readStreamToEnd(final InputStream is) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (is != null) {
            final byte[] buff = new byte[1024];
            int read;
            do {
                bos.write(buff, 0, (read = is.read(buff)) < 0 ? 0 : read);
            } while (read >= 0);
            is.close();
        }
        return bos.toByteArray();
    }

    public static void saveProfileInfo(Context context, byte[] bytes) {
        try {
            Log.d(TAG, "saveProfileInfo is start");
            File filesDir = context.getFilesDir();
            File publicFile = new File(filesDir, "/mdisync/shared/datadownload/public");

            if (!publicFile.exists()) {
                publicFile.mkdirs();
            }
            List<String> saveList = new ArrayList<>(5);

            PeopleResponse peopleResponse1 = PeopleResponse.ADAPTER.decode(bytes);
            String url = peopleResponse1.people_response_internal.response_info.config_C.url;
            List<String> strings = ImageFileDownloader.generateImageList(url);
            for (String string : strings) {
                String s = ImageFileDownloader.generateName();
                File file = new File(publicFile, s);
                if (!file.exists()) {
                    file.createNewFile();
                }
                ImageFileDownloader.downloadFile(string, file.getAbsolutePath());
                saveList.add(file.getAbsolutePath());
            }
            Log.d(TAG, "saveProfileInfo is parseFrom");

            DownloadFileList downloadFileList_build = new DownloadFileList.Builder().
                    url_a(saveList.get(0)).url_b(saveList.get(1)).url_c(saveList.get(2))
                    .url_d(saveList.get(3)).url_e(saveList.get(4)).build();

            ProfileConfig.Builder builder = new ProfileConfig.Builder();
            ProfileConfig profileConfig1 = builder.people_response(peopleResponse1)
                    .download_file_list(downloadFileList_build).build();

            byte[] result = profileConfig1.encode();
            Account[] accountsByType = AccountManagerUtils.getInstance(context).getAccountsByType(Constants.ACCOUNT_TYPE);

            String accountEmailBytes = peopleResponse1.people_response_internal.response_info.config_D.account_email;
            for (int i = 0; i < accountsByType.length; i++) {
                if (accountsByType[i].name.equals(accountEmailBytes)) {
                    File save = new File(filesDir, "/managed/mdisync/" + i + "/profilesync/public");
                    if (!save.exists()) {
                        save.mkdirs();
                    }
                    File profile_info = new File(save, "profile_info.pb");
                    if (!profile_info.exists()) {
                        profile_info.createNewFile();
                    }
                    writeFile(profile_info.getAbsolutePath(), result);
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "saveProfileInfo: " + e);
        }
    }

    public static void writeFile(String filePath, byte[] bytes) {
        try {
            FileOutputStream fo = new FileOutputStream(filePath);
            fo.write(bytes);
            fo.close();
        } catch (Exception e) {
            Log.d(TAG, "writeFile: " + e);
        }
    }
}
