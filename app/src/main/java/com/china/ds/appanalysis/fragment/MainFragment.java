package com.china.ds.appanalysis.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.china.ds.appanalysis.data.AppTotalData;

import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class MainFragment extends TotalItemBaseFragment {

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MainFragment newInstance(int columnCount) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onListFragmentInteraction(AppTotalData item) {
        if(item.packageName.equals(this.getActivity().getPackageName())){
            return;
        }
        PackageManager packageManager = this.getActivity().getPackageManager();
        Intent intent=new Intent();
        intent = packageManager.getLaunchIntentForPackage(item.packageName);
        if(intent==null){
            System.out.println("APP not found!");
            return;
        }
        startActivity(intent);
    }

    @Override
    public RecyclerView.Adapter getAdapter(List<AppTotalData> items, OnListFragmentInteractionListener listener) {
        return new MainTotalItemRecyclerViewAdapter(items, listener);
    }
}
