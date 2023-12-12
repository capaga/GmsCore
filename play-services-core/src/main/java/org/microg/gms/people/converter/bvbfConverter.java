package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;
import org.microg.gms.people.bvbf;

public class bvbfConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvbf)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvbf bvbf0 = (bvbf)message0;
        typeValue asge0 = checkType(bvbf0.getD(), phenoType, 0);
        String s = bvbf0.getC();
        String s1 = bvbf0.getF();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/phone_v2");
        contentValues0.put("is_primary",  bool0?1:0);
        checkNull(contentValues0, "data1", s);
        checkNull(contentValues0, "data4", s1);
        dataCheck(contentValues0, asge0.type, asge0.value, 0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : ((int)Integer.parseInt(s));
        String s1 = data2 == null ? null : data2 == 0 ? contentValues0.getAsString("data3") : phenoTypeR.get(data2);
        String s2 = contentValues0.getAsString("data1");
        String s3 = contentValues0.getAsString("data4");
        boolean z = contentValues0.getAsLong("is_primary") > 0;
        bvbf.Builder bvbf0 = bvbf.newBuilder();
        if(s1 != null) {
            bvbf0.setD(s1);
        }

        if(s2 != null) {
            bvbf0.setC(s2);
        }

        if(s3 != null) {
            bvbf0.setF(s3);
        }

        bvba bvba0 = sourceid2bvba(sourceid, z);
        bvbf0.setB(bvba0);

        return bvbf0.build();
    }
}
