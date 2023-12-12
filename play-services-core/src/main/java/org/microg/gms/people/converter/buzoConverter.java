package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.buzo;
import org.microg.gms.people.bvba;

public class buzoConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((buzo)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        String s = ((buzo)message0).getC();
        return TextUtils.isEmpty(s) ? null : group_membershipValue(s);
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("group_sourceid");
        buzo.Builder buzo0 = buzo.newBuilder();
        if(s1 != null) {
            buzo0.setC(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        buzo0.setB(bvba0);

        return buzo0.build();
    }
}
