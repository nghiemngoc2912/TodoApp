package com.example.todoapp.controller.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.HdrVH>{

    private final String title;
    private final TaskRowAdapter rowAd;

    public SectionAdapter(String title, TaskRowAdapter rowAd){
        this.title=title; this.rowAd=rowAd;
    }
    public String getTitle()        { return title; }
    public TaskRowAdapter getRowAd(){ return rowAd; }

    static class HdrVH extends RecyclerView.ViewHolder{
        TextView tv; RecyclerView rv;
        HdrVH(@NonNull View v){
            super(v);
            tv=v.findViewById(R.id.tvHeader);
            rv=v.findViewById(R.id.rvRows);
        }
    }

    @NonNull
    @Override public HdrVH onCreateViewHolder(@NonNull ViewGroup p, int v){
        View view= LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_section,p,false);
        return new HdrVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HdrVH h,int pos){
        h.tv.setText(title);
        if(h.rv.getAdapter()==null){
            h.rv.setLayoutManager(new LinearLayoutManager(h.rv.getContext()));
            h.rv.setAdapter(rowAd);
        }
    }
    @Override public int getItemCount(){ return 1; }
}
