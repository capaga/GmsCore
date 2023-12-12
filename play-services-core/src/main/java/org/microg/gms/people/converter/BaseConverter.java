package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public abstract class BaseConverter implements IBaseConverter{
    HashMap<Byte,Integer> byteTable = new HashMap<Byte,Integer>(){{
        put((byte)1,1);
        put((byte)2,2);
        put((byte)3,3);
        put((byte)-1,4);
    }};
    HashMap<Byte,Integer> byteTable2 = new HashMap<Byte,Integer>(){{
        put((byte)1,1);
        put((byte)2,2);
        put((byte)3,3);
        put((byte)4,4);
        put((byte)-1,5);
    }};
    public static HashMap<String,Integer> sexType = new HashMap<String,Integer>(){{
       put("male",1);
       put("female",2);
    }};
    public static HashMap<String,Integer> contactJotType = new HashMap<String,Integer>(){{
        put("user",4);
        put("keyword",3);
        put("home",2);
        put("work",1);
        put("other",5);
    }};
    static HashMap<String,Integer> type = new HashMap<String,Integer>(){{
        put("home",1);
        put("work",2);
        put("other",3);
    }};
    static HashMap<Integer,String> typeR = new HashMap<Integer,String>(){{
        put(1,"home");
        put(2,"work");
        put(3,"other");
    }};
    static HashMap<String,Integer> type2 = new HashMap<String,Integer>(){{
        put("work",1);
        put("school",2);
    }};
    static HashMap<Integer,String> type2R = new HashMap<Integer,String>(){{
        put(1,"work");
        put(2,"school");
    }};
    static HashMap<String,Integer> type3 = new HashMap<String,Integer>(){{
        put("anniversary",1);
        put("birthday",3);
        put("other",2);
    }};
    static HashMap<Integer,String> type3R = new HashMap<Integer,String>(){{
        put(1,"anniversary");
        put(2,"other");
        put(3,"birthday");
    }};
    static HashMap<String,Integer> phenoType = new HashMap<String,Integer>(){{
        put("home",1);
        put("mobile",2);
        put("work",3);
        put("workFax",4);
        put("homeFax",5);
        put("pager",6);
        put("other",7);
        put("main",12);
        put("otherFax",13);
        put("workMobile",17);
        put("workPager",18);
    }};
    static HashMap<Integer,String> phenoTypeR = new HashMap<Integer,String>(){{
        put(1,"home");
        put(2,"mobile");
        put(3,"work");
        put(4,"workFax");
        put(5,"homeFax");
        put(6,"pager");
        put(7,"other");
        put(12,"main");
        put(13,"otherFax");
        put(17,"workMobile");
        put(18,"workPager");
    }};
    static HashMap<String,Integer> socialType = new HashMap<String,Integer>(){{
        put("aim",0);
        put("msn",1);
        put("yahoo",2);
        put("skype",3);
        put("qq",4);
        put("googleTalk",5);
        put("icq",6);
        put("jabber",7);
        put("netMeeting",8);
    }};
    static HashMap<Integer,String> socialTypeR = new HashMap<Integer,String>(){{
        put(0,"aim");
        put(1,"msn");
        put(2,"yahoo");
        put(3,"skype");
        put(4,"qq");
        put(5,"googleTalk");
        put(6,"icq");
        put(7,"jabber");
        put(8,"netMeeting");
    }};
    static HashMap<String,Integer> relationshipType = new HashMap<String,Integer>(){{
        put("assistant",1);
        put("brother",2);
        put("child",3);
        put("domesticPartner",4);
        put("father",5);
        put("friend",6);
        put("manager",7);
        put("mother",8);
        put("parent",9);
        put("partner",10);
        put("referredBy",11);
        put("relative",12);
        put("sister",13);
        put("spouse",14);
    }};
    static HashMap<Integer,String> relationshipTypeR = new HashMap<Integer,String>(){{
        put(1,"assistant");
        put(2,"brother");
        put(3,"child");
        put(4,"domesticPartner");
        put(5,"father");
        put(6,"friend");
        put(7,"manager");
        put(8,"mother");
        put(9,"parent");
        put(10,"partner");
        put(11,"referredBy");
        put(12,"relative");
        put(13,"sister");
        put(14,"spouse");
    }};
    static HashMap<String,Integer> websiteType = new HashMap<String,Integer>(){{
        put("home",4);
        put("work",5);
        put("blog",2);
        put("profile",3);
        put("homePage",1);
        put("ftp",6);
        put("other",7);
    }};
    static HashMap<Integer,String> websiteTypeR = new HashMap<Integer,String>(){{
        put(4,"home");
        put(5,"work");
        put(2,"blog");
        put(3,"profile");
        put(1,"homePage");
        put(6,"ftp");
        put(7,"other");
    }};
    static HashMap<String,Integer> calendarType = new HashMap<String,Integer>(){{
        put("home",1);
        put("work",2);
        put("freeBusy",3);
    }};
    static HashMap<Integer,String> calendarTypeR = new HashMap<Integer,String>(){{
        put(1,"home");
        put(2,"work");
        put(3,"freeBusy");
    }};
    static HashMap<String,Integer> contactType = new HashMap<String,Integer>(){{
        put("account",1);
        put("customer",2);
        put("network",3);
        put("organization",4);
    }};
static HashMap<Integer,String> contactTypeR = new HashMap<Integer,String>(){{
        put(1,"account");
        put(2,"customer");
        put(3,"network");
        put(4,"organization");
    }};

    abstract public bvba getbvalue(MessageLite message0);

    bvba sourceid2bvba(String s) {
        bvba.Builder bvba0 = bvba.newBuilder();
        bvba0.setC(2);
        if(s != null){
            bvba0.setD(s);
        }
        return bvba0.build();
    }

    protected bvba sourceid2bvba(String sourceid, boolean z) {
        bvba bvba0 = sourceid2bvba(sourceid);
        bvba.Builder bvba1 = bvba0.toBuilder();
        bvba1.setG(z);
        return bvba1.build();
    }

    class typeValue{
        int type;
        String value;

        public typeValue(int type,String value){
            this.type = type;
            this.value = value;
        }
    }

    public abstract ContentValues toContentValues(MessageLite message0, Boolean bool0);

    public typeValue checkType(String s,Map map0, Integer integer0) {
        Integer integer1 = (Integer)map0.get(s);
        if(integer1 != null) {
            s = "";
        }

        if(integer1 != null) {
            integer0 = integer1;
        }

        return new typeValue(integer0, s);
    }

    public String buildPhotoStr(String s, boolean z) {
        if(s == null) {
            Log.e("PhotoUrlUtil", "Photo url is null");
            s = "";
        }

        StringBuilder stringBuilder0 = new StringBuilder();
        stringBuilder0.append(s);
        stringBuilder0.append(" ");
        stringBuilder0.append(s);
        if(z) {
            stringBuilder0.append(" Sync_High_Res");
        }

        return stringBuilder0.toString();
    }

    public boolean checkArrayEmpty(String[] arr_s) {
        int v;
        for(v = 0; v < 3; ++v) {
            if(!TextUtils.isEmpty(arr_s[v])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void checkNull(ContentValues contentvalue, String s1, String s2) {
        if(TextUtils.isEmpty(s2)) {
            contentvalue.putNull(s1);
            return;
        }

        contentvalue.put(s1, s2);
    }

    public ContentValues contact_eventValue(int v, String s, String s1) {
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/contact_event");
        checkNull(contentValues0, "data1", s);
        dataCheck(contentValues0, v, s1,0);
        return contentValues0;
    }

    public  ContentValues email_v2Value(int v, String s, String s1, String s2, boolean z) {
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/email_v2");
        contentValues0.put("is_primary", z?1:0);
        checkNull(contentValues0, "data1", s);
        checkNull(contentValues0, "data4", s1);
        dataCheck(contentValues0, v, s2,0);
        return contentValues0;
    }

    public ContentValues group_membershipValue(String s) {
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/group_membership");
        checkNull(contentValues0, "group_sourceid", s);
        return contentValues0;
    }

    void dataCheck(ContentValues contentValues0, int v, String s,int v1) {
        if(v1 == v && (TextUtils.isEmpty(s))) {
            contentValues0.putNull("data2");
            contentValues0.putNull("data3");
            return;
        }

        contentValues0.put("data2", v);
        checkNull(contentValues0, "data3", s);
    }

    public String getTimeStr(long v) {
        GregorianCalendar gregorianCalendar0 = new GregorianCalendar();
        gregorianCalendar0.setGregorianChange(new Date(0x8000000000000000L));
        gregorianCalendar0.setTimeZone(TimeZone.getTimeZone("UTC"));
        gregorianCalendar0.setTimeInMillis(v);
        int v1 = 0;
        int v2 = gregorianCalendar0.get(0);
        int v3 = gregorianCalendar0.get(1);
        if(v2 != 0) {
            v1 = v3;
        }

        int v4 = gregorianCalendar0.get(2);
        int v5 = gregorianCalendar0.get(5);
        DecimalFormat decimalFormat0 = new DecimalFormat("0000");
        DecimalFormat decimalFormat1 = new DecimalFormat("00");
        return decimalFormat0.format(((long)v1)) + "-" + decimalFormat1.format(((long)(v4 + 1))) + "-" + decimalFormat1.format(((long)v5));
    }

    public Long timestr2long(String s) {
        int v3;
        int v2;
        int v1;
        int v;

        List<String> list0 = Arrays.asList(TextUtils.split(s, "-"));
        if(list0.size() != 3) {
            return null;
        }

        try {
            v = Integer.parseInt(((String)list0.get(0)));
            v1 = Integer.parseInt(((String)list0.get(1))) - 1;
            v2 = Integer.parseInt(((String)list0.get(2)));
        }
        catch(NumberFormatException numberFormatException0) {
            Log.e("FSA2_DataTypeConverters", "NumberFormatException when converting date "+s+"to ms");
            return null;
        }

        GregorianCalendar gregorianCalendar0 = new GregorianCalendar();
        gregorianCalendar0.setGregorianChange(new Date(0x8000000000000000L));
        gregorianCalendar0.setTimeZone(TimeZone.getTimeZone("UTC"));
        if(v == 0) {
            gregorianCalendar0.set(0, 0);
            v3 = 1;
        }
        else {
            v3 = v;
        }

        gregorianCalendar0.set(v3, v1, v2, 0, 0, 0);
        gregorianCalendar0.set(14, 0);
        return gregorianCalendar0.getTimeInMillis();
    }

    public  String TimeStrSub(String s) {
        if(s == null) {
            return null;
        }

        if(s.matches("0000-[0-2][0-9]-[0-3][0-9]")) {
            String s1 = s.substring(5);
            return s1.length() == 0 ? new String("--") : "--".concat(s1);
        }

        return s;
    }
}
