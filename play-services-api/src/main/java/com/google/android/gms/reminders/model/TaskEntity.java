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

package com.google.android.gms.reminders.model;

import org.microg.gms.common.PublicApi;
import org.microg.safeparcel.AutoSafeParcelable;

import java.util.Arrays;

@PublicApi
public class TaskEntity extends AutoSafeParcelable {
    public static Creator<TaskEntity> CREATOR = new AutoCreator<>(TaskEntity.class);
    @Field(2)
    public TaskIdEntity taskId;
    @Field(3)
    public Integer taskList;
    @Field(4)
    public String mTitle;
    @Field(6)
    public DateTimeEntity dueDate;
    @Field(7)
    public LocationEntity locationEntity;
    @Field(8)
    public DateTimeEntity eventDate;
    @Field(9)
    public Boolean archived;
    @Field(11)
    public Boolean deleted;
    @Field(12)
    public Long archivedTimeMs;
    @Field(13)
    public LocationGroupEntity locationGroupEntity;
    @Field(15)
    public Long locationSnoozedUntilMs;
    @Field(16)
    public byte[] extensions;
    @Field(17)
    public RecurrenceInfoEntity recurrenceInfoEntity;
    @Field(18)
    public byte[] assistance;
    @Field(19)
    public Long createdTimeMillis;
    @Field(20)
    public Integer s;
    @Field(22)
    public Boolean pinned;
    @Field(23)
    public Boolean snoozed;
    @Field(24)
    public Long snoozedTimeMillis;
    @Field(26)
    public ExternalApplicationLinkEntity externalApplicationLinkEntity;
    @Field(27)
    public Long u;
    @Field(1001)
    public Long v;

    public TaskEntity(){}

    public TaskEntity(TaskEntity task) {
        this.taskId = task.taskId;
        this.taskList = task.taskList;
        this.mTitle = task.mTitle;
        this.dueDate = task.dueDate;
        this.locationEntity = task.locationEntity;
        this.eventDate = task.eventDate;
        this.archived = task.archived;
        this.deleted = task.deleted;
        this.archivedTimeMs = task.archivedTimeMs;
        this.locationGroupEntity = task.locationGroupEntity;
        this.locationSnoozedUntilMs = task.locationSnoozedUntilMs;
        this.extensions = task.extensions;
        this.recurrenceInfoEntity = task.recurrenceInfoEntity;
        this.assistance = task.assistance;
        this.createdTimeMillis = task.createdTimeMillis;
        this.s = task.s;
        this.pinned = task.pinned;
        this.snoozed = task.snoozed;
        this.snoozedTimeMillis = task.snoozedTimeMillis;
        this.externalApplicationLinkEntity = task.externalApplicationLinkEntity;
        this.u = task.u;
        this.v = task.v;
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "taskId=" + taskId +
                ", taskList=" + taskList +
                ", mTitle='" + mTitle + '\'' +
                ", dueDate=" + dueDate +
                ", locationEntity=" + locationEntity +
                ", eventDate=" + eventDate +
                ", archived=" + archived +
                ", deleted=" + deleted +
                ", archivedTimeMs=" + archivedTimeMs +
                ", locationGroupEntity=" + locationGroupEntity +
                ", locationSnoozedUntilMs=" + locationSnoozedUntilMs +
                ", extensions=" + Arrays.toString(extensions) +
                ", recurrenceInfoEntity=" + recurrenceInfoEntity +
                ", assistance=" + Arrays.toString(assistance) +
                ", createdTimeMillis=" + createdTimeMillis +
                ", s=" + s +
                ", pinned=" + pinned +
                ", snoozed=" + snoozed +
                ", snoozedTimeMillis=" + snoozedTimeMillis +
                ", externalApplicationLinkEntity=" + externalApplicationLinkEntity +
                ", u=" + u +
                ", v=" + v +
                '}';
    }
}
