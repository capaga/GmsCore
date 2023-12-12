/*
 * Copyright (C) 2013-2017 microG Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.reminders;

import org.microg.safeparcel.AutoSafeParcelable;

import java.util.ArrayList;

public class LoadRemindersOptions extends AutoSafeParcelable {
    @Field(value = 3, useDirectList = true)
    public ArrayList<String> clientAssignedIds;
    @Field(value = 4, useDirectList = true)
    public ArrayList<Integer> taskList;
    @Field(5)
    public Long startTimeTodayMidnight;
    @Field(6)
    public Long endTimeLaterMidnight;
    @Field(7)
    public Long e;
    @Field(8)
    public Long f;
    @Field(9)
    public boolean archived;
    @Field(10)
    public int h;
    @Field(11)
    public boolean isExceptional;
    @Field(12)
    public boolean recurrenceIdNotNull;
    @Field(13)
    public int reminderType;
    @Field(14)
    public int sortKey;
    @Field(value = 15, useDirectList = true)
    public ArrayList<String> recurrenceIds;
    @Field(16)
    public Long firedTimeMillisBegin;
    @Field(17)
    public Long firedTimeMillisEnd;

    public static Creator<LoadRemindersOptions> CREATOR = new AutoCreator<>(LoadRemindersOptions.class);

    @Override
    public String toString() {
        return "LoadRemindersOptions{" +
                "clientAssignedIds=" + clientAssignedIds +
                ", taskList=" + taskList +
                ", startTimeTodayMidnight=" + startTimeTodayMidnight +
                ", endTimeLaterMidnight=" + endTimeLaterMidnight +
                ", e=" + e +
                ", f=" + f +
                ", archived=" + archived +
                ", h=" + h +
                ", isExceptional=" + isExceptional +
                ", recurrenceIdNotNull=" + recurrenceIdNotNull +
                ", reminderType=" + reminderType +
                ", sortKey=" + sortKey +
                ", recurrenceIds=" + recurrenceIds +
                ", firedTimeMillisBegin=" + firedTimeMillisBegin +
                ", firedTimeMillisEnd=" + firedTimeMillisEnd +
                '}';
    }
}
