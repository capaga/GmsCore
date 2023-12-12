package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;
import org.microg.gms.people.bvbm;

public class bvbmConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvbm)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvbm bvbm0 = (bvbm)message0;
        typeValue asge0 = checkType(bvbm0.getC(), relationshipType, 0);
        String s = bvbm0.getD();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/relation");
        checkNull(contentValues0, "data1", s);
        dataCheck(contentValues0, asge0.type, asge0.value, 0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : ((int)Integer.parseInt(s));
        String s1 = data2 == null ? null : data2 == 0 ? contentValues0.getAsString("data3") : relationshipTypeR.get(data2);
        String s2 = contentValues0.getAsString("data1");
        bvbm.Builder bvbm0 = bvbm.newBuilder();
        if(s1 != null) {
            bvbm0.setC(s1);
        }

        if(s2 != null) {
            bvbm0.setD(s2);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvbm0.setB(bvba0);
        return bvbm0.build();
    }
}
