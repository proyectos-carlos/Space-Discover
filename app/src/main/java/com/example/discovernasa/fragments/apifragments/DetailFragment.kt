package com.example.discovernasa.fragments.apifragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.discovernasa.R
import com.example.discovernasa.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var mBinding : FragmentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return mBinding.root
    }


}