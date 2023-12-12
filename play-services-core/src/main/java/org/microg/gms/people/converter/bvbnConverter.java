package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;
import org.microg.gms.people.bvbn;

public class bvbnConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvbn)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvbn bvbn0 = (bvbn)message0;
        typeValue asge0 = checkType(bvbn0.getD(), type, 0);
        String s = bvbn0.getC();
        if(TextUtils.isEmpty(s)) {
            return null;
        }

        int v = (int)asge0.type;
        String s1 = asge0.value;
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/sip_address");
        contentValues0.put("is_primary",  bool0?1:0);
        if(s.startsWith("sip:")) {
            s = s.substring(4);
        }

        checkNull(contentValues0, "data1", s);
        dataCheck(contentValues0, v, s1, 0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : ((int)Integer.parseInt(s));
        String s1 = data2 == null ? null : data2 == 0 ? contentValues0.getAsString("data3") : typeR.get(data2);
        String s2 = contentValues0.getAsString("data1");
        boolean z = contentValues0.getAsLong("is_primary") > 0;
        bvbn.Builder bvbn0 = bvbn.newBuilder();
        if(s2 != null) {
            if(!s2.contains(":")) {
                s2 = s2.length() == 0 ? "sip:" : "sip:".concat(s2);
            }

            bvbn0.setC(s2);
        }

        if(s1 != null) {
            bvbn0.setD(s1);
        }

        bvba bvba0 = sourceid2bvba(s, z);
        bvbn0.setB(bvba0);

        return bvbn0.build();
    }
}
