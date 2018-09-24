package com.example.asus.halalfoodfinder;


import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class DayHoursAdapter extends RecyclerView.Adapter<DayHoursAdapter.ViewHolderDayHours>{

    List<DayHours> listItem;
    private Context context;

    public DayHoursAdapter(List<DayHours> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }




    @NonNull
    @Override
    public ViewHolderDayHours onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolderDayHours(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDayHours holder, int position) {
         DayHours dayHours = listItem.get(position);

         holder.mDayHourTextView.setText(dayHours.getDayHours());

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolderDayHours extends RecyclerView.ViewHolder
    {
        public TextView mDayHourTextView;

        public ViewHolderDayHours(View itemView) {
            super(itemView);

            mDayHourTextView = (TextView) itemView.findViewById(R.id.dayElement);
        }
    }

}
