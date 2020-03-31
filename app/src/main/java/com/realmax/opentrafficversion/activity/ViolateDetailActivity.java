package com.realmax.opentrafficversion.activity;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.bean.ViolateBean;
import com.realmax.opentrafficversion.bean.ViolateCarBean;
import com.realmax.opentrafficversion.dao.OpenTrafficQueryDao;
import com.realmax.opentrafficversion.fragment.ViolateFragment;

import java.util.ArrayList;
import java.util.List;

public class ViolateDetailActivity extends BaseActivity {
    private ViewPager vp_pager;
    private List<ViolateBean> violateBeans;
    private ArrayList<ViolateFragment> fragments;
    private FragmentStatePagerAdapter adapter;
    private Button btn_back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_violate_items;
    }

    @Override
    protected void initView() {
        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
        btn_back = (Button) findViewById(R.id.btn_back);
    }

    @Override
    protected void initEvent() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        fragments = new ArrayList<>();

        ArrayList<ViolateCarBean> violateCarBeans = new ArrayList<>();
        OpenTrafficQueryDao.queryForAll(violateCarBeans, new OpenTrafficQueryDao.Result() {
            @Override
            public void success(Object object) {
                for (ViolateCarBean violateCarBean : violateCarBeans) {
                    fragments.add(new ViolateFragment(violateCarBean));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomerAdapter customerAdapter = new CustomerAdapter(getSupportFragmentManager());
                        vp_pager.setAdapter(customerAdapter);
                        vp_pager.setOffscreenPageLimit(10);
                        vp_pager.setPageMargin(300);
                    }
                });
            }
        });
    }

    class CustomerAdapter extends FragmentStatePagerAdapter {

        public CustomerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
