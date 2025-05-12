package com.example.solarsystemapp.fragments.settingsfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.solarsystemapp.MainActivity
import com.example.solarsystemapp.Tools
import com.example.solarsystemapp.databinding.FragmentSettingsBinding
import com.example.solarsystemapp.localdata.LocalDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private lateinit var mBinding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderUI()
        setupListeners()
    }

    private fun renderUI() {
        CoroutineScope(Dispatchers.IO).launch {
            val darkMode = LocalDatastore.readBoolean(requireContext(), Tools.DARK_MODE_KEY)

            requireActivity().runOnUiThread {
                mBinding.switchDarkMode.isChecked = darkMode
            }
        }
    }

    private fun setupListeners() {
        mBinding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                LocalDatastore.writeBoolean(requireContext(), Tools.DARK_MODE_KEY, isChecked)
                requireActivity().runOnUiThread { (activity as? MainActivity)?.toggleNightMode(isChecked) }
            }
        }
    }


}