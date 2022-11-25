package com.example.jetpackplayerapp.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.jetpackplayerapp.model.BottomBar;
import com.example.jetpackplayerapp.model.Destination;
import com.example.libcommon.global.AppGlobals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {
    private static HashMap<String, Destination> sDestConfig;
    private static BottomBar sBottomBar;

    /**
     * 获取路由配置
     * @return
     */
    public static HashMap<String, Destination> getDestConfig() {
        if (sDestConfig == null) {
            String content = parseFile("destination.json");
            sDestConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>(){});
        }
        return sDestConfig;
    }

    /**
     * 读取底部Tab配置
     * @return
     */
    public static BottomBar getsBottomBarConfig() {
        if (sBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return sBottomBar;
    }

    /**
     * 读文件
     * @param fileName
     * @return
     */
    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getAssets();

        InputStream is = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();

        try {
            is = assets.open(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {

            }
        }
        return builder.toString();
    }
}
