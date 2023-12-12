package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;
import org.microg.gms.people.bvbq;

public class bvbqConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvbq)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvbq bvbq0 = (bvbq)message0;
        typeValue asge0 = checkType(bvbq0.getD(), websiteType, 0);
        String s = bvbq0.getC();
        if(TextUtils.isEmpty(s)) {
            return null;
        }

        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/website");
        contentValues0.put("is_primary",  bool0?1:0);
        checkNull(contentValues0, "data1", s);
        dataCheck(contentValues0, asge0.type, asge0.value, 0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        if(TextUtils.isEmpty(s1)) {
            return null;
        }

        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : ((int)Integer.parseInt(s));
        String s2 = data2 == null ? null : data2 == 0 ? contentValues0.getAsString("data3") : websiteTypeR.get(data2);
        boolean z = contentValues0.getAsLong("is_primary") > 0;
        bvbq.Builder bvbq0 = bvbq.newBuilder();

        bvbq0.setC(s1);
        if(s2 != null) {
            bvbq0.setD(s2);
        }

        bvba bvba0 = sourceid2bvba(sourceid, z);

        bvbq0.setB(bvba0);
        return bvbq0.build();
    }
}
