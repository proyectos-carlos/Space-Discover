package com.example.discovernasa.fragments.apifragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.discovernasa.R
import com.example.discovernasa.databinding.FragmentDetailBinding
import com.example.discovernasa.solar_system_api.SolarSystemNetwork
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private lateinit var mBinding : FragmentDetailBinding
    private val args : DetailFragmentArgs by navArgs()
    private var bodyId : String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bodyId = args.bodyId
        renderUI(bodyId)
    }

    private fun renderUI(bodyID: String) {
        Snackbar.make(mBinding.root, "ID: $id", Snackbar.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            val body = SolarSystemNetwork.getDetailBodyById(bodyID)
        }

    }


}