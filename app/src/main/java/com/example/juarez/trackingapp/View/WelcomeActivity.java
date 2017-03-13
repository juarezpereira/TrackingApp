package com.example.juarez.trackingapp.View;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.juarez.trackingapp.R;
import com.example.juarez.trackingapp.Utils.IntroManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_skip)
    Button btnSkip;

    private int[] layouts = new int[]{R.layout.welcome_side1, R.layout.welcome_side2, R.layout.welcome_side3};
    private TextView[] dots;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        addBottomDots(0);
        changeStatusBarColor();

        pagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.length - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText(getString(R.string.button_label_start));
                    btnSkip.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    btnNext.setText(getString(R.string.button_label_next));
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick(R.id.btn_skip) void onClickSkip(View view){
        IntroManager introManager = new IntroManager(this);
        introManager.setFirstLaunch(false);
        finish();
    }

    @OnClick(R.id.btn_next) void onClickNext(View view){
        int current = getItem(+1);

        if(current < layouts.length){
            viewPager.setCurrentItem(current);
        }else{
            IntroManager introManager = new IntroManager(this);
            introManager.setFirstLaunch(false);
            finish();
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public void addBottomDots(int currentPage){
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    private class ViewPagerAdapter extends PagerAdapter{

        private LayoutInflater mLayoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            mLayoutInflater = LayoutInflater.from(getBaseContext());

            View view = mLayoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

    }

}