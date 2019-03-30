package com.nesstech.metube.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.nesstech.metube.R;
import com.nesstech.metube.fragment.VListPanel;

public class VListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        String cID = getIntent().getStringExtra("cID");

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(fragment==null){
            fragment = VListPanel.newInstance(cID);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                    .replace(R.id.frame_container,fragment,"")
                    .commit();
        }else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .attach(fragment)
                    .commit();
        }
    }

}
