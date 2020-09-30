package com.nesstech.metube.fragment;


import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.nesstech.metube.R;
import com.nesstech.metube.youmodel.Item;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerPanel extends Fragment {

    private static final String KEY_DATA = "ITEM";
    private WebView webView;
    private boolean isAdded;
    private boolean isError;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private Item item;


    public PlayerPanel() {
        // Required empty public constructor
    }

    public static PlayerPanel newInstance(Item item) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, item);
        PlayerPanel fragment = new PlayerPanel();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player_panel, container, false);
        linearLayout = v.findViewById(R.id.adLayout);
        progressBar = v.findViewById(R.id.progressBar);
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadVideoPlayer();
    }

    public void loadVideoPlayer() {
        webView = new WebView(getActivity());
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(false);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollbarFadingEnabled(true);
        webView.setNetworkAvailable(true);

        webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                isError = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
                onLoadError(description.toString());
            }

            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                onLoadError(error.getDescription().toString());
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 99 && !isAdded && !isError) {
                    isAdded = true;
                    onLoadComplete();
                }
            }
        });

        webView.loadUrl("https://www.youtube.com/embed/" + item.getId());
    }

    private void onLoadError(String error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    private void onLoadComplete() {
        progressBar.setVisibility(View.GONE);
        if (isLoaded()) {
            linearLayout.setVisibility(View.VISIBLE);
            showVideoPlaying(linearLayout);
        }
    }

    public boolean isLoaded() {
        return isAdded;
    }

    public void showVideoPlaying(LinearLayout view) {
        if (view == null) {
            throw new NullPointerException("LinearLayout should not be null");
        }
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.addView(webView);
    }

    @Override
    public void onDetach() {
        if (webView != null) {
            webView.loadUrl(null);
            webView.stopLoading();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }
        System.gc();
        super.onDetach();
    }
}
