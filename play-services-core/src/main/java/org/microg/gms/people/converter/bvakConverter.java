package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvak;
import org.microg.gms.people.bvba;

public class bvakConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvak)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvak bvak0 = (bvak)message0;
        if(!TextUtils.isEmpty(bvak0.getC())) {
            String s = bvak0.getC();
            ContentValues contentValues0 = new ContentValues();
            contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_language");
            checkNull(contentValues0, "data1", s);
            checkNull(contentValues0, "data2", null);
            return contentValues0;
        }

        return null;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        bvak.Builder bvak0 = bvak.newBuilder();
        if(s1 != null) {
            bvak0.setC(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvak0.setB(bvba0);

        return bvak0.build();
    }
}
