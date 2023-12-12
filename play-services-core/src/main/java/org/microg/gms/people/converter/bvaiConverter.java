package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvai;
import org.microg.gms.people.bvba;

public class bvaiConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvai)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvai bvai0 = (bvai)message0;
        typeValue asge0 = checkType(bvai0.getD(),type, 0);
        String s = bvai0.getE();
        Integer integer0 = (Integer)socialType.get(s);
        String s1 = null;
        if(integer0 == null && !TextUtils.isEmpty(s)) {
            integer0 = (int)-1;
        }
        else {
            s = null;
        }

        int v = (int)asge0.type;
        String s2 = bvai0.getC();
        if(integer0 != null) {
            s1 = integer0.toString();
        }

        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/im");
        contentValues0.put("is_primary", bool0?1:0);
        checkNull(contentValues0, "data1", s2);
        checkNull(contentValues0, "data5", s1);
        checkNull(contentValues0, "data6", s);
        dataCheck(contentValues0, v, asge0.value, 0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : Integer.parseInt(s);
        String s1 = data2 == null ? null : data2 == 0 ? contentValues0.getAsString("data3") : typeR.get(data2);
        String s2 = contentValues0.getAsString("data1");
        Integer integer0 = contentValues0.getAsInteger("data5");
        String s3 = integer0 == null || integer0 != -1 ? socialTypeR.get(integer0) : contentValues0.getAsString("data6");
        boolean z = contentValues0.getAsLong("is_primary") > 0;
        bvai.Builder bvai0 = bvai.newBuilder();
        if(s1 != null) {
            bvai0.setD(s2);
        }

        if(s2 != null) {
            bvai0.setC(s2);
        }

        if(s3 != null) {
            bvai0.setE(s3);
        }

        bvba bvba0 = sourceid2bvba(sourceid, z);
        bvai0.setB(bvba0);
        return bvai0.build();
    }
}
