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
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TotalItemFragment extends TotalItemBaseFragment{

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TotalItemFragment newInstance(int columnCount) {
        TotalItemFragment fragment = new TotalItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onListFragmentInteraction(AppTotalData item) {

    }

    @Override
    public RecyclerView.Adapter getAdapter(List<AppTotalData> items, OnListFragmentInteractionListener listener) {
        return new MyTotalItemRecyclerViewAdapter(items, listener);
    }
}
