package com.example.asus.halalfoodfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ReminderPlaceAdapter extends RecyclerView.Adapter<ReminderPlaceAdapter.ViewHolder> {

    private List<ReminderPlace> reminderPlaces;
    private Context context;

    public ReminderPlaceAdapter(List<ReminderPlace> reminderPlaces, Context context) {
        this.reminderPlaces = reminderPlaces;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_reminder, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReminderPlace reminderPlace = reminderPlaces.get(position);

        holder.mAddressTextView.setText(reminderPlace.getPlaceAdress());
        holder.mNameTextView.setText(reminderPlace.getPlaceName());



    }

    @Override
    public int getItemCount() {
        return reminderPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mNameTextView;
        public TextView mAddressTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView)itemView.findViewById(R.id.placeNameTextView);
            mAddressTextView = (TextView) itemView.findViewById(R.id.addressTextView);
        }
    }

}
