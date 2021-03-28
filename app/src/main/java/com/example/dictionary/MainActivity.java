package com.example.dictionary;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static DatabaseHelper databaseHelper;
    static boolean isOpened = false;
    SearchView search;
    androidx.cursoradapter.widget.SimpleCursorAdapter cursorAdapter;

    ArrayList<History> histories = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter historyAdapter;

    RelativeLayout emptyHistory;
    Cursor historyCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (SearchView) findViewById(R.id.search_view);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });

        databaseHelper = new DatabaseHelper(this);

        if (databaseHelper.checkDB()) {
            openDatabase();
        } else {
            LoadDatabaseAsync loadDatabaseAsync = new LoadDatabaseAsync(MainActivity.this);
            loadDatabaseAsync.execute();
        }
        final String[] from = new String[]{"en_word"};
        final int[] to = new int[]{R.id.suggestion_text};

        cursorAdapter = new SimpleCursorAdapter(MainActivity.this,R.layout.suggestion_row,null,from,to,0){
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };
        search.setSuggestionsAdapter(cursorAdapter);
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
             public boolean onSuggestionSelect(int position) {

                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clickedWord = cursor.getString(cursor.getColumnIndex("en_word"));
                search.setQuery(clickedWord,false);


                search.clearFocus();
                search.setFocusable(false);

                Intent intent = new Intent(MainActivity.this,WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",clickedWord);
                Log.d("word check set",clickedWord);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clickedWord = cursor.getString(cursor.getColumnIndex("en_word"));
                search.setQuery(clickedWord,false);


                search.clearFocus();
                search.setFocusable(false);

                Intent intent = new Intent(MainActivity.this,WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",clickedWord);
                Log.d("word check set",clickedWord);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Cursor c = databaseHelper.getMeaning(query);
                if(c.getCount() == 0){
                    search.setQuery("",false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("No Word Was Found");
                    builder.setMessage("Please search another");
                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            search.clearFocus();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this,WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",query);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search.setIconified(false);
                Cursor cursorSuggestion = databaseHelper.getSuggestion(newText);
                cursorAdapter.swapCursor(cursorSuggestion);
                return false;
            }
        });

        emptyHistory = findViewById(R.id.emptyHistory);
        recyclerView = findViewById(R.id.recycle_view_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        fetch_history();

    }
    public void fetch_history(){
        histories = new ArrayList<>();
        historyAdapter = new RecyclerViewAdapterHistory(histories,this);
        recyclerView.setAdapter(historyAdapter);
        History h;
        if(isOpened){
            historyCursor = databaseHelper.getHistory();
            if(historyCursor.moveToFirst()){
                do{
                    h = new History(historyCursor.getString(historyCursor.getColumnIndex("word")));
                    histories.add(h);
                }while(historyCursor.moveToNext());
            }
            historyAdapter.notifyDataSetChanged();
        }
        if(histories.size()==0){
            emptyHistory.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else{
            emptyHistory.setVisibility(View.GONE);
        }

    }

    protected static void openDatabase() {
        try {
            databaseHelper.openDatabase();
            isOpened = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_exit) {
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        fetch_history();
        super.onResume();
    }
}
