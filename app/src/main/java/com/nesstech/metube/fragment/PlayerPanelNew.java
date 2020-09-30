package com.nesstech.metube.fragment;


import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.nesstech.metube.R;
import com.nesstech.metube.Utility.Constant;
import com.nesstech.metube.youmodel.Item;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerPanelNew extends Fragment {

    private static final String KEY_DATA = "ITEM";
    private Item item;
    private FragmentActivity myContext;
    private YouTubePlayer YPlayer;
    private static final String YoutubeDeveloperKey = "xyz";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    public void onAttach(Activity activity) {

        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;
        }

        super.onAttach(activity);
    }

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
        View rootView = inflater.inflate(R.layout.fragment_player_panel_new, container, false);
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(Constant.api_key, new OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    //YPlayer.setFullscreen(true);
                    YPlayer.loadVideo(item.getId());
                    YPlayer.play();
                }
            }
            @Override
            public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
                onLoadError("Something went wrong!");
            }
        });
        return rootView;
    }


    private void onLoadError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        YPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        YPlayer.release();
    }
}
