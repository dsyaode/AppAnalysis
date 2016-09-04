package com.china.ds.appanalysis.fragment;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.china.ds.appanalysis.OnListFragmentInteractionListener;
import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.data.AppUseRecord;
import com.china.ds.appanalysis.db.DBAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AppItemFragment extends Fragment implements OnListFragmentInteractionListener,
        LoaderManager.LoaderCallbacks< List<AppUseRecord> > {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    List<AppUseRecord> items;
    RecyclerView recyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppItemFragment() {
        items = new ArrayList<>();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AppItemFragment newInstance(int columnCount) {
        AppItemFragment fragment = new AppItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        this.getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appitem_list, container, false);
        recyclerView = (RecyclerView) view;

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyAppItemRecyclerViewAdapter(items, this));
        }
        return view;
    }

    @Override
    public void onListFragmentInteraction(AppUseRecord item) {

    }

    @Override
    public android.support.v4.content.Loader<List<AppUseRecord>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<AppUseRecord>>(this.getContext()) {
            @Override
            public List<AppUseRecord> loadInBackground() {
                return DBAccessor.queryAll(AppUseRecord.class, new String[]{"startTime"}, new boolean[]{false});
            }
            @Override
            protected void onStartLoading() {
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<AppUseRecord>> loader, List<AppUseRecord> data) {
        items.clear();
        items.addAll(data);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<AppUseRecord>> loader) {

    }
}
