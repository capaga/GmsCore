package org.microg.gms.romanesco.settings;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.gms.R;

import java.util.List;

public class ResotreSettingsListAdapter extends ArrayAdapter<String> {
    private final Activity setting_activity;
    private String account;
    private TextView selectAccountSummary;

    public ResotreSettingsListAdapter(Context context, int resource, List<String> objects, String account , Activity activity) {
        super(context, resource, objects);
        this.account = account;
        this.setting_activity = activity;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        switch (position){
            case 0 :
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.restore_contacts_account_select, parent, false);
                }
                selectAccountSummary = convertView.findViewById(R.id.restore_contacts_account_select_summary);
                selectAccountSummary.setText(account);
                convertView.setOnClickListener(v -> {
                    Intent intent = new Intent("com.google.android.gms.common.account.CHOOSE_ACCOUNT");
                    intent.setPackage("com.google.android.gms");
                    intent.putExtra("allowableAccounts", (String) null);
                    intent.putExtra("allowableAccountTypes", new String[]{"com.google"});
                    intent.putExtra("addAccountOptions", (String) null);
                    intent.putExtra("selectedAccount", account);
                    intent.putExtra("alwaysPromptForAccount", true);
                    intent.putExtra("descriptionTextOverride", (String) null);
                    intent.putExtra("authTokenType", (String) null);
                    intent.putExtra("addAccountRequiredFeatures", (String) null);
                    intent.putExtra("setGmsCoreAccount", false);
                    intent.putExtra("overrideTheme", 0);
                    intent.putExtra("overrideCustomTheme", 0);
                    intent.putExtra("hostedDomainFilter", (String) null);
                    setting_activity.startActivityForResult(intent, 1);
                });
                return convertView;
            case 1 :
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.restore_contacts_title,parent,false);
                }
                TextView title = convertView.findViewById(R.id.restore_contacts_title_title);

                title.setText( setting_activity.getString(R.string.restore_contacts_google_contacts));
                TextView summary2 = convertView.findViewById(R.id.restore_contacts_title_summary);
                summary2.setText(setting_activity.getString(R.string.restore_contacts_summary));
                return convertView;
            case 2 :
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.restore_contacts_title,parent,false);
                }
                TextView title2 = convertView.findViewById(R.id.restore_contacts_title_title);
                title2.setText(setting_activity.getString(R.string.restore_contacts_device_backup));
                TextView summary3 = convertView.findViewById(R.id.restore_contacts_title_summary);
                summary3.setText(setting_activity.getString(R.string.restore_contacts_summary2));
                return convertView;
        }
        return new View(getContext());
    }

    public void setAccount(String account){
        selectAccountSummary.setText(account);
    }
}
