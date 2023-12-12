package org.microg.gms.people.converter;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.MessageLite;

import org.microg.gms.people.bvag;
import org.microg.gms.people.bvap;
import org.microg.gms.people.bvaq;
import org.microg.gms.people.bvau;
import org.microg.gms.people.bvba;

import java.util.ArrayList;
import java.util.List;

public class multiConverter extends BaseConverter{
    @Override
    public bvba getbvalue(MessageLite message0) {
        return null;
    }

    @Override
    public ContentValues toContentValues(MessageLite message0, Boolean bool0) {
        return null;
    }

    @Override
    public MessageLite toProtobuf(ContentValues contentValues0, String sourceid) {
        return null;
    }

    public void toContentValues(List<bvag> fList, List<bvap> lList, List<bvaq> nList, List<bvau> xList, ArrayList<ContentValues> personData) {
        ContentValues contentValues0 = new ContentValues();
        if (!fList.isEmpty()) {
            String s = ((bvag) fList.get(0)).getC();
            Integer integer0 = (Integer) BaseConverter.sexType.get(s);
            if (integer0 != null) {
                contentValues0.put("data3", integer0);
            }
        }

        for (Object object0 : lList) {
            bvap bvap0 = (bvap) object0;
            int v = bvap0.getD() == 0 ? 1 : bvap0.getD();
            String s1 = bvap0.getC();
            if (v == 1) {
                continue;
            }

            switch (v) {
                case 2: {
                    contentValues0.put("data5", s1);
                    continue;
                }
                case 3: {
                    contentValues0.put("data10", s1);
                    continue;
                }
                case 4: {
                    contentValues0.put("data4", s1);
                }
            }
        }

        if (!nList.isEmpty()) {
            contentValues0.put("data7", ((bvaq) nList.get(0)).getC());
        }

        for (Object object1 : xList) {
            bvau bvau0 = (bvau) object1;
            String s2 = bvau0.getD();
            String s3 = bvau0.getC();
            if (TextUtils.isEmpty(s2)) {
                continue;
            }

            switch (s2) {
                case "subject": {
                    contentValues0.put("data11", s3);
                    continue;
                }
                case "billinginformation": {
                    contentValues0.put("data1", s3);
                    continue;
                }
                case "directoryserver": {
                    contentValues0.put("data2", s3);
                    continue;
                }
                case "mileage": {
                    contentValues0.put("data6", s3);
                    continue;
                }
                case "priority": {
                    try {
                        contentValues0.put("data8", ((Integer) byteTable.get((byte) Byte.parseByte(s3))));
                    } catch (NumberFormatException numberFormatException0) {
                        Log.e("FSA2_ProtoToPeopleUtil", "Getting priority value failed: " + s3);
                    }
                    continue;
                }
                case "sensitivity": {
                    try {
                        contentValues0.put("data9", ((Integer) byteTable2.get((byte) Byte.parseByte(s3))));
                    } catch (NumberFormatException numberFormatException1) {
                        Log.e("FSA2_ProtoToPeopleUtil", "Getting sensitivity value failed: " + s3);
                    }
                    continue;
                }
            }

            Integer integer1 = (Integer) BaseConverter.contactJotType.get(s2);
            if (integer1 == null) {
                continue;
            }

            ContentValues contentValues1 = new ContentValues();
            contentValues1.put("mimetype", "vnd.com.google.cursor.item/contact_jot");
            contentValues1.put("data2", integer1);
            checkNull(contentValues1, "data1", s3);
            personData.add(contentValues1);
        }

        if(contentValues0.size() > 0) {
            contentValues0.put("mimetype", "vnd.com.google.cursor.item/contact_misc");
            personData.add(contentValues0);
        }
    }
}
