package com.example.japaneseflash;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window content transitions before calling super.onCreate()
        getWindow().requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable shared element transitions for this Activity
        getWindow().setSharedElementEnterTransition(
                TransitionInflater.from(this).inflateTransition(android.R.transition.move));
        getWindow().setSharedElementExitTransition(
                TransitionInflater.from(this).inflateTransition(android.R.transition.move));

        // Set up the toolbar and set it as the support ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle (hamburger icon)
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,   // defined in strings.xml
                R.string.navigation_drawer_close);   // defined in strings.xml
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Set up the auth state listener to update the UI when login status changes
        authStateListener = firebaseAuth1 -> {
            FirebaseUser currentUser = firebaseAuth1.getCurrentUser();
            if (currentUser == null) {
                // No user logged in: hide the navigation drawer and load LoginFragment
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                navigationView.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            } else {
                // User is logged in: show the navigation drawer
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                navigationView.setVisibility(View.VISIBLE);
                // If no fragment is currently loaded, load HomeFragment
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation drawer item clicks.
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                break;
            case R.id.nav_kanji_letters:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new KanjiFlashcardFragment())
                        .commit();
                break;
            case R.id.nav_saved_cards:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SavedCardsFragment())
                        .commit();
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Close the drawer if it's open; otherwise, move the app to the background.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }
    @Override
    protected void onDestroy() {
        // Avoid logging out during configuration changes (e.g. rotation)
        if (!isChangingConfigurations()) {
            // Sign out the user when the app is closing
            FirebaseAuth.getInstance().signOut();
            Log.d("MainActivity2", "User logged out because app is closing.");
        }
        super.onDestroy();
    }

}
