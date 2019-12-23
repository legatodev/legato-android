package com.legato.music.messaging;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.legato.music.R;

import java.util.List;

import androidx.viewpager.widget.ViewPager;
import co.chatsdk.core.Tab;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.main.BaseFragment;
import co.chatsdk.ui.main.PagerAdapterTabs;

public class ActiveChatActivity extends AppCompatActivity {
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    protected PagerAdapterTabs adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_chats);

        viewPager = findViewById(co.chatsdk.ui.R.id.pager);

        tabLayout = findViewById(co.chatsdk.ui.R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        // Only creates the adapter if it wasn't initiated already
        if (adapter == null) {
            adapter = new PagerAdapterTabs(getSupportFragmentManager());
        }

        final List<Tab> tabs = adapter.getTabs();
        for (Tab tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab.title));
        }

        ((BaseFragment) tabs.get(0).fragment).setTabVisibility(true);

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                for(int i = 0; i < tabs.size(); i++) {
                    ((BaseFragment) tabs.get(i).fragment).setTabVisibility(i == tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
