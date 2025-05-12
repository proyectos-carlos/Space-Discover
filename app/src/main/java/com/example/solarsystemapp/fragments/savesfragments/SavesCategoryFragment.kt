package com.example.solarsystemapp.fragments.savesfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solarsystemapp.R
import com.example.solarsystemapp.databinding.FragmentSavesCategoryBinding
import com.example.solarsystemapp.fragments.apifragments.BodyAdapter
import com.example.solarsystemapp.fragments.apifragments.DetailAction
import com.example.solarsystemapp.solar_system_local.DatabaseInit
import com.example.solarsystemapp.solar_system_local.toBodyDataResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class CategoryAction{
    LOAD_PLANETS,
    LOAD_MOONS,
    LOAD_ASTEROIDS,
    LOAD_COMETS,
    LOAD_STARS,
    LOAD_DWARF_PLANETS
}


class SavesCategoryFragment : Fragment() {
    private val args : SavesCategoryFragmentArgs by navArgs()
    private lateinit var mBinding : FragmentSavesCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        mBinding = FragmentSavesCategoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val action = args.loadCategoryAction
        renderUI(action)
    }

    private fun renderUI(action: CategoryAction) {
         CoroutineScope(Dispatchers.IO).launch {
             val bodies = when(action){
                 CategoryAction.LOAD_PLANETS -> DatabaseInit.localDataDatabase.getBodyDao().getBodiesByType("Planet")

                 CategoryAction.LOAD_MOONS -> DatabaseInit.localDataDatabase.getBodyDao().getBodiesByType("Moon")

                 CategoryAction.LOAD_ASTEROIDS -> DatabaseInit.localDataDatabase.getBodyDao().getBodiesByType("Asteroid")

                 CategoryAction.LOAD_COMETS -> DatabaseInit.localDataDatabase.getBodyDao().getBodiesByType("Comet")

                 CategoryAction.LOAD_STARS -> DatabaseInit.localDataDatabase.getBodyDao().getBodiesByType("Star")

                 CategoryAction.LOAD_DWARF_PLANETS -> DatabaseInit.localDataDatabase.getBodyDao().getBodiesByType("Dwarf Planet")

             }.map { it.toBodyDataResponse() }

             requireActivity().runOnUiThread {
                 renderTopImage(action)
                 mBinding.recyclerViewSaved.adapter = BodyAdapter(bodies,  onItemSelected = { id -> navigateToDetail(id) } )
                 mBinding.recyclerViewSaved.layoutManager = LinearLayoutManager(requireContext())
             }
         }
    }


    private fun navigateToDetail(id : String){
        findNavController().navigate(SavesCategoryFragmentDirections.actionSavesCategoryFragmentToBodyDetailFragment(
            bodyID = id,
            detailAction = DetailAction.LOAD_FROM_DATABASE
        ))
    }

    private fun renderTopImage(action: CategoryAction){
        when(action){
            CategoryAction.LOAD_PLANETS -> mBinding.imageViewCategory.setImageResource(R.drawable.ic_planet)
            CategoryAction.LOAD_MOONS -> mBinding.imageViewCategory.setImageResource(R.drawable.ic_moon)
            CategoryAction.LOAD_ASTEROIDS -> mBinding.imageViewCategory.setImageResource(R.drawable.ic_asteroid)
            CategoryAction.LOAD_COMETS -> mBinding.imageViewCategory.setImageResource(R.drawable.ic_comet)
            CategoryAction.LOAD_STARS -> mBinding.imageViewCategory.setImageResource(R.drawable.ic_sun)
            CategoryAction.LOAD_DWARF_PLANETS -> mBinding.imageViewCategory.setImageResource(R.drawable.ic_dwarf_planet)
        }
    }
}