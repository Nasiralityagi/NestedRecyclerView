package com.nesstech.metube.fragment;


import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.nesstech.metube.R;
import com.nesstech.metube.Utility.Constant;
import com.nesstech.metube.youmodel.Item;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerPanelNew extends Fragment {

    private View view;
    private static final String KEY_DATA = "ITEM";
    private YouTubePlayer playerr;
    private ProgressBar progressBar;
    private Item item;
    private YouTubePlayerFragment youTubePlayerFragment;


    public PlayerPanelNew() {
        // Required empty public constructor
    }

    public static PlayerPanelNew newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, item);
        PlayerPanelNew fragment = new PlayerPanelNew();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (Item) getArguments().getSerializable(KEY_DATA);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       // View view = inflater.inflate(R.layout.fragment_player_panel_new, container, false);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_player_panel_new, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        progressBar = view.findViewById(R.id.progressBar);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadVideoPlayer();
    }

    public void loadVideoPlayer() {
        if(youTubePlayerFragment == null) {
            youTubePlayerFragment = (YouTubePlayerFragment) getActivity().getFragmentManager().findFragmentById(R.id.youtube_fragment);
            youTubePlayerFragment.initialize(Constant.api_key, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    onLoadError("Something went wrong!");
                }

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    playerr = player;
                    progressBar.setVisibility(View.GONE);
                    if (!wasRestored) {
                        player.cueVideo(item.getId());
                    }
                }
            });
        }else {
            playerr.cueVideo(item.getId());
        }
    }

    private void onLoadError(String error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        PlayerPanelNew f = (PlayerPanelNew) getFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        playerr.release();
        youTubePlayerFragment.onDetach();
        System.gc();
        super.onDetach();
    }
}
