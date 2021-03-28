package com.example.dictionary;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.dictionary.fragments.FragmentAntonyms;
import com.example.dictionary.fragments.FragmentDefinition;
import com.example.dictionary.fragments.FragmentExample;
import com.example.dictionary.fragments.FragmentSynonyms;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;

public class WordMeaningActivity extends AppCompatActivity {

    private ViewPager viewPager;
    String enWord = null;
    TextToSpeech ttsp;
    DatabaseHelper myDatabaseHelper;
    Cursor cursor;
    public String en_definition,example,synonyms,antonyms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);
        enWord = getIntent().getExtras().getString("en_word");
        String action = getIntent().getAction();
        String type = getIntent().getType();

        if(Intent.ACTION_SEND.equals(action) && type.equals("text/plain")){{
                    String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                    if(sharedText!=null){
                        Pattern p = Pattern.compile("[A-Za-z]{1,25}");
                        Matcher m = p.matcher(sharedText);
                        if(m.matches()){
                            enWord = sharedText;
                        }else{
                            enWord = "Not available";
                        }

                    }
                }
        }
        Log.d("word check get",enWord);
        myDatabaseHelper = new DatabaseHelper(this);
        try{
            myDatabaseHelper.openDatabase();
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor = myDatabaseHelper.getMeaning(enWord);
        if(cursor.moveToFirst()){
            en_definition = cursor.getString(cursor.getColumnIndex("en_definition"));
            antonyms = cursor.getString(cursor.getColumnIndex("antonyms"));
            example = cursor.getString(cursor.getColumnIndex("example"));
            synonyms = cursor.getString(cursor.getColumnIndex("synonyms"));
            myDatabaseHelper.insertHistory(enWord);
        }else{
            enWord = "Not available";
        }
        ImageButton speakButton = findViewById(R.id.btnSpeak);
        speakButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Context context;
                TextToSpeech.OnInitListener listener;
                ttsp = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener(){
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int result = ttsp.setLanguage(Locale.getDefault());
                            if(result == TextToSpeech.LANG_MISSING_DATA || result ==TextToSpeech.LANG_NOT_SUPPORTED){
                                Log.e("Loading langues","can't load this language");
                            }
                            else{
                                ttsp.speak(enWord,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                        else{
                            Log.e("Can not load","ttsp");
                        }
                    }
                });
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(enWord);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        viewPager = findViewById(R.id.tab_viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        //tabLayout

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDefinition(), "Definition");
        adapter.addFrag(new FragmentSynonyms(), "Synonyms");
        adapter.addFrag(new FragmentAntonyms(), "Antonyms");
        adapter.addFrag(new FragmentExample(), "Example");
        viewPager.setAdapter(adapter);
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
