package com.example.exoplayer;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private ViewPager viewpagerMedia;
    private VideoPagerAdapter videoPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        viewpagerMedia = findViewById(R.id.viewpager_media);

        // Initialize VideoPagerAdapter and set it to the ViewPager
        videoPagerAdapter = new VideoPagerAdapter();
        viewpagerMedia.setAdapter(videoPagerAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Rect scrollBounds = new Rect();
            scrollView.getHitRect(scrollBounds);

            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (viewpagerMedia != null) {
                    if (viewpagerMedia.getGlobalVisibleRect(scrollBounds)) {
                        if (scrollBounds.height() < viewpagerMedia.getHeight()) {
                            // ViewPager is partially visible
                            Log.d("ScrollView", "ViewPager is partially visible");
                            videoPagerAdapter.stopVideo();
                        } else {
                            Log.d("ScrollView", "ViewPager is fully visible");
                            videoPagerAdapter.playVideo();
                        }
                    } else {
                        Log.d("ScrollView", "ViewPager is not visible");
                        videoPagerAdapter.stopVideo();
                    }
                }
            });
        }

        scrollView.setOnFocusChangeListener((v, hasFocus) -> {
            checkViewPagerFocus();
        });
        viewpagerMedia.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // Pause all videos first
                videoPagerAdapter.stopVideo();

                // Play the video on the selected page
                videoPagerAdapter.playVideo();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


//        viewpagerMedia.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                // No action needed here
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                // When a new page is selected, stop any currently playing video
//                videoPagerAdapter.playVideo();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                // No action needed here
//            }
//        });
    }

    private void checkViewPagerFocus() {
        // Check if any child view within the ViewPager has focus
        if (viewpagerMedia.hasFocus() || viewpagerMedia.findFocus() != null) {
            Log.d("Focus", "ViewPager is likely in focus");
        } else {
            Log.d("Focus", "ViewPager is likely not in focus");
        }
    }
}
