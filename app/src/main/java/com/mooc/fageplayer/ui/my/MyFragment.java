package com.mooc.fageplayer.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mooc.fageplayer.databinding.FragmentMyBinding;
import com.mooc.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/my", needLogin = true)
public class MyFragment extends Fragment {

    private FragmentMyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
