package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.util.Log;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.buzg;
import org.microg.gms.people.bvba;


public class buzgConverter extends BaseConverter {

    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((buzg)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        if(!(message0 instanceof buzg)){
            Log.e("Converter","message0 is not buzg");
        }

        String s = ((buzg)message0).getC();
        ContentValues contentValues0 = new ContentValues();
        contentValues0.put("mimetype", "vnd.android.cursor.item/note");
        checkNull(contentValues0,"data1",s);
        return contentValues0;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        buzg.Builder buzg0 = buzg.newBuilder();
        if(s1 != null) {
            buzg0.setC(s1);
        }

        bvba bvba0 = sourceid2bvba(sourceid);
        buzg0.setB(bvba0);
        return buzg0.build();
    }
}
