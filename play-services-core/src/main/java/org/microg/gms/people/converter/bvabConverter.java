package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvab;
import org.microg.gms.people.bvba;

public class bvabConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvab)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvab bvab0 = (bvab)message0;
        typeValue asge0 = checkType(bvab0.getD(),contactType, 5);
        String s = bvab0.getC();
        if(TextUtils.isEmpty(s)) {
            return null;
        }

        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_external_id");
        contentValues0.put("data1", s);
        dataCheck(contentValues0, asge0.type, asge0.value, 5);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        int data2 = s == null ? null : Integer.parseInt(s);
        String s1 = data2==5 ?  contentValues0.getAsString("data3") : contactTypeR.get(data2);
        String s2 = contentValues0.getAsString("data1");
        bvab.Builder bvab0 = bvab.newBuilder();
        if(s2 != null) {
            bvab0.setC(s2);
        }

        if(s1 != null) {
            bvab0.setD(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvab0.setB(bvba0);
        return bvab0.build();
    }
}
