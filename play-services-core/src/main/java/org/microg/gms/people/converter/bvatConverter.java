package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvat;
import org.microg.gms.people.bvba;

public class bvatConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((bvat)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        bvat bvat0 = (bvat)message0;
        int v = bvat0.getI()+1;
        String s = null;
        if(v == 2) {
            s = "work";
        }
        else {
            int v1 = bvat0.getI()+1;
            if(v1 == 3) {
                s = "school";
            }
        }

        typeValue asge0 = checkType(s, type2, 0);
        String s1 = bvat0.getC();
        String s2 = bvat0.getE();
        String s3 = bvat0.getD();
        String s4 = bvat0.getH();
        String s5 = bvat0.getF();
        String s6 = bvat0.getG();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/organization");
        contentValues0.put("is_primary", bool0?1:0);
        checkNull(contentValues0, "data1", s1);
        checkNull(contentValues0, "data4", s2);
        checkNull(contentValues0, "data5", s3);
        checkNull(contentValues0, "data6", s4);
        checkNull(contentValues0, "data7", s5);
        checkNull(contentValues0, "data8", s6);
        dataCheck(contentValues0, asge0.type, asge0.value, 0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s = contentValues0.getAsString("data2");
        Integer data2 = s == null ? null : ((int)Integer.parseInt(s));
        String s1 = data2 == null ? null : data2 == 0 ? contentValues0.getAsString("data3") : type2R.get(data2);
        String s2 = contentValues0.getAsString("data1");
        String s3 = contentValues0.getAsString("data4");
        String s4 = contentValues0.getAsString("data5");
        String s5 = contentValues0.getAsString("data6");
        String s6 = contentValues0.getAsString("data7");
        String s7 = contentValues0.getAsString("data8");
        boolean z = contentValues0.getAsLong("is_primary") > 0;
        bvat.Builder bvat0 = bvat.newBuilder();

        bvat0.setI(0);
        if(s1 != null) {
            if(s1.equals("work")) {
                bvat0.setI(1);
            }
            else if(s1.equals("school")) {
                bvat0.setI(2);
            }
        }

        if(s2 != null) {
            bvat0.setC(s2);
        }

        if(s3 != null) {
            bvat0.setE(s3);
        }

        if(s4 != null) {
            bvat0.setD(s4);
        }

        if(s5 != null) {
            bvat0.setH(s5);
        }

        if(s6 != null) {
            bvat0.setF(s6);
        }

        if(s7 != null) {
            bvat0.setG(s7);
        }

        bvba bvba0 = sourceid2bvba(sourceid, z);
        bvat0.setB(bvba0);
        return bvat0.build();
    }
}
