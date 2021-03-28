package com.example.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    private static String DB_NAME = "eng_dictionary.db";
    private SQLiteDatabase myDatabase;
    private Context myContext;

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,1);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases";

        Log.e("Opened Database", "doing");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }
    public void createDatabase(){
        boolean exits = checkDB();
        if(!exits){
            this.getReadableDatabase();
            try{
                myContext.deleteDatabase(DB_NAME);
                copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error in copying DB");
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean checkDB(){
        SQLiteDatabase sqLiteDatabase = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            sqLiteDatabase = sqLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(sqLiteDatabase!= null){
            sqLiteDatabase.close();
        }
        return sqLiteDatabase != null?true:false;
    }
    public void copyDatabase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outputName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outputName);
        byte[] buffer = new byte[64];
        int length ;
        while((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.d("Debug","Copy database");
    }
    public void openDatabase(){
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(myDatabase!=null){
            myDatabase.close();
        }
        super.close();
    }
    public Cursor getMeaning(String s){
        Cursor c = myDatabase.rawQuery("SELECT en_definition,example,synonyms,antonyms FROM words WHERE en_word == UPPER('"+s+"')", null);
        return c;
    }
    public Cursor getSuggestion(String s){
        Cursor c = myDatabase.rawQuery("SELECT _id,en_word FROM words WHERE en_word LIKE  '"+s+"%' LIMIT 4",null);
        return c;
    }
    void insertHistory(String newWord){
        myDatabase.execSQL("INSERT INTO history(word) VALUES(UPPER('"+newWord+"'))");
    }

    public Cursor getHistory() {
        Cursor c = myDatabase.rawQuery("SELECT DISTINCT word,en_definition FROM history h JOIN words w ON h.word == w.en_word ORDER BY h._id DESC", null);
        return c;
    }
    public void clearHistory(){
        myDatabase.execSQL("DELETE FROM history");
    }
}
