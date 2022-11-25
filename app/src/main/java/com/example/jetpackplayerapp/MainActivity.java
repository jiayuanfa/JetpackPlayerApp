package com.example.jetpackplayerapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.jetpackplayerapp.databinding.ActivityMainBinding;
import com.example.jetpackplayerapp.utils.NavGraphBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * 通过NavHostFragment拿到NavController
         */
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);

        /**
         * 构建自定义的NavGraph
         * 并设置给NavController
         */
        NavGraphBuilder.build(this, fragment.getChildFragmentManager(), navController, fragment.getId());

        /**
         * 最后给底部的BottomBar设置点击事件
         */
        binding.navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        navController.navigate(menuItem.getItemId());

        /**
         * 如果标题为空，则不处理
         */
        return !TextUtils.isEmpty(menuItem.getTitle());
    }
}