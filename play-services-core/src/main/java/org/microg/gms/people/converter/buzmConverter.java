package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.buzm;
import org.microg.gms.people.bvba;

public class buzmConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((buzm)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        buzm buzm0 = (buzm)message0;
        typeValue asge0 = checkType(buzm0.getD(),calendarType,4);
        String s = buzm0.getC();
        if(TextUtils.isEmpty(s)) {
            return null;
        }

        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_calendar_link");
        checkNull(contentValues0, "data1", s);
        dataCheck(contentValues0, asge0.type, asge0.value, 4);
        contentValues0.put("data4", bool0?1:0);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        long v;
        String s = contentValues0.getAsString("data2");
        Integer data2 = s==null?null:Integer.parseInt(s);
        String s1 = data2==null?null:data2==4? contentValues0.getAsString("data3") : calendarTypeR.get(data2);
        String s2 = contentValues0.getAsString("data1");

//        Long long0 = contentValues0.getAsLong("data4");
//        v = long0 != null && long0 > 0L ? 1 : 0;
        v = contentValues0.getAsLong("is_primary");

        buzm.Builder buzm0 = buzm.newBuilder();
        if(s2 != null) {
            buzm0.setC(s2);
        }

        if(s1 != null) {
            buzm0.setD(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid, v == 1);


        buzm0.setB(bvba0);
        return buzm0.build();
    }
}
