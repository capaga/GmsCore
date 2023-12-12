package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvaf;
import org.microg.gms.people.bvba;

public class bvafConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvaf)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        String s = ((bvaf)message0).getC();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_file_as");
        checkNull(contentValues0, "data1", s);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        bvaf.Builder bvaf0 = bvaf.newBuilder();
        if(s1 != null) {
            bvaf0.setC(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvaf0.setB(bvba0);

        return bvaf0.build();
    }
}
