package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;
import org.microg.gms.people.bvbp;

public class bvbpConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvbp)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvbp bvbp0 = (bvbp)message0;
        if(!bvbp0.getD().isEmpty()) {
            String s = bvbp0.getC().isEmpty() ? "Custom" : bvbp0.getC();
            String s1 = bvbp0.getD();
            ContentValues contentValues0 = new ContentValues();
            contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_user_defined_field");
            contentValues0.put("data1", s);
            checkNull(contentValues0, "data2", s1);
            return contentValues0;
        }

        return null;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        String s2 = contentValues0.getAsString("data2");
        bvbp.Builder bvbp0 = bvbp.newBuilder();
        if(!TextUtils.isEmpty(s2)) {
            if(TextUtils.isEmpty(s1)) {
                s1 = "Custom";
            }


            bvbp0.setC(s1);
            bvbp0.setD(s2);
            bvba bvba0 = sourceid2bvba(sourceid);

            bvbp0.setB(bvba0);
            return bvbp0.build();
        }

        return null;
    }
}
