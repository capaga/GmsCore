package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvam;
import org.microg.gms.people.bvba;

public class bvamConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvam)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvam bvam0 = (bvam)message0;
        String s = bvam0.getH();
        String s1 = bvam0.getE();
        String s2 = bvam0.getG();
        String s3 = bvam0.getF();
        String s4 = bvam0.getI();
        String s5 = bvam0.getC();
        String s6 = bvam0.getK();
        String s7 = bvam0.getM();
        String s8 = bvam0.getL();
        String s9 = bvam0.getJ();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/name");
        checkNull(contentValues0, "data4", s);
        checkNull(contentValues0, "data2", s1);
        checkNull(contentValues0, "data5", s2);
        checkNull(contentValues0, "data3", s3);
        checkNull(contentValues0, "data6", s4);
        checkNull(contentValues0, "data1", s5);
        if(checkArrayEmpty(new String[]{s6, s7, s8})) {
            checkNull(contentValues0, "data7", s9);
            contentValues0.putNull("data8");
            contentValues0.putNull("data9");
            return contentValues0;
        }

        checkNull(contentValues0, "data7", s6);
        checkNull(contentValues0, "data8", s7);
        checkNull(contentValues0, "data9", s8);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data4");
        String s2 = contentValues0.getAsString("data2");
        String s3 = contentValues0.getAsString("data5");
        String s4 = contentValues0.getAsString("data3");
        String s5 = contentValues0.getAsString("data6");
        String s6 = contentValues0.getAsString("data1");
        String s7 = contentValues0.getAsString("data7");
        String s8 = contentValues0.getAsString("data8");
        String s9 = contentValues0.getAsString("data9");
        bvam.Builder bvam0 = bvam.newBuilder();
        if(s1 != null) {
            bvam0.setH(s1);
        }

        if(s2 != null) {
            bvam0.setE(s2);
        }

        if(s3 != null) {
            bvam0.setG(s3);
        }

        if(s4 != null) {
            bvam0.setF(s4);
        }

        if(s5 != null) {
            bvam0.setI(s5);
        }

        if(s6 != null) {
            bvam0.setC(s6);
        }

        if(s7 != null) {
            bvam0.setK(s7);
        }

        if(s8 != null) {
            bvam0.setM(s8);
        }

        if(s9 != null) {
            bvam0.setL(s9);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        bvam0.setB(bvba0);

        return bvam0.build();
    }
}
