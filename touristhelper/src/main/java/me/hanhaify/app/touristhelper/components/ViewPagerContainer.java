package me.hanhaify.app.touristhelper.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.hanhaify.app.touristhelper.R;

public class ViewPagerContainer extends FrameLayout {

    private ViewPager viewPager;

    public ViewPagerContainer(Context context) {
        super(context);
        initUi();
    }

    public ViewPagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public ViewPagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initUi();
    }

    private void initUi() {
        setClipChildren(false);
        setLayerType();

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_pager_container, this, true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(container.getContext());
                imageView.setImageDrawable(container.getResources().getDrawable(R.drawable.demo_image));
                viewPager.addView(imageView, 0);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeViewAt(position);
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return viewPager.dispatchTouchEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setLayerType() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }
}
