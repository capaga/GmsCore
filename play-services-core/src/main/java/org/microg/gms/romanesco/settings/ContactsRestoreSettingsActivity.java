package org.microg.gms.romanesco.settings;


import androidx.appcompat.app.ActionBar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ContactsRestoreSettingsActivity extends AppCompatActivity {
    public static final String ACTION_RESTORE_CONTACTS = "org.microg.gms.romanesco.action.RESTORE_CONTACTS";
    ResotreSettingsListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_restore_settings);
        Toolbar toolbar = findViewById(R.id.restore_settings_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.restore_contacts));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.restore_settings_list);
        List<String> dataList = new ArrayList<>();
        if (adapter == null) {
            adapter = new ResotreSettingsListAdapter(this, android.R.layout.simple_list_item_1, dataList, Objects.requireNonNull(getIntent().getExtras()).getString("authAccount"), this);

        }
        listView.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if(item.getItemId()==R.id.restore_contacts_feedback){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://support.google.com/nexus/answer/7199294")));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No browser application found to open the link.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getExtras() != null) {
            if (data.getExtras().getString("authAccount") != null) {
                adapter.setAccount(data.getExtras().getString("authAccount"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restore_contact_menu, menu);
        return true;
    }
}
