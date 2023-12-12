package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.buzl;
import org.microg.gms.people.bvba;

public class buzlConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return ((buzl)message0).getB();
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        String s;
        buzl buzl0 = (buzl)message0;
        long v = buzl0.getC();
        if(v == 0L && !buzl0.getE().isEmpty()) {
            s = buzl0.getE();

            if(s.matches("[0-2][0-9]/[0-3][0-9]")) {
                String s1 = s.replace('/', '-');
                return s1.length() == 0 ? contact_eventValue(3, "--", "") : contact_eventValue(3, "--".concat(s1), "");
            }
        }
        else {
            s = getTimeStr(v);
            if(s.startsWith("0000-")) {
                s = TimeStrSub(s);
            }
        }

        return contact_eventValue(3, s, "");
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        String s1 = contentValues0.getAsString("data1");
        if(TextUtils.isEmpty(s1)) {
            return null;
        }

        buzl.Builder buzl0 = buzl.newBuilder();
        if(s1.startsWith("--")) {
            s1 = s1.matches("--[0-2][0-9]-[0-3][0-9]") ? s1.replace("--", "0000-") : s1;
        }

        Long long0 = timestr2long(s1);
        if(long0 == null) {
            buzl0.setE(s1);
        }
        else {
            long v = long0 == 0 ? 1 : long0;
            buzl0.setC(v);
        }

        bvba bvba0 = super.sourceid2bvba(sourceid);
        buzl0.setB(bvba0);

        return buzl0.build();
    }
}
