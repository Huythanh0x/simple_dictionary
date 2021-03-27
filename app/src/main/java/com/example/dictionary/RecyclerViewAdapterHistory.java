package com.example.dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.HistoryViewHolder> {
    private ArrayList<History> histories;
    Context context;

    public RecyclerViewAdapterHistory(ArrayList<History> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }
    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView enWord;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            enWord = itemView.findViewById(R.id.en_word);

        }

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_layout, parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.enWord.setText(histories.get(position).getEn_word());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }
}
