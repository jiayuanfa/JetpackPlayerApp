package com.mooc.ppjoke.ui.sofa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mooc.ppjoke.databinding.FragmentSofaBinding;
import com.mooc.libnavannotation.FragmentDestination;

@FragmentDestination(pageUrl = "main/tabs/sofa", asStarter = false)
public class SofaFragment extends Fragment {

    private FragmentSofaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSofaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
