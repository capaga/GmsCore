package org.microg.gms.people.converter;

import android.content.ContentValues;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvba;

public interface IBaseConverter {

    ContentValues toContentValues(MessageLite message0, Boolean bool0);
    MessageLite toProtobuf(ContentValues contentValues0, String sourceid);
    void checkNull(ContentValues contentvalue,String s1,String s2);
    bvba getbvalue(MessageLite message0);
}
