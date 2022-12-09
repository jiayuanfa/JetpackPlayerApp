package com.mooc.fageplayer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.mooc.fageplayer.databinding.ActivityMainBinding;
import com.mooc.fageplayer.model.Destination;
import com.mooc.fageplayer.model.User;
import com.mooc.fageplayer.ui.login.UserManager;
import com.mooc.fageplayer.utils.AppConfig;
import com.mooc.fageplayer.utils.NavGraphBuilder;
import com.mooc.libcommon.utils.StatusBar;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 由于启动的时候给Activity设置了主题，这里要更改回来，消掉背景
        setTheme(R.style.AppTheme);

        // 启动沉浸式布局，白底黑字
        StatusBar.fitSystemBar(this);

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

    private void getUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiService.init("https://www.baidu.com", null);
                ApiService.post("/s?wd=2022卡塔尔世界杯&rsv_dl=Worldcup_PC_2022_index_tips")
                        .execute(new JsonCallback() {
                            @Override
                            public void onSuccess(ApiResponse response) {
                                super.onSuccess(response);
                                System.out.println("ApiResponse:" + response);
                            }

                            @Override
                            public void onError(ApiResponse response) {
                                super.onError(response);
                                System.out.println("ApiResponse:" + response);
                            }

                            @Override
                            public void onCacheSuccess(ApiResponse response) {
                                super.onCacheSuccess(response);
                            }
                        });
            }
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        Iterator<Map.Entry<String, Destination>> iterator = destConfig.entrySet().iterator();

        // 遍历 target destination 看是否需要登录拦截
        while (iterator.hasNext()) {
            Map.Entry<String, Destination> entry = iterator.next();
            Destination value = entry.getValue();
            if (value != null && !UserManager.get().isLogin() && value.isNeedLogin() && value.getId() == menuItem.getItemId()) {
                UserManager.get().login(this).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            binding.navView.setSelectedItemId(menuItem.getItemId());
                        }
                    }
                });
                return false;
            }
        }

        navController.navigate(menuItem.getItemId());

        /**
         * 如果标题为空，则不处理
         */
        return !TextUtils.isEmpty(menuItem.getTitle());
    }
}