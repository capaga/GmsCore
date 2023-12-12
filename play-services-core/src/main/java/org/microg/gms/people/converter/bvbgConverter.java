package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;
import org.microg.gms.people.bvbg;

public class bvbgConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvbg)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvbg bvbg0 = (bvbg)message0;
        String s = buildPhotoStr(bvbg0.getC(), false);
        String s1 = bvbg0.getE();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/photo");
        checkNull(contentValues0, "data_sync1", s);
        checkNull(contentValues0, "data_sync2", s1);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s3[] = contentValues0.getAsString("data_sync1") == null ? null : contentValues0.getAsString("data_sync1").split(" ");
        String s1 = null;
        if(s3 != null && s3.length > 0){
            s1 = s3[0];
        }
        String s2 = contentValues0.getAsString("data_sync2");
        bvbg.Builder bvbg0 = bvbg.newBuilder();
        if(s1 != null) {
            bvbg0.setC(s1);
        }

        if(s2 != null) {
            bvbg0.setE(s2);
        }

        bvba bvba0 = sourceid2bvba(sourceid);

        bvbg0.setB(bvba0);
        return bvbg0.build();
    }
}
