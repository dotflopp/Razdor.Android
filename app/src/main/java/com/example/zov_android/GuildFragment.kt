package com.example.zov_android

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zov_android.databinding.FragmentGuildBinding
import com.example.zov_android.ui.fragments.navigation.NavigableFragment
import com.example.zov_android.ui.fragments.navigation.NavigationFragment
import com.example.zov_android.ui.fragments.navigation.NavigationInsideFragment


class GuildFragment(
    id: String,
    name: String,
) : NavigableFragment() {
    private var _binding: FragmentGuildBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(context: Context): View {
        _binding = FragmentGuildBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}