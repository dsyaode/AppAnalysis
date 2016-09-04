package com.china.ds.appanalysis.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.percent.PercentLayoutHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.data.AppTotalData;

import java.util.List;

public class MyTotalItemRecyclerViewAdapter extends RecyclerView.Adapter<MyTotalItemRecyclerViewAdapter.ViewHolder> {

    private final List<AppTotalData> mValues;
    private final TotalItemFragment.OnListFragmentInteractionListener mListener;

    public MyTotalItemRecyclerViewAdapter(List<AppTotalData> items, TotalItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_totalitem, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.packageNameView.setText(mValues.get(position).appName);

        long time = mValues.get(position).times/1000;
        String str = "";
        if(time > 60*60){
            str = str + time/(60*60) + "h";
            time = time%(60*60);
        }
        if(time >= 60 || !str.isEmpty()){
            str = str + time/(60) + "m";
            time = time%(60);
        }
        str = str + time%(60) + "s";

        holder.mContentView.setText(str);
        ((PercentLayoutHelper.PercentLayoutParams)holder.backgroudView.getLayoutParams())
                .getPercentLayoutInfo().widthPercent=(((float)holder.mItem.times)/holder.mItem.maxTimes);

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
        public final TextView mContentView;
        public final ImageView backgroudView;
        public AppTotalData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            packageNameView = (TextView) view.findViewById(R.id.packagename);
            mContentView = (TextView) view.findViewById(R.id.time);
            backgroudView = (ImageView) view.findViewById(R.id.backgroud);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
