package com.legato.music;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.Tab;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;

public class LegatoInterfaceAdapter extends BaseInterfaceAdapter {


    public LegatoInterfaceAdapter(Context context) {
        super(context);
    }

    @Override
    public List<Tab> defaultTabs() {
        ArrayList<Tab> tabs = new ArrayList<>();
        tabs.add(privateThreadsTab());
        tabs.add(contactsTab());
        return tabs;
    }
}
