package com.mooc.fageplayer.ui.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mooc.fageplayer.databinding.FragmentFindBinding;
import com.mooc.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/find", asStarter = false)
public class FindFragment extends Fragment {

    private FragmentFindBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
