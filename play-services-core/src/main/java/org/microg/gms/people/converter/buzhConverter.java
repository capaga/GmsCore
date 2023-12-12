package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.buzh;
import org.microg.gms.people.bvba;

public class buzhConverter extends BaseConverter{


    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((buzh)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        buzh buzh0 = (buzh)message0;
        typeValue typevalue0 = checkType(buzh0.getC(),type, 0);

        String s = buzh0.getF();
        String s1 = buzh0.getE();
        String s2 = buzh0.getG();
        String s3 = buzh0.getH();
        String s4 = buzh0.getI();
        String s5 = buzh0.getK();
        String s6 = buzh0.getJ();
        String s7 = buzh0.getD();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/postal-address_v2");
        contentValues0.put("is_primary", bool0?1:0);
        checkNull(contentValues0, "data4", s);
        checkNull(contentValues0, "data5", s1);
        checkNull(contentValues0, "data6", s2);
        checkNull(contentValues0, "data7", s3);
        checkNull(contentValues0, "data8", s4);
        checkNull(contentValues0, "data10", s5);
        checkNull(contentValues0, "data9", s6);
        checkNull(contentValues0, "data1", s7);
        dataCheck(contentValues0, typevalue0.type, typevalue0.value,0);

        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        int v;
        String data2=contentValues0.getAsString("data2");
        Integer data2l = data2==null?null:Integer.parseInt(data2);
        String s1 = data2l==null?null:data2l==0?contentValues0.getAsString("data3"): typeR.get(data2l);
        String s2 = contentValues0.getAsString("data4");
        String s3 = contentValues0.getAsString("data5");
        String s4 = contentValues0.getAsString("data6");
        String s5 = contentValues0.getAsString("data7");
        String s6 = contentValues0.getAsString("data8");
        String s7 = contentValues0.getAsString("data10");
        String s8 = contentValues0.getAsString("data9");
        String s9 = contentValues0.getAsString("data1");
        boolean z = contentValues0.getAsLong("is_primary") == 1L;
        buzh.Builder buzh0 = buzh.newBuilder();
        if(s1 != null) {
            buzh0.setC(s1);
        }

        if(s3 == null) {
            v = 0;
        }
        else {
            buzh0.setE(s3);
            v = 1;
        }

        if(s4 != null) {
            buzh0.setG(s4);
            v = 1;
        }

        if(s5 != null) {
            buzh0.setH(s5);
            v = 1;
        }

        if(s6 != null) {
            buzh0.setI(s6);
            v = 1;
        }

        if(s7 != null) {
            buzh0.setK(s7);
            v = 1;
        }

        if(s8 != null) {
            buzh0.setJ(s8);
            v = 1;
        }

        if(s9 != null) {
            buzh0.setD(s9);
        }

        if(s2 != null && (v != 0 || !s2.equals(s9))) {

            buzh0.setF(s2);
        }

        bvba bvba0 = sourceid2bvba(sourceid, z);


        buzh0.setB(bvba0);
        return buzh0.build();
    }


}
