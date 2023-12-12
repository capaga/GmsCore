package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bval;
import org.microg.gms.people.bvba;

public class bvalConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bval)message0).getD();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bval bval0 = (bval)message0;
        String s = bval0.hasOneofField1() ? ((String)bval0.getOneofField1()) : "";
        return TextUtils.isEmpty(s) ? null : group_membershipValue(s);
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("group_sourceid");
        bval.Builder bval0 = bval.newBuilder();
        bvba bvba0 = sourceid2bvba(sourceid);



        bval0.setD(bvba0);

        if(s1 != null) {
            bval0.setOneofField1(s1);
        }

        return bval0.build();
    }
}
