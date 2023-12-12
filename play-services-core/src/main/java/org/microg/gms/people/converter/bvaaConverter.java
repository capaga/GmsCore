package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvaa;
import org.microg.gms.people.bvba;

public class bvaaConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvaa)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvaa bvaa0 = (bvaa)message0;
        typeValue asge0 = checkType(bvaa0.getD(),type3, 0);
        String s = getTimeStr(bvaa0.getC());
        if(TextUtils.isEmpty(s)) {
            return null;
        }
        s = TimeStrSub(s);
        return contact_eventValue(asge0.type, s, asge0.value);
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        int data2 = s == null ? null : Integer.parseInt(s);
        String s1 = data2 == 0 ? contentValues0.getAsString("data3") : type3R.get(data2);
        String s2 = contentValues0.getAsString("data1");
        bvaa.Builder bvaa0 = bvaa.newBuilder();
        if(s2 != null) {
            if(s2.startsWith("--")) {
                assert s != null;
                s2 =  s.matches("--[0-2][0-9]-[0-3][0-9]") ? s.replace("--", "0000-") : s;
            }

            Long long0 = timestr2long(s2);
            if(long0 != null) {
                long v = long0 == 0 ? 1 : long0;
                bvaa0.setC(v);
            }
        }

        if(s1 != null) {
            bvaa0.setD(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvaa0.setB(bvba0);

        return bvaa0.build();
    }
}
