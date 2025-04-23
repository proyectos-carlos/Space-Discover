package com.example.discovernasa.fragments.apifragments

import android.os.Bundle
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
import com.example.discovernasa.solar_system_api.BodiesDataResponse
import com.example.discovernasa.solar_system_api.DetailBodiesDataResponse
import com.example.discovernasa.solar_system_api.SolarSystemNetwork
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Body


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
        when(detailAction){
            DetailAction.LOAD_FROM_API -> loadFromApi(args.bodyID)
            DetailAction.LOAD_FROM_DATABASE -> loadFromDatabase(args.bodyID)
        }

        mBinding.progressBar.visibility = View.GONE
    }

    private fun loadFromApi(bodyID: String) {

        CoroutineScope(Dispatchers.IO).launch {

            val body = SolarSystemNetwork.getDetailBodyById(bodyID)
            val bodyMoons = SolarSystemNetwork.getAllMoons(body?.moons ?: emptyList())

                requireActivity().runOnUiThread {
                    body?.let { body ->
                        renderUI(body, bodyMoons)
                    }
                        ?: run { Snackbar.make(mBinding.root, "Error al cargar $bodyID", Snackbar.LENGTH_SHORT).show() }
                }

            }

        }

    private fun loadFromDatabase(bodyID : String) {

    }



    private fun renderUI(body: DetailBodiesDataResponse, moons : List<BodiesDataResponse>) {
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

            // Lunas (si hay)
            tvMoons.text = if (!body.moons.isNullOrEmpty()) {
                "Lunas: " + body.moons.joinToString(", ") { it.moon }
            } else {
                "Lunas: Ninguna"
            }
            moonsAdapter = BodyAdapter(bodiesList =  moons, onItemSelected = { id -> navigateToDetail(id) } )

            mBinding.recyclerViewMoons.let {
                it.adapter = moonsAdapter
                it.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun navigateToDetail(id : String){
//        findNavController().navigate(ApiFragmentDirections.actionApiFragmentToBodyDetailFragment
//            (bodyID = id, detailAction = DetailAction.LOAD_FROM_API))
    }




}