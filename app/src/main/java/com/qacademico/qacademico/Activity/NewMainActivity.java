package com.qacademico.qacademico.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.qacademico.qacademico.R;

public class NewMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawer;
    BottomNavigationView navigation;
    NavigationView navigationView;
    ViewGroup mainLayout;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainLayout = (ViewGroup) findViewById(R.id.main_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        setSupportActionBar(toolbar);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_materiais) {

        } else if (id == R.id.nav_calendario) {

        } else if (id == R.id.nav_documentos) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sug) {

        } else if (id == R.id.nav_bug) {

        } else if (id == R.id.nav_logout) {

        } else if (id == R.id.nav_password) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;

                case R.id.navigation_diarios:

                    return true;

                case R.id.navigation_boletim:

                    return true;

                case R.id.navigation_horario:

                    return true;
            }
            return false;
        }
    };
}
