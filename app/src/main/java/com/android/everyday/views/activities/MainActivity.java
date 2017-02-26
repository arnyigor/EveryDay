package com.android.everyday.views.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.android.everyday.R;
import com.android.everyday.views.fragments.TasksFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity {
    private static final int DRAWER_MENU_TASKS = 0;
    private static final int DRAWER_MENU_TASKS_COMPLETE = 1;
    private static final String DRAWER_SELECTION = "drawer_selection";
    private Drawer drawer = null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_tasks));

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(DRAWER_MENU_TASKS).withName(R.string.fragment_tasks).withIcon(GoogleMaterial.Icon.gmd_flight),
                        new PrimaryDrawerItem().withIdentifier(DRAWER_MENU_TASKS_COMPLETE).withName(R.string.fragment_tasks_complete).withIcon(GoogleMaterial.Icon.gmd_equalizer)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        selectItem((int) drawerItem.getIdentifier());
                        return true;
                    }
                })
                .build();
        if (savedInstanceState == null) {
            selectItem(DRAWER_MENU_TASKS);
        }else{
            try{
                drawer.setSelection(Long.parseLong(savedInstanceState.getString(DRAWER_SELECTION)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        outState.putString(DRAWER_SELECTION, String.valueOf(drawer.getCurrentSelection()));
        //add the values which need to be saved from the accountHeader to the bundle
        super.onSaveInstanceState(outState);
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case DRAWER_MENU_TASKS:
                fragment = new TasksFragment();
                toolbar.setTitle(getString(R.string.fragment_tasks));
                break;
            case DRAWER_MENU_TASKS_COMPLETE:
                fragment = new TasksFragment();
                toolbar.setTitle(getString(R.string.fragment_tasks));
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            drawer.closeDrawer();
        }
    }
}
