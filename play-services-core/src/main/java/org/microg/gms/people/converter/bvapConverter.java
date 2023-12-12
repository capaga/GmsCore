package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvap;
import org.microg.gms.people.bvba;

public class bvapConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvap)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvap bvap0 = (bvap)message0;
        int v = bvap0.getD()+1;
        if(v != 0 && v != 1) {
            return null;
        }

        String s = bvap0.getC();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/nickname");
        contentValues0.put("data2", Integer.valueOf(1));
        checkNull(contentValues0, "data1", s);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        bvap.Builder bvap0 = bvap.newBuilder();
        if(s1 != null) {
            bvap0.setC(s1);
            bvap0.setD(0);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvap0.setB(bvba0);

        return bvap0.build();
    }
}
