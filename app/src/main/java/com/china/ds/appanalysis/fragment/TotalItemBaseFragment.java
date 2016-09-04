package com.china.ds.appanalysis.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.data.AppTotalData;
import com.china.ds.appanalysis.data.AppUseRecord;
import com.china.ds.appanalysis.db.DBAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/9/2.
 */
public abstract class TotalItemBaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<AppTotalData>>{

    // TODO: Customize parameter argument names
    public static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    List<AppTotalData> items;
    TotalItemFragment.OnListFragmentInteractionListener listener;
    RecyclerView recyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TotalItemBaseFragment() {
        items = new ArrayList<>();
        listener = new TotalItemFragment.OnListFragmentInteractionListener(){

            @Override
            public void onListFragmentInteraction(AppTotalData item) {
               TotalItemBaseFragment.this.onListFragmentInteraction(item);
            }
        };
    }

    public abstract void onListFragmentInteraction(AppTotalData item);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        this.getLoaderManager().initLoader(2, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_totalitem_list, container, false);
        recyclerView = (RecyclerView) view;
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            }
            recyclerView.setAdapter(getAdapter(items, listener));
        }
        return view;
    }

    public abstract RecyclerView.Adapter getAdapter(List<AppTotalData> items, TotalItemFragment.OnListFragmentInteractionListener listener);

    @Override
    public android.support.v4.content.Loader<List<AppTotalData>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<AppTotalData>>(this.getContext()) {
            @Override
            public List<AppTotalData> loadInBackground() {
                List<AppUseRecord> records = DBAccessor.queryAll(AppUseRecord.class, new String[]{"startTime"}, new boolean[]{false});
                HashMap<String, Long> datas = new HashMap<>();
                long maxTime = 0;
                for (AppUseRecord record : records){
                    long time = 0;
                    if(datas.containsKey(record.packageName)){
                        time = datas.get(record.packageName) + (record.endTime-record.startTime);
                    }else{
                        time = (record.endTime-record.startTime);
                    }
                    if(maxTime < time){
                        maxTime = time;
                    }
                    datas.put(record.packageName, time);
                }
                List<AppTotalData> appTotalDatas = new ArrayList<>();
                Set<String> strs = datas.keySet();

                PackageManager pm = this.getContext().getPackageManager();

                for (String str : strs){
                    AppTotalData data = new AppTotalData();
                    try {
                        PackageInfo info = pm.getPackageInfo(str, 0);
                        data.appName = info.applicationInfo.loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    data.packageName = str;
                    data.times = datas.get(str);
                    data.maxTimes = maxTime;
                    appTotalDatas.add(data);
                }
                Collections.sort(appTotalDatas, new Comparator<AppTotalData>() {
                    @Override
                    public int compare(AppTotalData appTotalData, AppTotalData t1) {
                        return (int) (t1.times-appTotalData.times);
                    }
                });
                return appTotalDatas;
            }

            @Override
            protected void onStartLoading() {
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<AppTotalData>> loader, List<AppTotalData> data) {
        items.clear();
        items.addAll(data);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<AppTotalData>> loader) {

    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(AppTotalData item);
    }

}

