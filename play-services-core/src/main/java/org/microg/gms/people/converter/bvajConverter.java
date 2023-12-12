package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvaj;
import org.microg.gms.people.bvba;

public class bvajConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvaj)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvaj bvaj0 = (bvaj)message0;
        if(!TextUtils.isEmpty(bvaj0.getC())) {
            String s = bvaj0.getC();
            ContentValues contentValues0 = new ContentValues();
            contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_hobby");
            checkNull(contentValues0, "data1", s);
            return contentValues0;
        }

        return null;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        bvaj.Builder bvaj0 = bvaj.newBuilder();
        if(s1 != null) {
            bvaj0.setC(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvaj0.setB(bvba0);

        return bvaj0.build();
    }
}
