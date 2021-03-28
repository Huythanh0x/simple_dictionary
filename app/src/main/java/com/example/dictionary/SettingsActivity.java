package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    DatabaseHelper mydatabasehelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView clearHistory = findViewById(R.id.clear_history);
        clearHistory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mydatabasehelper = new DatabaseHelper(SettingsActivity.this);
                try{
                    mydatabasehelper.openDatabase();
                }catch (Exception e){
                    e.printStackTrace();
                }
                showAlertDialog();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
    }
    public void showAlertDialog(){
        AlertDialog.Builder d = new AlertDialog.Builder(SettingsActivity.this);
        d.setTitle("Are you sure");
        d.setMessage("it will eliminate all of historical word");
        String positiveText = "OK";
        d.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mydatabasehelper.clearHistory();
                Toast.makeText(getApplicationContext(),"Histories was eliminated",Toast.LENGTH_LONG).show();
            }
        });
        String negativeText = "Cancel";
        d.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
