package com.google.android.gms.backup;

interface IBackupAccountManagerService{
    Account getAccount() = 0;
    void setAccount(in Account account) = 1;
    boolean isServiceEnabled() = 2;
}