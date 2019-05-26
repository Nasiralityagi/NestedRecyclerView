package com.nesstech.metube.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nesstech.metube.R;
import com.nesstech.metube.adapter.HorizontalRVListAdapter;
import com.nesstech.metube.adapter.SiteItemAdapter;
import com.nesstech.metube.adapter.VerticalRVListAdapter;
import com.nesstech.metube.fragment.MainFragment;
import com.nesstech.metube.fragment.PlayerPanel;
import com.nesstech.metube.fragment.PlayerPanelNew;
import com.nesstech.metube.fragment.VListPanel;
import com.nesstech.metube.github.pedrovgs.DraggableListener;
import com.nesstech.metube.github.pedrovgs.DraggablePanel;
import com.nesstech.metube.model.ModelSites;
import com.nesstech.metube.model.SectionDataModel;
import com.nesstech.metube.youmodel.Item;

import java.util.List;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements
        VerticalRVListAdapter.SetViewMoreClickListener,
        VerticalRVListAdapter.SetPagerItemListener,
        SiteItemAdapter.SetSiteItemListener,
        HorizontalRVListAdapter.SetVideoClickListener {


    private DraggablePanel draggablePanel;

    private Toolbar toolbar;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private ViewGroup playerParent;
    private PlayerPanelNew topFragment;
    private VListPanel bottomFragment;
    private boolean isLandScapeMode;

    public static void start(Context context) {
        context.startActivity(new Intent(context,MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_youtube));
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        draggablePanel = findViewById(R.id.draggable_panel);
        draggablePanelListnerInit();

        Fragment selectedScreen = getSupportFragmentManager().findFragmentById(R.id.container);
        if (selectedScreen == null) {
            selectedScreen = MainFragment.createFor();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.container, selectedScreen, "")
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .attach(selectedScreen)
                    .commit();
        }
        /*Material Drawer*/
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScapeMode = true;
            toolbar.setVisibility(View.GONE);
            playerParent = (ViewGroup) findViewById(R.id.drag_view);
            if (topFragment != null) {
                View playerView = topFragment.getView();
                if (playerView != null && draggablePanel != null) {
                    ((ViewGroup) playerView.getParent()).removeView(playerView);
                    draggablePanel.addView(playerView, playerView.getLayoutParams());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }
                }
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandScapeMode = false;
            toolbar.setVisibility(View.VISIBLE);
            playerParent = (ViewGroup) findViewById(R.id.drag_view);
            if (topFragment != null) {
                View playerView = topFragment.getView();
                if (playerView != null && playerParent != null) {
                    ((ViewGroup) playerView.getParent()).removeView(playerView);
                    playerParent.addView(playerView, playerView.getLayoutParams());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            }
        }

        super.onConfigurationChanged(newConfig);
    }

    /*void setListShown(boolean shown) {
        recyclerView.setVisibility(shown ? View.VISIBLE : View.GONE);
        progress.setVisibility(shown ? View.GONE : View.VISIBLE);
        empty.setVisibility(shown && adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }*/

    private void draggablePanelListnerInit() {
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {

            }

            @Override
            public void onMinimized() {

            }

            @Override
            public void onClosedToLeft() {
                refreshDraggablePanel();
            }

            @Override
            public void onClosedToRight() {
                refreshDraggablePanel();
            }
        });

    }

    private void refreshDraggablePanel() {
        draggablePanel.removeFragment();
        draggablePanel.removeAllViews();//this method remove all views from drag view
        draggablePanel.setVisibility(View.GONE);
    }

    private void setNavigationDrawer(Bundle savedInstanceState) {
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }


    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.search, menu);
        //MenuItem item = menu.findItem(R.id.menu_icon_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.menu_icon_search:
                startActivity(new Intent(this,SearchActivity.class));
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (isLandScapeMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (draggablePanel.isShown()) {
            if (draggablePanel.isMaximized()) {
                draggablePanel.minimize();
            } else if (draggablePanel.isMinimized()) {
                draggablePanel.closeToRight();
            }
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count > 0) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment instanceof VListPanel) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        break;
                    }
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onPagerItemClick(Item item) {
        onVideoItemClick(item);
    }

    @Override
    public void onVideoItemClick(Item item) {
        /*Like Youtube Dragable Panel*/
        topFragment = PlayerPanelNew.newInstance(item);
        bottomFragment = VListPanel.newInstance(item.getSnippet().getCategoryId());
        setUpDragablePanel(item);
    }


    @Override
    public void onMoreIconClick(Item result) {
        String videoTitle = result.getSnippet().getTitle() == null ? "Unknown" : result.getSnippet().getTitle();
        Toast.makeText(this, videoTitle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoItemLongClick(Item item) {
        onVideoItemClick(item);
    }

    private void setUpDragablePanel(Item item) {
        if (draggablePanel.getVisibility() == View.VISIBLE) {
            refreshDraggablePanel();
        }
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(topFragment);
        draggablePanel.setBottomFragment(bottomFragment);
        draggablePanel.setClickToMaximizeEnabled(true);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.x_scale_factor, typedValue, true);
        float xScaleFactor = typedValue.getFloat();
        typedValue = new TypedValue();
        getResources().getValue(R.dimen.y_scale_factor, typedValue, true);
        float yScaleFactor = typedValue.getFloat();
        draggablePanel.setXScaleFactor(xScaleFactor);
        draggablePanel.setYScaleFactor(yScaleFactor);
        draggablePanel.setTopViewHeight(getResources().getDimensionPixelSize(R.dimen.top_fragment_height));
        draggablePanel.setTopFragmentMarginRight(getResources().getDimensionPixelSize(R.dimen.top_fragment_margin));
        draggablePanel.setTopFragmentMarginBottom(getResources().getDimensionPixelSize(R.dimen.top_fragment_margin));
        draggablePanel.initializeView();
        draggablePanel.maximize();
        draggablePanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSiteItemClick(ModelSites item) {
        Toast.makeText(this, item.text + " under construction", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewMoreClick(SectionDataModel model) {
        bottomFragment = VListPanel.newInstance(model.getHeaderId());
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.container, bottomFragment, bottomFragment.getClass().getName())
                .addToBackStack(bottomFragment.getClass().getName())
                .commit();
    }
}
