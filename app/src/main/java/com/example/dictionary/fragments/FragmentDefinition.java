package com.example.dictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dictionary.R;
import com.example.dictionary.WordMeaningActivity;

public class FragmentDefinition extends Fragment {
    public FragmentDefinition() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition,container, false);//Inflate Layout
        Context context = getContext();
        TextView textView = (TextView) view.findViewById(R.id.textViewD);
        String en_definition = ((WordMeaningActivity)context).en_definition;
        if(en_definition==null){
            textView.setText("not found this word");
        }
        else{
            textView.setText(en_definition);
        }
        return view;
    }
}
