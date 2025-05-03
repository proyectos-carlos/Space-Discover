package com.example.solarsystemapp.fragments.savesfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.discovernasa.databinding.FragmentSavesBinding

class SavesFragment : Fragment() {

    private lateinit var mBinding : FragmentSavesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSavesBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }


    private fun setupListeners() {
        mBinding.cardPlanet.setOnClickListener { navigateToCategory(CategoryAction.LOAD_PLANETS) }
        mBinding.cardMoon.setOnClickListener { navigateToCategory(CategoryAction.LOAD_MOONS) }
        mBinding.cardAsteroid.setOnClickListener { navigateToCategory(CategoryAction.LOAD_ASTEROIDS) }
        mBinding.cardComet.setOnClickListener { navigateToCategory(CategoryAction.LOAD_COMETS) }
        mBinding.cardStar.setOnClickListener { navigateToCategory(CategoryAction.LOAD_STARS) }
        mBinding.cardDwarfPlanet.setOnClickListener { navigateToCategory(CategoryAction.LOAD_DWARF_PLANETS) }
    }


    private fun navigateToCategory(category : CategoryAction){
        findNavController().navigate(SavesFragmentDirections.actionSavesFragmentToSavesCategoryFragment(
            loadCategoryAction = category
        ))
    }

}