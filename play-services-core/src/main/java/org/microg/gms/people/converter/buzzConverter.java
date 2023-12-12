package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.buzz;
import org.microg.gms.people.bvba;

public class buzzConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((buzz)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        buzz buzz0 = (buzz)message0;
        typeValue asge0 = checkType(buzz0.getD(),type, 0);
        String s = buzz0.getC();
        return email_v2Value(asge0.type, s, null, asge0.value, bool0);
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : Integer.parseInt(s);
        String s1 = data2 == null ? contentValues0.getAsString("data3") : typeR.get(data2);
        String s2 = contentValues0.getAsString("data1");
        boolean z = contentValues0.getAsLong("is_primary") > 0;
        buzz.Builder buzz0 = buzz.newBuilder();
        if(s1 != null) {
            buzz0.setD(s1);
        }

        if(s2 != null) {
            buzz0.setC(s2);
        }

        bvba bvba0 = sourceid2bvba(sourceid, z);

        buzz0.setB(bvba0);
        return buzz0.build();
    }
}
