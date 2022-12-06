package com.mooc.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.mooc.ppjoke.databinding.ActivityMainBinding;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.model.User;
import com.mooc.ppjoke.ui.login.UserManager;
import com.mooc.ppjoke.utils.AppConfig;
import com.mooc.ppjoke.utils.NavGraphBuilder;
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