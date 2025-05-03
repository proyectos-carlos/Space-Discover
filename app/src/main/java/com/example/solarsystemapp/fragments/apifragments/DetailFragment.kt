package com.example.solarsystemapp.fragments.apifragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.discovernasa.R
import com.example.discovernasa.databinding.FragmentDetailBinding
import com.example.solarsystemapp.solar_system_api.BodiesDataResponse
import com.example.solarsystemapp.solar_system_api.DetailBodiesDataResponse
import com.example.solarsystemapp.solar_system_api.SolarSystemNetwork
import com.example.solarsystemapp.solar_system_local.DatabaseInit
import com.example.solarsystemapp.solar_system_local.toApi
import com.example.solarsystemapp.solar_system_local.toLocal
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class DetailAction{
    LOAD_FROM_API,
    LOAD_FROM_DATABASE
}

class DetailFragment : Fragment() {

    private lateinit var mBinding : FragmentDetailBinding
    private val args : DetailFragmentArgs by navArgs()
    private lateinit var moonsAdapter : BodyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.progressBar.visibility = View.VISIBLE
        val detailAction = args.detailAction

        // Render main UI
        CoroutineScope(Dispatchers.IO).launch {
            val body = when (detailAction) {

                DetailAction.LOAD_FROM_API -> {
                    mBinding.floatingActionButtonSaveBody.visibility = View.VISIBLE
                    loadFromApi(args.bodyID)
                }

                DetailAction.LOAD_FROM_DATABASE -> {
                    mBinding.floatingActionButtonDeleteBody.visibility = View.VISIBLE
                    mBinding.tvMoons.visibility = View.GONE
                    loadFromDatabase(args.bodyID)
                }
            }

            requireActivity().runOnUiThread {
                body?.let {
                    renderMainUI(body)
                    setupListeners(body)
                    mBinding.progressBar.visibility = View.GONE
                } ?: run{
                    Snackbar.make(mBinding.root, "Error al cargar ID: ${args.bodyID}", Snackbar.LENGTH_SHORT).show()
                }

                //After rendering main UI, render body moons if any:
                CoroutineScope(Dispatchers.IO).launch {
                    val moons = when (detailAction) {
                        DetailAction.LOAD_FROM_API -> SolarSystemNetwork.getAllMoons(body?.moons ?: emptyList())
                        DetailAction.LOAD_FROM_DATABASE -> emptyList()
                    }
                    requireActivity().runOnUiThread { renderMoons(moons) }
                }
            }
        }




    }

    private suspend fun loadFromApi(bodyID : String) : DetailBodiesDataResponse? {
        return SolarSystemNetwork.getDetailBodyById(bodyID)
    }



    private suspend fun loadFromDatabase(bodyID : String) : DetailBodiesDataResponse? {
       return DatabaseInit.localDataDatabase.getBodyDao().getBodyById(bodyID)?.toApi()
    }



    private fun renderMainUI(body: DetailBodiesDataResponse) {
        with(mBinding) {

            Glide.with(requireContext())
                .load(body.imageURL)
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.ic_question_mark)
                .into(mBinding.bodyImage)

            tvBodyDescription.text = body.description ?: getString(R.string.no_description)

            // Basic data
            tvName.text = body.englishName
            tvBodyType.text = "Tipo: ${body.bodyType}"
            tvDiscoveredBy.text = "Descubierto por: ${body.discoveredBy.ifBlank { "Desconocido" }}"
            tvDiscoveryDate.text = "Fecha: ${body.discoveryDate.ifBlank { "Desconocida" }}"


            // Technical Data
            tvAvgTemp.text = "Temperatura promedio: ${body.avgTemp} K"
            tvDensity.text = "Densidad: ${body.density} g/cm³"
            tvGravity.text = "Gravedad: ${body.gravity} m/s²"
            tvEscape.text = "Velocidad escape: ${body.escape} m/s"
            tvMeanRadius.text = "Radio medio: ${body.meanRadius} km"
            tvSemimajorAxis.text = "Eje semi mayor: ${body.semimajorAxis} km"
            tvSideralOrbit.text = "Órbita sideral: ${body.sideralOrbit} días"
            tvSideralRotation.text = "Rotación sideral: ${body.sideralRotation} horas"
            tvPolarRadius.text = "Radio polar: ${body.polarRadius} km"
            tvAphelion.text = "Afelio: ${body.aphelion} km"


        }
    }

    private fun renderMoons(moons : List<BodiesDataResponse>){
        if(moons.isEmpty()) {
            mBinding.tvMoons.text = getString(R.string.no_moons)
            return
        }

        mBinding.tvMoons.text = getString(R.string.total_moons, moons.size)
        mBinding.recyclerViewMoons.visibility = View.VISIBLE
        mBinding.searchViewMoons.visibility = View.VISIBLE

        moonsAdapter = BodyAdapter(bodiesList =  moons, onItemSelected = { id -> navigateToDetail(id) } )

        mBinding.recyclerViewMoons.let {
            it.adapter = moonsAdapter
            it.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun navigateToDetail(id : String){
        findNavController().navigate(DetailFragmentDirections.actionBodyDetailFragmentToSelf
            (bodyID = id, detailAction = DetailAction.LOAD_FROM_API))
    }

    private fun setupListeners(body : DetailBodiesDataResponse, moons : List<BodiesDataResponse>? = null) {
         mBinding.floatingActionButtonSaveBody.setOnClickListener {
             saveBodyToLocalDatabase(body)

         }

        mBinding.floatingActionButtonDeleteBody.setOnClickListener {
            deleteBodyFromLocalDatabase(body)
        }
    }

    private fun deleteBodyFromLocalDatabase(body: DetailBodiesDataResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseInit.localDataDatabase.getBodyDao().deleteBody(body.toLocal())
            Log.i("BigoLocalDatabaseDeleted", DatabaseInit.localDataDatabase.getBodyDao().getAllBodies().toString())

            requireActivity().runOnUiThread {
                Snackbar.make(mBinding.root, "Deleted ${body.englishName}", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(DetailFragmentDirections.actionBodyDetailFragmentToSavesFragment())

            }
        }
    }

    private fun saveBodyToLocalDatabase(body : DetailBodiesDataResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseInit.localDataDatabase.getBodyDao().insertBody(body.toLocal())
            Log.i("BigoLocalDatabaseAdded", DatabaseInit.localDataDatabase.getBodyDao().getAllBodies().toString())

            requireActivity().runOnUiThread {
                Snackbar.make(mBinding.floatingActionButtonSaveBody, getString(R.string.saved_body, body.englishName),
                    Snackbar.LENGTH_SHORT)
                    .setAction("OK"){}//Close snackbar
                    .show()
            }
        }

    }


}