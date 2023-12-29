package org.microg.gms.people;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import org.json.JSONException;
import org.json.JSONObject;
import org.microg.gms.people.converter.BaseConverter;
import org.microg.gms.people.converter.IBaseConverter;
import org.microg.gms.people.converter.buzgConverter;
import org.microg.gms.people.converter.buzhConverter;
import org.microg.gms.people.converter.buzlConverter;
import org.microg.gms.people.converter.buzmConverter;
import org.microg.gms.people.converter.buzoConverter;
import org.microg.gms.people.converter.buzzConverter;
import org.microg.gms.people.converter.bvaaConverter;
import org.microg.gms.people.converter.bvabConverter;
import org.microg.gms.people.converter.bvafConverter;
import org.microg.gms.people.converter.bvaiConverter;
import org.microg.gms.people.converter.bvajConverter;
import org.microg.gms.people.converter.bvakConverter;
import org.microg.gms.people.converter.bvalConverter;
import org.microg.gms.people.converter.bvamConverter;
import org.microg.gms.people.converter.bvapConverter;
import org.microg.gms.people.converter.bvatConverter;
import org.microg.gms.people.converter.bvbfConverter;
import org.microg.gms.people.converter.bvbgConverter;
import org.microg.gms.people.converter.bvbmConverter;
import org.microg.gms.people.converter.bvbnConverter;
import org.microg.gms.people.converter.bvbpConverter;
import org.microg.gms.people.converter.bvbqConverter;
import org.microg.gms.people.converter.multiConverter;
import org.microg.gms.people.sync.HeaderClientInterceptor;
import org.microg.gms.profile.Build;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ContactSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = ContactSyncAdapter.class.getSimpleName();
    static cehl.Builder gmsVersion;
    ManagedChannel mchannel;
    Uri RawConcts;
    Uri DataConcts;
    ContentResolver resolver = getContext().getContentResolver();
    String[] dataFields = new String[]{"data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10", "data11", "data12", "data13", "data14", "data15", "data_sync1", "data_sync2", "data_sync3", "data_sync4"};
    String[] personInfoStub = new String[]{"person.about", "person.address", "person.birthday", "person.calendar", "person.client_data", "person.contact_group_membership", "person.email", "person.event", "person.external_id", "person.file_as", "person.gender", "person.im", "person.interest", "person.language", "person.name", "person.nickname", "person.occupation", "person.organization", "person.other_keyword", "person.phone", "person.relation", "person.sip_address", "person.user_defined", "person.website"};
    String[] personInfo = new String[]{"person.about", "person.address", "person.birthday", "person.calendar", "person.client_data", "person.contact_group_membership", "person.email", "person.event", "person.external_id", "person.file_as", "person.gender", "person.im", "person.interest", "person.language", "person.name", "person.nickname", "person.occupation", "person.organization", "person.other_keyword", "person.phone", "person.relation", "person.sip_address", "person.user_defined", "person.website","person.photo","person.metadata"};

    HashSet<String> groupSourceid = new HashSet<>();
    String currentGpsourceid;

    private Context context;
    static HashMap<Class, IBaseConverter> converter = new HashMap<Class, IBaseConverter>(){{
        put(buzg.class,new buzgConverter());
        put(buzh.class,new buzhConverter());
        put(buzl.class,new buzlConverter());
        put(buzm.class,new buzmConverter());
        put(buzo.class,new buzoConverter());
        put(buzz.class,new buzzConverter());
        put(bvaa.class,new bvaaConverter());
        put(bvab.class,new bvabConverter());
        put(bvaf.class,new bvafConverter());
        put(bvai.class,new bvaiConverter());
        put(bvaj.class,new bvajConverter());
        put(bvak.class,new bvakConverter());
        put(bval.class,new bvalConverter());
        put(bvam.class,new bvamConverter());
        put(bvap.class,new bvapConverter());
        put(bvat.class,new bvatConverter());
        put(bvbf.class,new bvbfConverter());
        put(bvbg.class,new bvbgConverter());
        put(bvbm.class,new bvbmConverter());
        put(bvbn.class,new bvbnConverter());
        put(bvbp.class,new bvbpConverter());
        put(bvbq.class,new bvbqConverter());
    }};

    static {
        cefo.Builder cefo0 = cefo.newBuilder();
        cefo0.setA("GMS FSA2");
        cefo0.setB("21.24.23 (190408-396046673)");
        gmsVersion = cehl.newBuilder();
        gmsVersion.setA(cefo0);
        cehk.Builder cehk0 = cehk.newBuilder();
        cehk0.setA(true);
        gmsVersion.setB(cehk0.build());
    }

    public ContactSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @SuppressLint("Recycle")
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync: ");
        RawConcts = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", account.name).appendQueryParameter("account_type", account.type).appendQueryParameter("caller_is_syncadapter", "true").build();
        DataConcts = ContactsContract.Data.CONTENT_URI.buildUpon().appendQueryParameter("account_name", account.name).appendQueryParameter("account_type", account.type).appendQueryParameter("caller_is_syncadapter", "true").build();
        Cursor cursorGpsourceid = resolver.query(appendAccount(ContactsContract.Groups.CONTENT_URI,account,true),
                new String[]{"_id", "sourceid"},"data_set IS NULL AND should_sync !=0",null,null);
        assert cursorGpsourceid != null;
        int sourceIndex = cursorGpsourceid.getColumnIndex("sourceid");
        while (cursorGpsourceid.moveToNext()) {
            String gpscid = cursorGpsourceid.getString(sourceIndex);
            if(!TextUtils.isEmpty(gpscid)){
                currentGpsourceid = gpscid;
                groupSourceid.add(gpscid);
            }

        }
        cursorGpsourceid.close();

        //-----
        ArrayList<ContentValues> insertlist = new ArrayList<>();
        ArrayList<ContentValues> updatelist = new ArrayList<>();
        ArrayList<ContentValues> delList = new ArrayList<>();
        ArrayList<ContentValues> nativeUserData = new ArrayList<>();
        getExistPersonData(account, "data_set IS NULL AND (sourceid IS NULL OR dirty != 0 OR deleted != 0)", null, nativeUserData,true);

        for(ContentValues userinfo : nativeUserData){
            if (userinfo.getAsLong("deleted")>0) {
                delList.add(userinfo);
            }else if (userinfo.getAsString("sourceid") == null){
                insertlist.add(userinfo);
            }else if(userinfo.getAsLong("dirty")>0){
                updatelist.add(userinfo);
            }
        }
        //insert
        if(insertlist.size()>0){
            cefl.Builder cefl0 = buildRequestForInsert(insertlist,account);
            try {
                cefn insertRes = getInternalPeopleServiceStub(account).bulkInsertContacts(cefl0.build());

                for(cefm cefm0 : insertRes.getAList()){
                    bvaw bvaw0 = cefm0.getA().getA();
                    ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                    String sourceid = null;
                    for(bvbo bvbo0 : bvaw0.getCSmall().getD().getAList()){
                        sourceid = bvbo0.getC();
                    }
                    bvaw2Operations(account,syncResult,operations,bvaw0,sourceid);
                    if(operations.size()>0){
                        resolver.applyBatch(ContactsContract.AUTHORITY, operations);
                    }

                }
            }catch (Exception ignored){
            }
        }
        //deleted
        if(delList.size()>0){
            deletedUpload(account, delList);
        }
        Log.d(TAG, "onPerformSync update size: " + updatelist.size());
        //update
        if(updatelist.size()>0){

            long currentId = updatelist.get(0).getAsLong("_id");
            ArrayList<bvaw> bvawArrayList = new ArrayList<>();
            bvaw.Builder bvaw0 = bvaw.newBuilder();
            ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            for(ContentValues updateValue : updatelist){
                if(currentId != updateValue.getAsLong("_id")){
                    bvawArrayList.add(bvaw0.build());

                    currentId = updateValue.getAsLong("_id");
                    bvaw0 = bvaw.newBuilder();
                }

                operations.add(ContentProviderOperation.newUpdate(RawConcts).withValue(ContactsContract.RawContacts.DIRTY,0).withSelection(ContactsContract.RawContacts._ID + " = " +updateValue.getAsLong("_id"),null).build());
                contentvalues2bvaw(updateValue,bvaw0);
            }
            bvawArrayList.add(bvaw0.build());

            for (bvaw bvaw1 : bvawArrayList){
                ceig.Builder ceig0 = buildRequestForUpdate(bvaw1);
                try {
                    ceih res = getInternalPeopleServiceStub(account).updatePerson(ceig0.build());
                    for (String sourceid : atoz_b(res.getA().getA())) {
                        bvaw2Operations(account,syncResult,operations,res.getA().getA(),sourceid);
                    }

                    if(operations.size()>0){
                        try {
                            resolver.applyBatch(ContactsContract.AUTHORITY,operations);
                        } catch (Exception e) {
                            Log.e("ContactSyncAdapter", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                } catch (Exception e) {
                    Log.e("ContactSyncAdapter", Objects.requireNonNull(e.getMessage()));
                }
            }
        }
        //sync
        int count=2;
        try {
            while (count > 1 ){
                ceia ckfe0 = buildRequest(account);
                cehh res = getInternalPeopleServiceStub(account).syncPeople(ckfe0);
                count = updateData(res,account,syncResult);
                Log.d(TAG, "onPerformSync count: " + count);
//                syncResult.stats.numUpdates += count;
            }
        } catch (OperationApplicationException e) {
            Log.e(TAG,"applyBatch error");
        } catch (io.grpc.StatusRuntimeException e){
            Log.e(TAG,"grpc 请求异常");
        }catch (Exception e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    @NonNull
    private ceig.Builder buildRequestForUpdate(bvaw bvaw1) {
        ceig.Builder ceig0 = ceig.newBuilder();
        ceig0.setA(bvaw1.getBSmall());
        ceig0.setB(bvaw1);
        ckex.Builder ckex0 = ckex.newBuilder();
        ckex0.addAllA(Arrays.asList(personInfoStub));
        ceig0.setC(ckex0.build());
        ceig0.setD(2);
        ceig0.setE(false);
//                ceig0.addH()
        cegl.Builder cegl0 = cegl.newBuilder();
        cegl0.setE(1);
        cegg.Builder cegg0 = cegg.newBuilder();
        cegg0.addA(8);
        cegl0.setC(cegg0);
        cegl0.setD(gmsVersion);
        cehs.Builder cehs0 = cehs.newBuilder();
        ckex.Builder ckex1 = ckex.newBuilder();
        ckex1.addAllA(Arrays.asList(personInfo));
        cehs0.setA(ckex1);
        cegl0.setB(cehs0);
        cehj.Builder cehj0 = cehj.newBuilder();
        cehm.Builder cehm0 = cehm.newBuilder();
        cehm0.setA(2);
        cehj0.setB(cehm0); //
        cehj0.addC(3);
        cegl0.setF(cehj0);
        ceig0.setF(cegl0);
        return ceig0;
    }

    private void deletedUpload(Account account, ArrayList<ContentValues> delList) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        cefx.Builder cefx0 = cefx.newBuilder();
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<String> sourceidList = new ArrayList<>();
        for(ContentValues delValue : delList){
            if(TextUtils.isEmpty(delValue.getAsString("sourceid")) && TextUtils.isEmpty(delValue.getAsString("sync3"))){
                if(!ids.contains(delValue.getAsLong("_id"))){
                    ids.add(delValue.getAsLong("_id"));
                    operations.add(ContentProviderOperation.newDelete(appendAccount(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, delValue.getAsLong("_id")), account,true)).build());
                }
            }else {
                if(!sourceidList.contains(delValue.getAsString("sourceid"))){
                    sourceidList.add(delValue.getAsString("sourceid"));
                    operations.add(ContentProviderOperation.newDelete(appendAccount(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, delValue.getAsLong("_id")), account,true)).build());
                }

            }
        }
        if(sourceidList.size()>0){
            cefx0.addAllA(sourceidList);
            cefx0.setB(gmsVersion);
            cmcp.Builder cmcp0 = cmcp.newBuilder();
            cmcr.Builder cmcr0 = cmcr.newBuilder();
            cmcr0.setD(6);
            cmcl.Builder cmcl0 = cmcl.newBuilder();
            cmcl0.setB(Build.MANUFACTURER + " - " + Build.MODEL);
            cmcr0.setOneofField0(cmcl0);
            cmcr0.setD(6);
            cmcp0.setB(cmcr0);
            cefx0.setC(cmcp0);

            try {
                cefy cefy0 = getInternalPeopleServiceStub(account).deletePeople(cefx0.build());
                if(operations.size()>0){
                    resolver.applyBatch(ContactsContract.AUTHORITY, operations);
                }
            } catch (Exception ignored){
                Log.i("ContactSyncAdapter","deleted people upload failed");
            }
        }

    }

    @NonNull
    private cefl.Builder buildRequestForInsert(ArrayList<ContentValues> insertlist,Account account) {
        ArrayList<cefk> cefks = new ArrayList<>();
        long currentId = insertlist.get(0).getAsLong("_id");
        Long currentSync2 = checkSync2(insertlist.get(0), currentId);
        bvaw.Builder bvaw0 = bvaw.newBuilder();
        for(ContentValues insertValue : insertlist){
            if(insertValue.getAsString("mimetype").equals("vnd.android.cursor.item/group_membership") && TextUtils.isEmpty(insertValue.getAsString("group_sourceid"))){
                ContentValues contentValues0 = new ContentValues();
                //CreateGpReq(account, insertValue,contentValues0);
                contentValues0.put("group_sourceid", currentGpsourceid);
                resolver.update(DataConcts, contentValues0, "_id = ?", new String[]{String.valueOf(insertValue.getAsLong("data_id"))});
                //resolver.update(DataConcts, contentValues0, "_id = ?", new String[]{String.valueOf(currentId)});
            }
            if(currentId != insertValue.getAsLong("_id")){
                if(bvaw0.getDBigList().size()==0){
                    ContentValues gpid = checkGroupMembership(currentId);
                    bvaw0.addDBig((buzo) Objects.requireNonNull(converter.get(buzo.class)).toProtobuf(gpid, null));
                }

                cefk.Builder cefk0 = cefk.newBuilder();
                cefk0.setA(bvaw0);
                if(currentSync2 != null){
                    cefk0.setB(currentSync2);
                }
                cefks.add(cefk0.build());

                currentId = insertValue.getAsLong("_id");
                currentSync2 = checkSync2(insertValue, currentId);
                bvaw0 = bvaw.newBuilder();
            }
            contentvalues2bvaw(insertValue,bvaw0);
        }
        if(bvaw0.getDBigList().size()==0){
            ContentValues gpid = checkGroupMembership(currentId);
            bvaw0.addDBig((buzo) Objects.requireNonNull(converter.get(buzo.class)).toProtobuf(gpid, null));
        }
        cefk.Builder cefk0 = cefk.newBuilder();
        cefk0.setA(bvaw0);
        if(currentSync2 != null){
            cefk0.setB(currentSync2);
        }
        cefks.add(cefk0.build());

        cefl.Builder cefl0 = cefl.newBuilder();
        cefl0.addAllA(cefks);

        cegl.Builder cegl0 = cegl.newBuilder();
        cegl0.setE(1);
        cegg.Builder cegg0 = cegg.newBuilder();
        cegg0.addA(8);
        cegl0.setC(cegg0.build());

        cegl0.setD(gmsVersion);
        cehs.Builder cehs0 = cehs.newBuilder();
        ckex.Builder ckex0 = ckex.newBuilder();
        ckex0.addAllA(Arrays.asList(personInfo));
        cehs0.setA(ckex0.build());
        cegl0.setB(cehs0.build());
        cehj.Builder cehj0 = cehj.newBuilder();
        cehm.Builder cehm0 = cehm.newBuilder();
        cehm0.setA(2);
        cehj0.setB(cehm0.build());
        cehj0.addC(3);
        cegl0.setF(cehj0.build());

        cefl0.setC(cegl0.build());
        ckex.Builder ckex1 = ckex.newBuilder();
        ckex1.addAllA(Arrays.asList(personInfoStub));

        cefl0.setB(ckex1.build());
        return cefl0;
    }

    private void CreateGpReq(Account account, ContentValues insertValue,ContentValues contentValues0) {
        cefp.Builder cefp0 = cefp.newBuilder();
        cmdm.Builder cmdm0 = cmdm.newBuilder();
        cmdj.Builder cmdj0 = cmdj.newBuilder();
        cmdl.Builder cmdl0 = cmdl.newBuilder();
        if (!TextUtils.isEmpty(insertValue.getAsString("title"))) {
            cmdl0.setB(insertValue.getAsString("title"));
        }

        cmdj0.setC(cmdl0);
        cmds.Builder cmds0 = cmds.newBuilder();
        cmds0.setB(1);
        cmdj0.setF(cmds0);
        cmdm0.setC(cmdj0);

        cefp0.addA(cmdm0);
        cegt.Builder cegt0 = cegt.newBuilder();
        cegt0.addA(1);
        cefp0.setB(cegt0);
        cefp0.setC(gmsVersion);
        try {
            cefq cefq0 = getInternalPeopleServiceStub(account).createContactGroups(cefp0.build());
            contentValues0.put("group_sourceid", cefq0.getA(0).getB().getB());
            insertValue.put("group_sourceid", cefq0.getA(0).getB().getB());
        } catch (Exception e) {
            Log.e("ContactSyncAdapter", Objects.requireNonNull(e.getMessage()));
        }
    }

    private ContentValues checkGroupMembership(long currentId) {
        Cursor cursor = resolver.query(DataConcts, null, "mimetype = ? AND raw_contact_id = ?", new String[]{"vnd.android.cursor.item/group_membership", String.valueOf(currentId)}, null);
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/group_membership");
        assert cursor != null;
        if(cursor.getCount() == 0){
            contentValues0.put("group_sourceid", currentGpsourceid);
            contentValues0.put(ContactsContract.Data.RAW_CONTACT_ID, currentId);
            resolver.insert(DataConcts, contentValues0);
            cursor.close();
            return contentValues0;
        }else {
            cursor.moveToFirst();
            @SuppressLint("Range") long gpsourceId = cursor.getLong(cursor.getColumnIndex("group_sourceid"));
            contentValues0.put("group_sourceid", gpsourceId);
            cursor.close();
            return contentValues0;
        }
    }

    private void contentvalues2bvaw2(ContentValues insertValue, bvaw.Builder bvaw0) {
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("group_sourceid", "6");
        bvaw0.addDBig((buzo) Objects.requireNonNull(converter.get(buzo.class)).toProtobuf(contentValues0,null));
    }

    @Nullable
    private Long checkSync2(ContentValues insertValue, long currentId) {
        String sync2Str = insertValue.getAsString("sync2");
        Long currentSync2 = TextUtils.isEmpty(sync2Str) ? null : Long.parseLong(sync2Str,16);
        if(currentSync2 == null || insertValue.getAsString("sourceid") != null){
            currentSync2 = (long)(new SecureRandom().nextLong() & 0x7FFFFFFF87FFFFFFL | 0x8000000L);
            ContentValues contentValues0 = new ContentValues();
            contentValues0.put("sync2", Long.toHexString(currentSync2));
            resolver.update(RawConcts, contentValues0, "_id = ?", new String[]{String.valueOf(currentId)});
        }
        return currentSync2;
    }

    private void contentvalues2bvaw(ContentValues insertlist, bvaw.Builder bvaw0) {
//        bvaw.Builder bvaw0 = bvaw.newBuilder();

        String sourceid = TextUtils.isEmpty(insertlist.getAsString("sourceid"))?null:insertlist.getAsString("sourceid");
        String sourceidTmp = sourceid == null ? null : "c" + Long.parseLong(sourceid, 16);
        if(!bvaw0.hasBSmall() && sourceidTmp!=null){
            bvaw0.setBSmall(sourceidTmp);
        }


        bvbe.Builder bvbe0 = bvbe.newBuilder();
        bvah.Builder bvah0 = bvah.newBuilder();
        bvbo.Builder bvbo0 = bvbo.newBuilder();
        bvbo0.setB(2);
        if(!TextUtils.isEmpty(insertlist.getAsString("sync2"))){
            bvbo0.setF(insertlist.getAsString("sync2"));
        }
        if(sourceid!=null){
            bvbo0.setC(sourceid);//
        }
        if(!TextUtils.isEmpty(insertlist.getAsString("sync3"))){
            bvbo0.setE(insertlist.getAsLong("sync3"));
        }


        bvah0.addA(bvbo0.build());
        bvbe0.setD(bvah0.build());
        bvbe0.setB(2);
        if(sourceid!=null){
            bvbe0.addC(Long.parseLong(sourceid,16));
        }
        bvaw0.setCSmall(bvbe0.build());

        //--------- data
        if(insertlist.getAsString("mimetype").equals("vnd.android.cursor.item/group_membership")){
            bvaw0.addDBig((buzo) Objects.requireNonNull(converter.get(buzo.class)).toProtobuf(insertlist,sourceid));
        }

        MessageLite msg = contentvalue2Proto(insertlist, sourceid);
        if(msg==null){
            return;
        }
        switch (msg.getClass().getName()){
            case "org.microg.gms.people.buzg":
                bvaw0.addI((buzg) msg);
                break;
            case "org.microg.gms.people.buzh":
                bvaw0.addO((buzh) msg);
                break;
            case "org.microg.gms.people.buzl":
                bvaw0.addK((buzl) msg);
                break;
            case "org.microg.gms.people.buzm":
                bvaw0.addY((buzm) msg);
                break;
            case "org.microg.gms.people.buzn":
                bvaw0.addBBig((buzn) msg);
                break;
            case "org.microg.gms.people.buzz":
                bvaw0.addG((buzz) msg);
                break;
            case "org.microg.gms.people.bvaa":
                bvaw0.addR((bvaa)msg);
                break;
            case "org.microg.gms.people.bvab":
                bvaw0.addA((bvab)msg);
                break;
            case "org.microg.gms.people.bvaf":
                bvaw0.addT((bvaf)msg);
                break;
            case "org.microg.gms.people.bvag":
                bvaw0.addF((bvag) msg);
                break;
            case "org.microg.gms.people.bvai":
                bvaw0.addQ((bvai) msg);
                break;
            case "org.microg.gms.people.bvaj":
                bvaw0.addV((bvaj) msg);
                break;
            case "org.microg.gms.people.bvak":
                bvaw0.addZ((bvak) msg);
                break;
            case "org.microg.gms.people.bvam":
                bvaw0.addDSmall((bvam) msg);
                break;
            case "org.microg.gms.people.bvap":
                bvaw0.addL((bvap) msg);
                break;
            case "org.microg.gms.people.bvaq":
                bvaw0.addN((bvaq) msg);
                break;
            case "org.microg.gms.people.bvat":
                bvaw0.addM((bvat) msg);
                break;
            case "org.microg.gms.people.bvau":
                bvaw0.addX((bvau) msg);
                break;
            case "org.microg.gms.people.bvbf":
                bvaw0.addH((bvbf) msg);
                break;
            case "org.microg.gms.people.bvbg":
                bvaw0.addE((bvbg) msg);
                break;
            case "org.microg.gms.people.bvbm":
                bvaw0.addP((bvbm) msg);
                break;
            case "org.microg.gms.people.bvbn":
                bvaw0.addU((bvbn) msg);
                break;
            case "org.microg.gms.people.bvbp":
                bvaw0.addS((bvbp) msg);
                break;
            case "org.microg.gms.people.bvbq":
                bvaw0.addJ((bvbq) msg);
                break;
        }
//        if(msg instanceof buzg){
//            bvaw0.addI((buzg) msg);
//        }
//        if(msg instanceof buzh){
//            bvaw0.addO((buzh) msg);
//        }
//        if(msg instanceof buzl){
//            bvaw0.addK((buzl) msg);
//        }
//        if(msg instanceof buzm){
//            bvaw0.addY((buzm) msg);
//        }
//        if(msg instanceof buzn){
//            bvaw0.addBBig((buzn) msg);
//        }
//        if(msg instanceof buzz){
//            bvaw0.addG((buzz) msg);
//        }
//        if(msg instanceof bvaa){
//            bvaw0.addR((bvaa)msg);
//        }
//        if(msg instanceof bvab){
//            bvaw0.addA((bvab)msg);
//        }
//        if(msg instanceof bvaf){
//            bvaw0.addT((bvaf)msg);
//        }
//        if(msg instanceof bvag){
//            bvaw0.addF((bvag) msg);
//        }
//        if (msg instanceof bvai) {
//            bvaw0.addQ((bvai) msg);
//        }
//        if (msg instanceof bvaj) {
//            bvaw0.addV((bvaj) msg);
//        }
//        if (msg instanceof bvak) {
//            bvaw0.addZ((bvak) msg);
//        }
//        if (msg instanceof bvam) {
//            bvaw0.addDSmall((bvam) msg);
//        }
//        bvaw0.addL((bvap) msg);
//        bvaw0.addN((bvaq) msg);
//        bvaw0.addM((bvat) msg);
//        bvaw0.addX((bvau) msg);
//        bvaw0.addH((bvbf) msg);
//        bvaw0.addE((bvbg) msg);
//        bvaw0.addP((bvbm) msg);
//        bvaw0.addU((bvbn) msg);
//        bvaw0.addS((bvbp) msg);
//        bvaw0.addJ((bvbq) msg);
//        return bvaw0.build();

    }

    private static MessageLite contentvalue2Proto(ContentValues insertlist, String sourceid) {
        String s1 = insertlist.getAsString("mimetype");
        int v = 0;
        switch(s1.hashCode()) {
            case 0x98E57A2C: {
                v = s1.equals("vnd.com.google.cursor.item/contact_file_as") ? 17 : -1;
                break;
            }
            case 0xA272C504: {
                if(!s1.equals("vnd.android.cursor.item/email_v2")) {
                    v = -1;
                }

                break;
            }
            case 0xB0CDE9D6: {
                v = s1.equals("vnd.android.cursor.item/contact_event") ? 1 : -1;
                break;
            }
            case 0xB80B32E6: {
                v = s1.equals("vnd.com.google.cursor.item/contact_external_id") ? 18 : -1;
                break;
            }
            case 0xBFAC5810: {
                v = s1.equals("vnd.android.cursor.item/name") ? 11 : -1;
                break;
            }
            case -1079210633: {
                v = s1.equals("vnd.android.cursor.item/note") ? 5 : -1;
                break;
            }
            case 0xCEA41BF5: {
                v = s1.equals("vnd.com.google.cursor.item/contact_misc") ? 23 : -1;
                break;
            }
            case 0xDC29F784: {
                v = s1.equals("vnd.android.cursor.item/postal-address_v2") ? 12 : -1;
                break;
            }
            case 0xEC04F1B2: {
                v = s1.equals("vnd.com.google.cursor.item/contact_extended_property") ? 21 : -1;
                break;
            }
            case 3430506: {
                v = s1.equals("vnd.android.cursor.item/sip_address") ? 10 : -1;
                break;
            }
            case 94070761: {
                v = s1.equals("vnd.com.google.cursor.item/contact_hobby") ? 15 : -1;
                break;
            }
            case 0x1B3458F6: {
                v = s1.equals("vnd.android.cursor.item/website") ? 13 : -1;
                break;
            }
            case 0x28C7A9F2: {
                v = s1.equals("vnd.android.cursor.item/phone_v2") ? 7 : -1;
                break;
            }
            case 689862072: {
                v = s1.equals("vnd.android.cursor.item/organization") ? 6 : -1;
                break;
            }
            case 905843021: {
                v = s1.equals("vnd.android.cursor.item/photo") ? 8 : -1;
                break;
            }
            case 950831081: {
                v = s1.equals("vnd.android.cursor.item/im") ? 3 : -1;
                break;
            }
            case 1238509849: {
                v = s1.equals("vnd.com.google.cursor.item/contact_user_defined_field") ? 16 : -1;
                break;
            }
            case 0x4F6EDDE1: {
                v = s1.equals("vnd.com.google.cursor.item/contact_language") ? 19 : -1;
                break;
            }
            case 0x54088D01: {
                v = s1.equals("vnd.android.cursor.item/relation") ? 9 : -1;
                break;
            }
            case 0x5749A772: {
                v = s1.equals("vnd.com.google.cursor.item/contact_calendar_link") ? 14 : -1;
                break;
            }
            case 0x574DEF9B: {
                v = s1.equals("vnd.android.cursor.item/group_membership") ? 2 : -1;
                break;
            }
            case 0x61811FA6: {
                v = s1.equals("vnd.com.google.cursor.item/contact_jot") ? 22 : -1;
                break;
            }
            case 0x6CFD03C3: {
                v = s1.equals("vnd.android.cursor.item/identity") ? 20 : -1;
                break;
            }
            case 2034973555: {
                v = s1.equals("vnd.android.cursor.item/nickname") ? 4 : -1;
                break;
            }
            default: {
                v = -1;
            }
        }

        Class class0 = null;
        switch(v) {
            case 0: {
                class0 = buzz.class;
                break;
            }
            case 1: {
                Log.w("FSA2_DataTypeConverters", "@getDataType: Use either fromCp2Events or fromCp2Birthdays");
                if("3".equals(insertlist.getAsString("data2"))){
                    class0 = buzl.class;
                }else {
                    class0 = bvaa.class;
                }

                break;
            }
            case 2: {
                class0 = bval.class;
                break;
            }
            case 3: {
                class0 = bvai.class;
                break;
            }
            case 4: {
                class0 = bvap.class;
                break;
            }
            case 5: {
                class0 = buzg.class;
                break;
            }
            case 6: {
                class0 = bvat.class;
                break;
            }
            case 7: {
                class0 = bvbf.class;
                break;
            }
            case 8: {
                class0 = bvbg.class;
                break;
            }
            case 9: {
                class0 = bvbm.class;
                break;
            }
            case 10: {
                class0 = bvbn.class;
                break;
            }
            case 11: {
                class0 = bvam.class;
                break;
            }
            case 12: {
                class0 = buzh.class;
                break;
            }
            case 13: {
                class0 = bvbq.class;
                break;
            }
            case 14: {
                class0 = buzm.class;
                break;
            }
            case 15: {
                class0 = bvaj.class;
                break;
            }
            case 16: {
                class0 = bvbp.class;
                break;
            }
            case 17: {
                class0 = bvaf.class;
                break;
            }
            case 18: {
                class0 = bvab.class;
                break;
            }
            case 19: {
                class0 = bvak.class;
            }
        }
        if(class0!=null){
            return Objects.requireNonNull(converter.get(class0)).toProtobuf(insertlist, sourceid);
        }
        return null;
    }

    @SuppressLint("Range")
    private int updateData(cehh res, Account account, SyncResult syncResult) throws RemoteException, OperationApplicationException {
        Log.d(TAG, "updateData: ");
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        //更新syncstate
        String syncState = TextUtils.isEmpty(res.getB()) ? res.getC() : res.getB();
        if (!TextUtils.isEmpty(syncState)) {
            //判断provider syncstate的条数是否大于0,如果不大于0插入一条空数据
            Cursor cursor1 = resolver.query(ContactsContract.SyncState.CONTENT_URI, new String[]{"data"}, "account_name=? AND account_type=?", new String[]{account.name, account.type}, null);
            if (cursor1 != null && cursor1.getCount() > 0) {
                cursor1.close();
            } else {
                assert cursor1 != null;
                cursor1.close();
                ContentValues contentValues0 = new ContentValues();
                contentValues0.put("account_name", account.name);
                contentValues0.put("account_type", account.type);
                contentValues0.put("data", atid.getDefaultInstance().toByteArray());
                resolver.insert(ContactsContract.SyncState.CONTENT_URI, contentValues0);
            }

            atid.Builder atid1 = atid.newBuilder();
            atid1.setB(syncState);
            atid1.setC(atid.getDefaultInstance().getC());

            ContentValues contentValues0 = new ContentValues();
            contentValues0.put("data", atid1.build().toByteArray());
//            resolver.update(ContactsContract.SyncState.CONTENT_URI,contentValues0,"account_name=? AND account_type=?", new String[]{account.name, account.type});
            contentProviderOperations.add(ContentProviderOperation.newUpdate(ContactsContract.SyncState.CONTENT_URI).withValues(contentValues0).withSelection("account_name=? AND account_type=?", new String[]{account.name, account.type}).build());
            resolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        }

        Log.d(TAG, "updateData res size: " + res.getAList().size());
        for (bvaw bvaw0 : res.getAList()) {
            contentProviderOperations.clear();
            for (String sourceid : atoz_b(bvaw0)) {
                bvaw2Operations(account, syncResult, contentProviderOperations, bvaw0, sourceid);
            }
            if(contentProviderOperations.size()>0){
                try {
                    resolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
                } catch (Exception e) {
                    Log.w(TAG, "updateData applyBatch error: ", e);
                    return 0;
                }
            }
        }
        Log.d(TAG, "updateData: " + contentProviderOperations.size());
        return contentProviderOperations.size();
    }

    @SuppressLint("Range")
    private void bvaw2Operations(Account account, SyncResult syncResult, ArrayList<ContentProviderOperation> contentProviderOperations, bvaw bvaw0, String sourceid) {
        boolean shouldDel = true;
        long _id = -1;
        long version = -1;
        long starred = -1;
        if(TextUtils.isEmpty(sourceid)){
            Log.e("ContactSyncAdapter","sourceid is null");
            return;
        }
//                Cursor existPerson = resolver.query(ContactsContract.RawContactsEntity.CONTENT_URI.buildUpon().appendQueryParameter("account_name",account.name).appendQueryParameter("account_type",account.type).appendQueryParameter("caller_is_syncadapter","true").build(),null, String.format("(%s) OR (%s)", "sourceid in ('" + sourceid + "') OR (sync2 in ('" + sourceid + "') AND sourceid IS NULL)", "data_set IS NULL AND sourceid IS NULL AND sync3 IS NOT NULL"),null,null);
        Cursor existPerson = resolver.query(RawConcts,new String[]{"_id","version","starred"}, String.format("(%s) OR (%s)", "sourceid in ('" + sourceid + "') OR (sync2 in ('" + sourceid + "') AND sourceid IS NULL)", "data_set IS NULL AND sourceid IS NULL AND sync3 IS NOT NULL"),null,null);
        assert existPerson != null;
        if(existPerson.getCount()>0){
            existPerson.moveToFirst();
            _id = existPerson.getLong(existPerson.getColumnIndex("_id"));
            version = existPerson.getLong(existPerson.getColumnIndex("version"));
            starred = existPerson.getLong(existPerson.getColumnIndex("starred"));
            existPerson.close();
        }

        for(buzo buzo1: bvaw0.getDBigList()){
            String gpSourceid = buzo1.getC();
            if(!groupSourceid.contains(gpSourceid) && groupSourceid.size()>0 ){
                continue;
            }

            shouldDel = false;
            //update
            if(_id>=0){
                contentProviderOperations.add(ContentProviderOperation.newAssertQuery(appendAccount(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, _id), account,true)).withValue("version", version).withExpectedCount(1).build());
                contentProviderOperations.add(ContentProviderOperation.newAssertQuery(appendAccount(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, _id), account,true)).withValue("starred", starred).withExpectedCount(1).build());
            } else {
                //sourceid assert
                ContentProviderOperation.Builder builder0 = ContentProviderOperation.newAssertQuery(RawConcts).withSelection("sourceid=?", new String[]{sourceid}).withExpectedCount(0);
                contentProviderOperations.add(builder0.build());
            }




            //rawcontact insert sync2 sync3
            long sync3 = 0;
            String sync2 = "";
            bvbe bvbe0 = bvaw0.getCSmall();
            for (bvbo bvbo0 : bvbe0.getD().getAList()) {
                if (bvbo0 == null || !sourceid.equals(bvbo0.getC())) {
                    continue;
                }

                sync2 = bvbo0.getF();
                if (bvbo0.getE() != 0) {
                    sync3 = bvbo0.getE();
                }
                break;
            }
            ContentValues contentValues0 = new ContentValues();
            contentValues0.put("sourceid", sourceid);
            contentValues0.put("sync3", sync3);
            contentValues0.put("sync2", sync2);
            if(_id>=0){
                contentProviderOperations.add(ContentProviderOperation.newUpdate(RawConcts).withValues(contentValues0).withSelection("_id="+ _id,null).build());
            } else {
                contentProviderOperations.add(ContentProviderOperation.newInsert(RawConcts).withValues(contentValues0).build());
            }

            int raw_contact_id = contentProviderOperations.size() - 1;

            //用户数据
            ArrayList<ContentValues> personData =  getPersonData(bvaw0, sourceid);
            ArrayList<ContentValues> existPersonData = new ArrayList<>();
            if(_id>0){
                getExistPersonData(account, String.format("(%s) OR (%s)", "sourceid in ('" + sourceid + "') OR (sync2 in ('" + sourceid + "') AND sourceid IS NULL)", "data_set IS NULL AND sourceid IS NULL AND sync3 IS NOT NULL"),null, existPersonData,false);

                //比较personData和existPersonData 更新data_version和data_sync4
                for(ContentValues person : personData){
                    boolean hasExist = false;
                    for(ContentValues exist : existPersonData){
                        if(person.getAsString("mimetype").equals(exist.getAsString("mimetype"))){
                            hasExist = true;
                            boolean isSame = true;
                            for (String dataField : person.keySet()) {
                                String value1 = person.containsKey(dataField)?person.getAsString(dataField):null;
                                String value2 = exist.containsKey(dataField)?exist.getAsString(dataField):null;
                                if(!Objects.equals(value2, value1) && !TextUtils.equals(value1,value2)){
                                    isSame = false;
                                    long data_version = exist.getAsLong("data_version")+1;
                                    person.put("data_sync4", data_version+10);
                                    person.put("data_version", data_version);
                                    contentProviderOperations.add(ContentProviderOperation.newUpdate(DataConcts).withValues(person).withSelection("_id="+exist.getAsLong("data_id"),null).build());
                                    break;
                                }
                            }

                            if(isSame && !"vnd.android.cursor.item/photo".equals(person.getAsString("mimetype"))){
                                long data_version = exist.getAsLong("data_version")+1;
                                contentProviderOperations.add(ContentProviderOperation.newUpdate(ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, exist.getAsLong("data_id")).buildUpon()
                                        .appendQueryParameter("account_name", account.name)
                                        .appendQueryParameter("account_type", account.type)
                                        .appendQueryParameter("caller_is_syncadapter","true").build()).withValue("data_sync4", data_version+10).withValue("data_version", data_version).build());
                            }
                            break;
                        }
                    }
                    if(!hasExist){
                        contentProviderOperations.add(ContentProviderOperation.newInsert(DataConcts).withValues(person).withValue(ContactsContract.Data.RAW_CONTACT_ID, _id).build());
                        syncResult.stats.numInserts++;
                    }
                }



            } else {
                for (ContentValues contentValues1 : personData) {
                    contentProviderOperations.add(ContentProviderOperation.newInsert(DataConcts).withValues(contentValues1).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id).build());
                    syncResult.stats.numInserts++;
                }
            }
            break;
        }

        if(shouldDel){
            Cursor delcursor = resolver.query(RawConcts,new String[]{"_id"}, String.format("(%s) OR (%s)", "sourceid in ('" + sourceid + "') OR (sync2 in ('" + sourceid + "') AND sourceid IS NULL)", "data_set IS NULL AND sourceid IS NULL AND sync3 IS NOT NULL"),null,null);
            if(delcursor.getCount()>0){
                delcursor.moveToFirst();
                long id = delcursor.getLong(0);
                delcursor.close();
//                resolver.delete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI,id).buildUpon().appendQueryParameter("account_name","wwuwenbiao@gmail.com").appendQueryParameter("account_type","com.google").appendQueryParameter("caller_is_syncadapter", "true").build(),null,null);
                contentProviderOperations.add(ContentProviderOperation.newDelete(appendAccount(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, id), account,true)).build());
                syncResult.stats.numDeletes++;
            }else {
                delcursor.close();
            }
        }
    }

    @SuppressLint("Range")
    private void getExistPersonData(@NonNull Account account, String selection,String[] selectionArgs ,ArrayList<ContentValues> existPersonData,boolean z) {
        Cursor existEntity = resolver.query(appendAccount(ContactsContract.RawContactsEntity.CONTENT_URI,account,true),null, selection,selectionArgs,null);
        assert existEntity!=null;
        while (existEntity.moveToNext()){
            ContentValues tmpcontent = new ContentValues();
            String mimetype = existEntity.getString(existEntity.getColumnIndex("mimetype"));
            tmpcontent.put("mimetype",mimetype);
            tmpcontent.put("data_id",existEntity.getLong(existEntity.getColumnIndex("data_id")));
            tmpcontent.put("data_version",existEntity.getLong(existEntity.getColumnIndex("data_version")));
            tmpcontent.put("is_primary",existEntity.getLong(existEntity.getColumnIndex("is_primary")));
            tmpcontent.put("dirty",existEntity.getLong(existEntity.getColumnIndex("dirty")));
            tmpcontent.put("deleted",existEntity.getLong(existEntity.getColumnIndex("deleted")));
            tmpcontent.put("sourceid",existEntity.getString(existEntity.getColumnIndex("sourceid")));
            tmpcontent.put("sync2",existEntity.getString(existEntity.getColumnIndex("sync2")));
            tmpcontent.put("sync3",existEntity.getString(existEntity.getColumnIndex("sync3")));
            long currentId = existEntity.getLong(existEntity.getColumnIndex("_id"));
            tmpcontent.put("_id",currentId);

//            if(mimetype.equals("vnd.android.cursor.item/group_membership")) {
//                if (groupSourceid.contains(existEntity.getString(existEntity.getColumnIndex("data1")))) {
//                    tmpcontent.put("group_sourceid", existEntity.getString(existEntity.getColumnIndex("group_sourceid")));
//                }
//            }
            String[] cmpFields = new String[0];
            switch (mimetype) {
                case "vnd.com.google.cursor.item/contact_language":
                    cmpFields = new String[]{"data1", "data2"};
                    break;
                case "vnd.com.google.cursor.item/contact_misc":
                    cmpFields = new String[]{"data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10", "data11"};
                    break;
                case "vnd.android.cursor.item/email_v2":
                    cmpFields = new String[]{"data1","data2"};
                    break;
                case "vnd.com.google.cursor.item/contact_user_defined_field":
                    cmpFields = new String[]{"data1", "data2"};
                    break;
                case "vnd.android.cursor.item/contact_event":
                    cmpFields = new String[]{"data1", "data2", "data3"};
                    break;
                case "vnd.com.google.cursor.item/contact_file_as":
                    cmpFields = dataFields;
                    break;
                case "vnd.android.cursor.item/group_membership":
                    cmpFields = new String[]{"group_sourceid","title"};
                    break;
                case "vnd.android.cursor.item/identity":
                    cmpFields = new String[]{"data2", "data1", "data_sync3"};
                    break;
                case "vnd.android.cursor.item/im":
                    cmpFields = new String[]{"data1", "data2", "data3", "data4", "data5", "data_sync3"};
                    break;
                case "vnd.android.cursor.item/nickname":
                    cmpFields = dataFields;
                    break;
                case "vnd.android.cursor.item/note":
                    cmpFields = dataFields;
                    break;
                case "vnd.android.cursor.item/organization":
                    cmpFields = new String[]{"data1","data2","data3","data4","data5","data6","data7","data8"};
                    break;
                case "vnd.android.cursor.item/phone_v2":
                    cmpFields = new String[]{"data1","data2"};
                    break;
                case "vnd.android.cursor.item/photo":
                    cmpFields = dataFields;
                    break;
                case "vnd.android.cursor.item/relation":
                    cmpFields = new String[]{"data1", "data2", "data3"};
                    break;
                case "vnd.android.cursor.item/sip_address":
                    cmpFields = dataFields;
                    break;
                case "vnd.android.cursor.item/name":
                    cmpFields = dataFields;
                    break;
                case "vnd.android.cursor.item/postal-address_v2":
                    cmpFields = new String[]{"data1"};
                    break;
                case "vnd.android.cursor.item/website":
                    cmpFields = new String[]{"data1"};
                    break;
                case "vnd.com.google.cursor.item/contact_calendar_link":
                    cmpFields = new String[]{"data1"};
                    break;
                case "vnd.com.google.cursor.item/contact_extended_property":
                    cmpFields = new String[]{"data1", "data2"};
                    break;
                case "vnd.com.google.cursor.item/contact_external_id":
                    cmpFields = new String[]{"data1"};
                    break;
                case "vnd.com.google.cursor.item/contact_hobby":
                    cmpFields = new String[]{"data1"};
                    break;
                case "vnd.com.google.cursor.item/contact_jot":
                    cmpFields = new String[]{"data1"};
                    break;
            }

            for (String dataField : cmpFields) {
                int index = existEntity.getColumnIndex(dataField);
                switch (existEntity.getType(index)) {
                    case Cursor.FIELD_TYPE_STRING:
                        tmpcontent.put(dataField, existEntity.getString(index));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        tmpcontent.put(dataField, existEntity.getLong(index));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        tmpcontent.put(dataField, existEntity.getFloat(index));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        tmpcontent.put(dataField, existEntity.getBlob(index));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        tmpcontent.putNull(dataField);
                        break;
                }
            }
            existPersonData.add(tmpcontent);
        }
        existEntity.close();
    }

    private ArrayList<ContentValues> getPersonData(bvaw bvaw0,String sourceid){
        ArrayList<ContentValues> personData = new ArrayList<>();
        pushPersonData(bvaw0.getKList(), personData, sourceid);//buzl //vnd.android.cursor.item/contact_event
        pushPersonData(bvaw0.getYList(), personData, sourceid);//buzm //vnd.com.google.cursor.item/contact_calendar_link
        pushPersonData(bvaw0.getSList(), personData, sourceid);//bvbp //vnd.com.google.cursor.item/contact_user_defined_field
        pushPersonData(bvaw0.getGList(), personData, sourceid);//buzz //vnd.android.cursor.item/email_v2
        pushPersonData(bvaw0.getRList(), personData, sourceid);//bvaa //vnd.android.cursor.item/contact_event
        pushPersonData(bvaw0.getAList(), personData, sourceid);//bvab //vnd.com.google.cursor.item/contact_external_id
        pushPersonData(bvaw0.getTList(), personData, sourceid);//bvaf //vnd.com.google.cursor.item/contact_file_as
        pushPersonData(bvaw0.getDBigList(), personData, sourceid);//buzo //vnd.android.cursor.item/group_membership
        pushPersonData(bvaw0.getVList(), personData, sourceid);//bvaj //vnd.com.google.cursor.item/contact_hobby
        pushPersonData(bvaw0.getQList(), personData, sourceid);//bvai //vnd.android.cursor.item/im
        pushPersonData(bvaw0.getZList(), personData, sourceid);//bvak //vnd.com.google.cursor.item/contact_language


        IBaseConverter converter1 = converter.get(bvbq.class);
        for (bvbq bvbq0 : bvaw0.getJList()) {
            assert converter1 != null;
            bvba bvba0 = converter1.getbvalue(bvbq0);
            int v1 = bvba0.getC() + 1;
            if (!(v1 == 3 && (sourceid.equals(bvba0.getD())))) {
                continue;
            }
            dealWithPersonData(bvba0, "vnd.android.cursor.item/website", bvbq0.getC(), personData);
        }

        converter1 = converter.get(buzz.class);
        for (buzz buzz0 : bvaw0.getGList()) {
            assert converter1 != null;
            bvba bvba0 = converter1.getbvalue(buzz0);
            int v1 = bvba0.getC() + 1;
            if (!(v1 == 3 && (sourceid.equals(bvba0.getD())))) {
                continue;
            }
            dealWithPersonData(bvba0, "vnd.android.cursor.item/email_v2", buzz0.getC(), personData);
        }

        converter1 = converter.get(bvbf.class);
        for (bvbf bvbf0 : bvaw0.getHList()) {
            assert converter1 != null;
            bvba bvba0 = converter1.getbvalue(bvbf0);
            int v1 = bvba0.getC() + 1;
            if (!(v1 == 3 && (sourceid.equals(bvba0.getD())))) {
                continue;
            }
            dealWithPersonData(bvba0, "vnd.android.cursor.item/phone_v2", bvbf0.getC(), personData);
        }

        new multiConverter().toContentValues(bvaw0.getFList(), bvaw0.getLList(), bvaw0.getNList(), bvaw0.getXList(), personData);

        List<bvam> bvamList = bvaw0.getDSmallList();
        if (bvamList.isEmpty()) {
            bvam.Builder bvam0 = bvam.newBuilder();
            bvba.Builder bvba0 = bvba.newBuilder();
            bvba0.setC(2);
            bvba0.setD(sourceid);
            bvam0.setB(bvba0.build());
            bvamList = Collections.singletonList(bvam0.build());
        }
        pushPersonData(bvamList, personData, sourceid);

        List<bvap> bvaplist0 = bvaw0.getLList();
        if (bvaplist0.isEmpty()) {
            bvap.Builder bvap0 = bvap.newBuilder();
            bvba.Builder bvba0 = bvba.newBuilder();
            bvap0.setD(0);
            bvba0.setC(2);
            bvba0.setD(sourceid);
            bvap0.setB(bvba0.build());
            bvaplist0 = Collections.singletonList(bvap0.build());
        }
        pushPersonData(bvaplist0, personData, sourceid);

        List<? extends MessageLite> buzglist0 = bvaw0.getIList();
        if (buzglist0.isEmpty()) {
            buzg.Builder buzg0 = buzg.newBuilder();
            bvba.Builder bvba0 = bvba.newBuilder();
            bvba0.setC(2);
            bvba0.setD(sourceid);
            buzg0.setB(bvba0.build());
            buzglist0 = Collections.singletonList(buzg0.build());
        }
        pushPersonData(buzglist0, personData, sourceid);

        pushPersonData(bvaw0.getMList(), personData, sourceid);
        pushPersonData(bvaw0.getHList(), personData, sourceid);
        pushPersonData(bvaw0.getOList(), personData, sourceid);

        bvbg bvbgSelect = bvbg.getDefaultInstance();
        for (bvbg bvbg0 : bvaw0.getEList()) {
            bvba bvba6 = bvbg0.getB() == null ? bvba.getDefaultInstance() : bvbg0.getB();
            if (!bvbg0.getD() && bvbg0.hasD()) {
                int v6 = bvba6.getC();
                if (v6 == 3 && (sourceid.equals(bvba6.getD()))) {
                    bvbgSelect = bvbg0;
                    break;
                }
                if (v6 == 2) {
                    bvbgSelect = bvbg0;
                }
            }
        }
        boolean z = (bvbgSelect.getB() == null ? bvba.getDefaultInstance() : bvbgSelect.getB()).getG();
        personData.add(Objects.requireNonNull(converter.get(bvbg.class)).toContentValues(bvbgSelect, z));

        dealWithPersonDataExtendProp(bvaw0.getBBigList(), personData);
        pushPersonData(bvaw0.getPList(), personData, sourceid);
        pushPersonData(bvaw0.getUList(), personData, sourceid);
        pushPersonData(bvaw0.getJList(), personData, sourceid);
        return personData;
    }

    private ceia buildRequest(Account account) {
        ceia.Builder ckfe0 = ceia.newBuilder();
        cehz.Builder ckfe1 = cehz.newBuilder();
        ContentResolver resolver = getContext().getContentResolver();
        atid atid0;
        //----
        Cursor cursor0 = resolver.query(ContactsContract.SyncState.CONTENT_URI,new String[]{"data"},"account_name=? AND account_type=?",new String[]{account.name,account.type},null);

        byte[] arr_b;
        assert cursor0 != null;
        if (cursor0.moveToNext()){
            try {
                arr_b = cursor0.getBlob(0);
                atid0 = atid.parseFrom(arr_b);
                cursor0.close();
            } catch (InvalidProtocolBufferException e) {
                atid0 = atid.getDefaultInstance();
                cursor0.close();
            }
        }else {
            atid0 = atid.getDefaultInstance();
        }

        if(atid0.hasB()){
            ckfe1.setA(atid0.getB());
        }

        ckfe1.setB(1);
        ckfe0.setC(ckfe1.build());
        //-----

        ckfe0.setA(1000);//pagesize

        cursor0 = resolver.query(Uri.parse("content://com.google.android.gsf.gservices"),null,null , new String[]{"android_id"},null);

        long androidId= 0L;
        assert cursor0 != null;
        if(cursor0.moveToFirst()){
            androidId = Long.parseLong(cursor0.getString(1));
        }
        ckfe0.setH("AID_".concat(Long.toHexString(androidId)));

        //--------\
        cegg.Builder ckfecegg = cegg.newBuilder();
        ckfecegg.addA(8); //"GDATA_COMPATIBILITY"

        ckfe0.setG(ckfecegg.build());
        ckfe0.setB(atid0.getC());

        String s15 = "";
        try {
            String s13 = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(),0).versionName;
            String s14 = "";//"Fsa__prefix_for_sync_people_client_version"
            if(!TextUtils.isEmpty(s13)){
                s15 = s14.concat(s13);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }


        cehk.Builder cehk0 = cehk.newBuilder();
        cehk0.setA(true);
//        cehk0.setC(false);

//        cehl0.setC(cmco.getDefaultInstance());
        ckfe0.setE(gmsVersion);

        cehs.Builder cehs0 = cehs.newBuilder();
        ckex.Builder ckex0 = ckex.newBuilder();
        ckex0.addAllA(Arrays.asList(personInfo));
        cehs0.setA(ckex0.build());
        cehs0.setC(cehr.newBuilder().setA(0).build());
        ckfe0.setD(cehs0.build());

        cehj.Builder cehj0 = cehj.newBuilder();
        cehm.Builder cehm0 = cehm.newBuilder();
        cehm0.setA(2);
        cehj0.setB(cehm0.build());
        cehj0.addC(3);
//        cehj0.setA(cefs.getDefaultInstance());
        ckfe0.setF(cehj0.build());
        return ckfe0.build();
    }

    private void dealWithPersonDataExtendProp(List<buzn> bBigList, ArrayList<ContentValues> personData) {
        JSONObject jSONObject0;
        for(buzn buzn0 : bBigList){
            if(buzn0 != null) {
                String s = buzn0.getC();
                if(("gdataExtendedProperty".equals(buzn0.getE())) && ("android".equals(s))) {
                    String s1 = buzn0.getD();
                    if(!TextUtils.isEmpty(s1)) {
                        try {
                            jSONObject0 = new JSONObject(s1);
                        }
                        catch(JSONException jSONException0) {
                            return;
                        }

                        Iterator<String> iterator0 = jSONObject0.keys();
                        while(iterator0.hasNext()) {
                            String s2 = iterator0.next();
                            try {
                                ContentValues contentValues0 = new ContentValues();
                                contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_extended_property");
                                contentValues0.put("data1", s2);
                                contentValues0.put("data2", jSONObject0.getString(s2));

                                personData.add(contentValues0);
                            }
                            catch(JSONException ignored) {
                            }
                        }
                    }
                }
            }
        }
    }

    private void dealWithPersonData(bvba bvba0, String s, String c, ArrayList<ContentValues> personData) {
        if(bvba0 != null) {
            ArrayList<String> arrayList0 = new ArrayList<>();
            for (buzy buzy0 : bvba0.getFList()) {
                int v1 = buzy0.getB() + 1;
                if (v1 != 2 || !buzy0.hasC()) {
                    continue;
                }
                String s0 = buzy0.getC().getA();
                for (int v = 0; v < s0.length(); ++v) {
                    if (s0.charAt(v) == 0x30) {
                        s0 = s0.substring(v);
                    }
                }
                arrayList0.add((s0.length() == 0 ? "gprofile:" : "gprofile:".concat(s0)));
            }

            if (!arrayList0.isEmpty()) {
                int v = 2;
                String s3 = TextUtils.join(",", arrayList0);
                ContentValues contentValues0 = null;
                if (!TextUtils.isEmpty(s)) {
                    switch (s.hashCode()) {
                        case 0xA272C504: {
                            v = s.equals("vnd.android.cursor.item/email_v2") ? 1 : -1;
                            break;
                        }
                        case 0x1B3458F6: {
                            if (!s.equals("vnd.android.cursor.item/website")) {
                                v = -1;
                            }

                            break;
                        }
                        case 0x28C7A9F2: {
                            v = s.equals("vnd.android.cursor.item/phone_v2") ? 0 : -1;
                            break;
                        }
                        default: {
                            v = -1;
                        }
                    }
                    switch (v) {
                        case 0:
                        case 1:
                        case 2: {
                            contentValues0 = new ContentValues();
                            contentValues0.put("mimetype", "vnd.android.cursor.item/identity");
                            contentValues0.put("data1", s3);
                            contentValues0.put("data2", "com.google");
                            contentValues0.put("data_sync3", s + " " + c);
                        }
                    }
                }

                if (contentValues0 != null) {
                    personData.add(contentValues0);
                }
            }
        }
    }

    private void pushPersonData(List<? extends MessageLite> list0, ArrayList<ContentValues> personData, String sourceid) {
        if(list0.size()==0){
            return;
        }
        BaseConverter converter0 = (BaseConverter) converter.get(list0.get(0).getClass());
        for(MessageLite buzl0 : list0){

            assert converter0 != null;
            bvba bvba0 = converter0.getbvalue(buzl0);
            if(!sourceid.equals(bvba0.getD())){
                continue;
            }
            ContentValues contentvalue0 =  converter0.toContentValues(buzl0,bvba0.getG());
            personData.add(contentvalue0);
        }
    }

    private ArrayList<String> atoz_b(bvaw bvaw0) {
        ArrayList<String> arrayList0 = new ArrayList<>();
        for(Long long0 : (bvaw0.getCSmall() == null ? bvbe.getDefaultInstance():bvaw0.getCSmall()).getCList()){
            arrayList0.add(Long.toHexString(long0));
        }
        return arrayList0;
    }

    private InternalPeopleServiceGrpc.InternalPeopleServiceBlockingStub getInternalPeopleServiceStub(Account account) throws OperationCanceledException, AuthenticatorException, IOException {
        String token = getPeopleSvcToken(getContext(), account);

        if(mchannel == null || mchannel.isTerminated()) {
            HeaderClientInterceptor interceptor = new HeaderClientInterceptor("Bearer " + token, null);
            mchannel = ManagedChannelBuilder.forAddress("people-pa.googleapis.com", 443)
                    .intercept(interceptor)
                    .useTransportSecurity()
                    .build();
        }

        return InternalPeopleServiceGrpc.newBlockingStub(mchannel);
    }

    private String getPeopleSvcToken(Context context, Account account) throws OperationCanceledException, AuthenticatorException, IOException {
        return AccountManager.get(context).blockingGetAuthToken (account, "oauth2:https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.media.upload https://www.googleapis.com/auth/plus.pages.manage https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.profiles.read https://www.googleapis.com/auth/plus.profiles.write https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/peopleapi.legacy.readwrite https://www.googleapis.com/auth/plus.applications.manage https://www.googleapis.com/auth/plus.settings", true);
    }

    private Uri appendAccount(Uri contentUri, Account account, boolean z) {
        if(account == null) {
            return contentUri;
        }

        Uri.Builder uri$Builder0 = contentUri.buildUpon().appendQueryParameter("account_name", account.name).appendQueryParameter("account_type", account.type);
        if(z) {
            uri$Builder0.appendQueryParameter("caller_is_syncadapter", "true");
        }

        return uri$Builder0.build();
    }
}
