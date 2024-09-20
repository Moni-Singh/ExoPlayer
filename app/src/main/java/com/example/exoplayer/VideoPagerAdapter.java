package com.example.exoplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
public class VideoPagerAdapter extends PagerAdapter {
    private static final int TYPE_MP4 = 1;
    private static final int TYPE_VIMEO = 0;
    private static final int TYPE_YOUTUBE = 2;

    // Member variables to track video state
    private ExoPlayer currentExoPlayer;
    private YouTubePlayer currentYouTubePlayer;
    private WebView currentVimeoWebView;
    private boolean isVideoPlaying = false;

    @Override
    public int getCount() {
        return 3; // Number of pages
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view;

        switch (position) {
            case TYPE_MP4:
                view = inflater.inflate(R.layout.mp4_page, container, false);
                StyledPlayerView exoPlayerView = view.findViewById(R.id.exo_player_view);
                FrameLayout layoutVideoPagerItem = view.findViewById(R.id.layout_video_pager_item);

                // Initialize ExoPlayer if it is null
                if (currentExoPlayer == null) {
                    currentExoPlayer = new ExoPlayer.Builder(container.getContext()).build();
                }

                exoPlayerView.setPlayer(currentExoPlayer);

                // Set up MP4 video URL
                String mp4Url = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4";
                MediaItem mediaItem = MediaItem.fromUri(mp4Url);

                // Re-prepare the player with the media item
                currentExoPlayer.setMediaItem(mediaItem);
                currentExoPlayer.prepare();
                currentExoPlayer.setPlayWhenReady(false);  // Start paused

                layoutVideoPagerItem.setOnClickListener(v -> {
                    currentExoPlayer.setPlayWhenReady(true);
                    layoutVideoPagerItem.setVisibility(View.GONE);
                });

                break;
            case TYPE_VIMEO:
                view = inflater.inflate(R.layout.vimeo_page, container, false);
                currentVimeoWebView = view.findViewById(R.id.vimeo_web_view);

                WebSettings webSettings = currentVimeoWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                currentVimeoWebView.setWebViewClient(new WebViewClient());

                String vimeoVideoUrl = "https://player.vimeo.com/video/76979871";
                currentVimeoWebView.loadUrl(vimeoVideoUrl);

                ImageView playButton = view.findViewById(R.id.image_view_placeholder);
                playButton.setOnClickListener(v -> playVideo());
                break;


            case TYPE_YOUTUBE:
                view = inflater.inflate(R.layout.you_tube_page, container, false);
                YouTubePlayerView youTubePlayerView = view.findViewById(R.id.youtube_player_view);
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        // Load a sample YouTube video
                        youTubePlayer.loadVideo("https://www.youtube.com/shorts/p_M9Uf-8SIE", 0);
                        currentYouTubePlayer = youTubePlayer;
                    }
                });
                break;

            default:
                throw new IllegalArgumentException("Invalid position");
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // Remove the view and release only the players associated with the current position
        View view = (View) object;
        container.removeView(view);

        if (position == TYPE_MP4 && currentExoPlayer != null) {
            currentExoPlayer.stop();  // Stop instead of release
        }

        if (position == TYPE_YOUTUBE && currentYouTubePlayer != null) {
            currentYouTubePlayer.pause();  // Pause YouTube player
        }

        if (position == TYPE_VIMEO && currentVimeoWebView != null) {
            currentVimeoWebView.stopLoading();
            currentVimeoWebView.destroy();
        }
    }

    public void playVideo() {
        // Play video based on the player type
        if (currentExoPlayer != null) {
            currentExoPlayer.play();
        }

        if (currentYouTubePlayer != null) {
            currentYouTubePlayer.play();
        }

        if (currentVimeoWebView != null) {
            currentVimeoWebView.evaluateJavascript(
                    "document.querySelector('iframe').contentWindow.postMessage('{\"method\":\"play\"}', '*');",
                    null
            );
        }
        isVideoPlaying = true;
    }

    public void stopVideo() {
        if (currentExoPlayer != null) {
            currentExoPlayer.pause();
        }

        if (currentYouTubePlayer != null) {
            currentYouTubePlayer.pause();
        }

        if (currentVimeoWebView != null) {
            currentVimeoWebView.evaluateJavascript(
                    "document.querySelector('iframe').contentWindow.postMessage('{\"method\":\"pause\"}', '*');",
                    null
            );
        }

        isVideoPlaying = false;
    }

}
