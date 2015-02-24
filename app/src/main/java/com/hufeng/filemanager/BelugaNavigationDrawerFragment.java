package com.hufeng.filemanager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Feng Hu on 15-01-31.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaNavigationDrawerFragment extends Fragment{

    RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Handler mHandler;

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDrawerItems = new DrawerItem[] {
                new DrawerItem(R.id.drawer_item_my_files, null, getString(R.string.drawer_my_files)),
                new DrawerItem(R.id.drawer_item_my_apps, null, getString(R.string.drawer_my_apps)),
                new DrawerItem(R.id.drawer_item_settings, null, getString(R.string.drawer_settings)),
                new DrawerItem(R.id.drawer_item_about, null, getString(R.string.drawer_about)),
                new DrawerItem(R.id.drawer_item_help_and_feedback, null, getString(R.string.drawer_help_and_feedback))
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drawer, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(android.R.id.list);
        mRecyclerView.setClipToPadding(false);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new NavigationDrawerAdapter());

        return v;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackground(R.color.primary_color_dark);
    }


    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    private DrawerItem[] mDrawerItems;

    private class DrawerItem {
        private String name;
        private Drawable icon;
        private int id;

        DrawerItem(int id, Drawable icon, String name) {
            this.id = id;
            this.icon = icon;
            this.name = name;
        }

        public int getId() {
            return id;
        }
    }

    public class NavigationDrawerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.name)
        TextView name;

        private DrawerItem item;

        public NavigationDrawerItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindItem(DrawerItem item) {
            this.item = item;
            if (item.icon != null) {
                icon.setImageDrawable(item.icon);
                icon.setVisibility(View.VISIBLE);
            } else {
                icon.setVisibility(View.GONE);
            }
            name.setText(item.name);
            this.itemView.setTag(item);
        }

        @Override
        public void onClick(View v) {
            goToNavigationDrawerItem(((DrawerItem) v.getTag()).getId());
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void goToNavigationDrawerItem(final int itemId) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (itemId) {
                    case R.id.drawer_item_my_files:
                        if (!(getActivity() instanceof BelugaDrawerActivity)) {
                            startActivity(new Intent(getActivity(), BelugaDrawerActivity.class));
                            getActivity().overridePendingTransition(0,0);
                            getActivity().finish();
                        }
                        break;
                    case R.id.drawer_item_my_apps:
                        if (!(getActivity() instanceof AppManagerActivity)) {
                            startActivity(new Intent(getActivity(), AppManagerActivity.class));
                            getActivity().overridePendingTransition(0,0);
                            getActivity().finish();
                        }
                        break;
                    case R.id.drawer_item_settings:
                        startActivity(new Intent(getActivity(), BelugaSettingActivity.class));
                        break;
                    case R.id.drawer_item_about:
                        startActivity(new Intent(getActivity(), BelugaAboutActivity.class));
                        break;
                    case R.id.drawer_item_help_and_feedback:
                        startActivity(new Intent(getActivity(), BelugaHelpActivity.class));
                        break;
                }
            }
        }, NAVDRAWER_LAUNCH_DELAY);

    }

    private class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerItemViewHolder>{

        @Override
        public NavigationDrawerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NavigationDrawerItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.navigation_drawer_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(NavigationDrawerItemViewHolder holder, int position) {
            holder.bindItem(mDrawerItems[position]);
        }

        @Override
        public int getItemCount() {
            return mDrawerItems.length;
        }
    }
}
