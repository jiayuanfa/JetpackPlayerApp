package com.example.jetpackplayerapp.ui.publish;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jetpackplayerapp.databinding.ActivityPublishBinding;
import com.example.libnavannotation.ActivityDestination;

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity {

    private ActivityPublishBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPublishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}
