package com.china.ds.appanalysis.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.fragment.AppItemFragment;
import com.china.ds.appanalysis.fragment.MainFragment;
import com.china.ds.appanalysis.fragment.TotalItemFragment;
import com.china.ds.appanalysis.service.MonitorService;
import com.china.ds.appanalysis.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private LinearLayout viewGroup;
    private ViewPager viewPager;
    private ViewGroup tab;
    private Fragment[] fragments;

    private int width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        initData();
    }

    public void initView(){
        viewGroup = (LinearLayout) this.findViewById(R.id.viewgroup);
        tab = (ViewGroup) this.findViewById(R.id.tab);
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);

        fragments = new Fragment[]{MainFragment.newInstance(2), new AppItemFragment(), new TotalItemFragment()};
        viewPager.setAdapter(new MyFragmentPagerAdapter(this.getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(this);

        final ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                width = viewGroup.getWidth();
                tab.getLayoutParams().width = width/fragments.length;
                tab.invalidate();
                viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    public void initData(){
//        MonitorService.startService(MainActivity.this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int w =  width/fragments.length;
        int x = (int) (position*w+positionOffset*w);
        tab.setX(x);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        checkPermission();
        initData();
    }

    private void showTips(){
        Toast.makeText(this, "已经打开相应权限", Toast.LENGTH_LONG).show();
    }
    private boolean hasOpen = false;
    private void checkPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            showTips();
            return;
        }

        if(hasOpen){
            return;
        }
        hasOpen = true;

        //如果是5.0以上则需要设置查看其它应用的权限
        //没有查看其它app使用情况
        boolean ret = PermissionUtil.isPermissionAllow(this, AppOpsManager.OPSTR_GET_USAGE_STATS);
        if(ret){
            showTips();
            return;
        }

        final String action = Settings.ACTION_USAGE_ACCESS_SETTINGS;
        ret = PermissionUtil.hasActivityByAction(this, action);
        if(!ret){
            showTips();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_privacy_lock_permission);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener(){

            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hasOpen = false;
                MainActivity.this.finish();
                MonitorService.stopService(MainActivity.this);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hasOpen = false;
                Intent intent = new Intent(action);
                startActivity(intent);
            }
        });
        builder.create().show();

    }
}
