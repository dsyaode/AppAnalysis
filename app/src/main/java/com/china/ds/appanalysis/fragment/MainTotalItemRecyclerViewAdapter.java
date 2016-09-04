package com.china.ds.appanalysis.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.percent.PercentLayoutHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.data.AppTotalData;

import java.util.List;
import java.util.Random;

public class MainTotalItemRecyclerViewAdapter extends RecyclerView.Adapter<MainTotalItemRecyclerViewAdapter.ViewHolder> {

    private final List<AppTotalData> mValues;
    private final TotalItemFragment.OnListFragmentInteractionListener mListener;
    private TypedArray colors;
    private int startIndex = 0;

    public MainTotalItemRecyclerViewAdapter(List<AppTotalData> items, TotalItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        startIndex = new Random().nextInt(10);
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        Resources res = recyclerView.getContext().getResources();
        //获取字符串资源
        colors = res.obtainTypedArray(R.array.item_bk);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_main_totalitem, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.packageNameView.setText(mValues.get(position).appName);

        float per = (((float)holder.mItem.times)/holder.mItem.maxTimes);
        per = 1 - (1-per)*(1-per);
        per += 0.2f;
        WindowManager wm = (WindowManager) holder.mView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Resources res = holder.mView.getContext().getResources();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int max = (int) (displayMetrics.heightPixels*(5.0f/12));
        holder.mView.getLayoutParams().height = (int) (max * per);

        holder.mView.setBackgroundColor(colors.getColor((position+startIndex)%colors.length(), res.getColor(R.color.colorAccent)));
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
        public final ImageView iconView;
        public AppTotalData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            packageNameView = (TextView) view.findViewById(R.id.packagename);
            iconView = (ImageView) view.findViewById(R.id.backgroud);
        }

    }
}
