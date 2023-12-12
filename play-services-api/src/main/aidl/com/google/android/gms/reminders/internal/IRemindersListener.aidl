package com.google.android.gms.reminders.internal;
import com.google.android.gms.common.data.DataHolder;

interface IRemindersListener {
    void createTasks(in DataHolder holder) = 1;
    void sendTaskNotifications(in DataHolder holder) = 2;
    void UnknowFunD(in DataHolder holder) = 3;
}