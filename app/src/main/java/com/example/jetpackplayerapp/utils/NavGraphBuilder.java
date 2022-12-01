package com.example.jetpackplayerapp.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.example.jetpackplayerapp.model.Destination;
import com.example.jetpackplayerapp.navigator.FixFragmentNavigator;
import com.example.libcommon.AppGlobals;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 导航器定制
 */
public class NavGraphBuilder {

    public static void build(FragmentActivity activity,
                             FragmentManager childFragmentManager,
                             NavController controller,
                             int containerId
                             ) {
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        FixFragmentNavigator fixFragmentNavigator = new FixFragmentNavigator(activity, childFragmentManager, containerId);
        provider.addNavigator(fixFragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        Iterator<Destination> iterator = destConfig.values().iterator();
        while (iterator.hasNext()) {
            Destination node = iterator.next();
            if (node.isIsFragment()) {
                FragmentNavigator.Destination destination = fixFragmentNavigator.createDestination();
                destination.setId(node.getId());
                destination.setClassName(node.getClassName());
                destination.addDeepLink(node.getPageUrl());
                navGraph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(node.getId());
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(), node.getClassName()));
                destination.addDeepLink(node.getPageUrl());
                navGraph.addDestination(destination);
            }

            /**
             * 设置默认展示页面
             */
            if (node.isAsStarter()) {
                navGraph.setStartDestination(node.getId());
            }
        }

        controller.setGraph(navGraph);
    }
}
