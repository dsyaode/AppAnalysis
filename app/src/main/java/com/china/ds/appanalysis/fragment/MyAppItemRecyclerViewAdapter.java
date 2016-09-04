package com.china.ds.appanalysis.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.china.ds.appanalysis.OnListFragmentInteractionListener;
import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.data.AppUseRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppUseRecord} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAppItemRecyclerViewAdapter extends RecyclerView.Adapter<MyAppItemRecyclerViewAdapter.ViewHolder> {

    private final List<AppUseRecord> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyAppItemRecyclerViewAdapter(List<AppUseRecord> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_appitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.packageNameView.setText(mValues.get(position).packageName);
        String time = "";
        long startTime = mValues.get(position).startTime;
        long endTime = mValues.get(position).endTime;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String startTimeStr = format.format(new Date(startTime));
        String endTimeStr = format.format(new Date(endTime));
        holder.timeView.setText(startTimeStr + "-" + endTimeStr  + "(" + (endTime-startTime)/1000  + "s)");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView packageNameView;
        public final TextView timeView;
        public AppUseRecord mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            packageNameView = (TextView) view.findViewById(R.id.packagename);
            timeView = (TextView) view.findViewById(R.id.time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + timeView.getText() + "'";
        }
    }
}
