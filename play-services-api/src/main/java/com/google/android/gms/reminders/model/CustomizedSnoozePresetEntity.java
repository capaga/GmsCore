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

import org.microg.safeparcel.AutoSafeParcelable;

public class CustomizedSnoozePresetEntity extends AutoSafeParcelable {
    public static Creator<CustomizedSnoozePresetEntity> CREATOR = new AutoCreator<>(CustomizedSnoozePresetEntity.class);
    @Field(2)
    public TimeEntity morningCustomizedTime;
    @Field(3)
    public TimeEntity afternoonCustomizedTime;
    @Field(4)
    public TimeEntity eveningCustomizedTime;

    public CustomizedSnoozePresetEntity(TimeEntity morningCustomizedTime, TimeEntity afternoonCustomizedTime, TimeEntity eveningCustomizedTime) {
        this.morningCustomizedTime = morningCustomizedTime;
        this.afternoonCustomizedTime = afternoonCustomizedTime;
        this.eveningCustomizedTime = eveningCustomizedTime;
    }

    public CustomizedSnoozePresetEntity() {
        this.morningCustomizedTime = null;
        this.afternoonCustomizedTime = null;
        this.eveningCustomizedTime = null;
    }

    @Override
    public String toString() {
        return "CustomizedSnoozePresetEntity{" +
                "morningCustomizedTime=" + morningCustomizedTime +
                ", afternoonCustomizedTime=" + afternoonCustomizedTime +
                ", eveningCustomizedTime=" + eveningCustomizedTime +
                '}';
    }
}