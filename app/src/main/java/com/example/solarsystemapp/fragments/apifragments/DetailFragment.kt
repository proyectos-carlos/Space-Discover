package com.example.solarsystemapp.fragments.apifragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.solarsystemapp.R
import com.example.solarsystemapp.databinding.FragmentDetailBinding
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
    private val moonsList = mutableListOf<BodiesDataResponse>()
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
        viewLifecycleOwner.lifecycleScope.launch {
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
                body?.let {
                    renderMainUI(body)
                    setupListeners(body)
                    mBinding.progressBar.visibility = View.GONE
                } ?: run {
                    Snackbar.make(
                        mBinding.root,
                        "${getString(R.string.error_loading)} ${args.bodyID}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }


                //After rendering main UI, render body moons if any:
                launch{
                    val moons = when (detailAction) {
                        DetailAction.LOAD_FROM_API -> SolarSystemNetwork.getAllMoons(body?.moons ?: emptyList())
                        DetailAction.LOAD_FROM_DATABASE -> emptyList()
                    }
                        renderMoons(moons)
                        setupMoonListeners(moons)
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
                .placeholder(R.drawable.ic_image_static)
                .error(R.drawable.ic_skull)
                .into(mBinding.bodyImage)

            tvBodyDescription.text = body.description ?: getString(R.string.no_description)

            // Basic data
            tvName.text = body.englishName
            tvBodyType.text = getString(R.string.body_type, body.bodyType.ifBlank { "Unknown" })
            tvDiscoveredBy.text = getString(R.string.discovered_by, body.discoveredBy.ifBlank { "Unknown" })
            tvDiscoveryDate.text = getString(R.string.date, body.discoveryDate.ifBlank { "Unknown"  })


            // Technical Data
            tvAvgTemp.text = formatValueOrHide(body.avgTemp.toDouble(), R.string.avg_temp, tvAvgTemp)
            tvDensity.text = formatValueOrHide(body.density, R.string.density, tvDensity)
            tvGravity.text = formatValueOrHide(body.gravity, R.string.gravity, tvGravity)
            tvEscape.text = formatValueOrHide(body.escape, R.string.escape, tvEscape)
            tvMeanRadius.text = formatValueOrHide(body.meanRadius, R.string.mean_radius, tvMeanRadius)
            tvSemimajorAxis.text = formatValueOrHide(body.semimajorAxis, R.string.semimajor_axis, tvSemimajorAxis)
            tvSideralOrbit.text = formatValueOrHide(body.sideralOrbit, R.string.sideral_orbit, tvSideralOrbit)
            tvSideralRotation.text = formatValueOrHide(body.sideralRotation, R.string.sideral_rotation, tvSideralRotation)
            tvPolarRadius.text = formatValueOrHide(body.polarRadius, R.string.polar_radius, tvPolarRadius)
            tvAphelion.text = formatValueOrHide(body.aphelion, R.string.aphelion, tvAphelion)

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
        mBinding.tvFilteredMoons.visibility = View.VISIBLE
        mBinding.tvFilteredMoons.text = getString(R.string.filtered_moons, moons.size)


        moonsAdapter = BodyAdapter(bodiesList =  moons, onItemSelected = { id -> navigateToDetail(id) } )

        mBinding.recyclerViewMoons.let {
            it.adapter = moonsAdapter
            it.layoutManager = LinearLayoutManager(context)
        }
        setupMoonListeners()
    }

    private fun setupMoonListeners(moons : List<BodiesDataResponse> = emptyList()) {
        mBinding.searchViewMoons.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                val bodyQuery = query.orEmpty().trim()
                Snackbar.make(mBinding.root, "Searching $bodyQuery", Snackbar.LENGTH_SHORT).show()
                filterAndUpdateMoons(bodyQuery, moons)
                mBinding.searchViewMoons.clearFocus()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val bodyQuery = newText.orEmpty().trim()
                filterAndUpdateMoons(bodyQuery, moons)
                return false
            }
        })

        mBinding.searchViewMoons.setOnClickListener { mBinding.searchViewMoons.onActionViewExpanded() }

    }

    private fun filterAndUpdateMoons(query : String, newMoons : List<BodiesDataResponse>) {
        val filteredMoons = newMoons.filter { moon -> moon.englishName.contains(query, ignoreCase = true) }
        moonsAdapter.updateList(filteredMoons)
        mBinding.tvFilteredMoons.text = getString(R.string.filtered_moons, filteredMoons.size)
    }


    private fun navigateToDetail(id : String){
        findNavController().navigate(DetailFragmentDirections.actionBodyDetailFragmentToSelf
            (bodyID = id, detailAction = DetailAction.LOAD_FROM_API))
    }

    private fun setupListeners(body : DetailBodiesDataResponse, moons : List<BodiesDataResponse> = emptyList()) {
         mBinding.floatingActionButtonSaveBody.setOnClickListener {
             saveBodyToLocalDatabase(body)

         }

        mBinding.floatingActionButtonDeleteBody.setOnClickListener {
            deleteBodyFromLocalDatabase(body)
        }

    }

    private fun deleteBodyFromLocalDatabase(body: DetailBodiesDataResponse) {
        viewLifecycleOwner.lifecycleScope.launch {
            DatabaseInit.localDataDatabase.getBodyDao().deleteBody(body.toLocal())
            Log.i("BigoLocalDatabaseDeleted", DatabaseInit.localDataDatabase.getBodyDao().getAllBodies().toString())

            Snackbar.make(mBinding.root, "Deleted ${body.englishName}", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(DetailFragmentDirections.actionBodyDetailFragmentToSavesFragment())


        }
    }

    private fun saveBodyToLocalDatabase(body : DetailBodiesDataResponse) {
        viewLifecycleOwner.lifecycleScope.launch {
            DatabaseInit.localDataDatabase.getBodyDao().insertBody(body.toLocal())
            Log.i("BigoLocalDatabaseAdded", DatabaseInit.localDataDatabase.getBodyDao().getAllBodies().toString())

                Snackbar.make(mBinding.floatingActionButtonSaveBody, getString(R.string.saved_body, body.englishName),
                    Snackbar.LENGTH_SHORT)
                    .setAction("OK"){}//Close snackbar
                    .show()

        }

    }

    private fun formatValueOrHide(value: Double, resId: Int, textView : TextView) : String{
        if (value == 0.0) {
            textView.visibility = View.GONE
            return ""
        } else {
            return getString(resId, value)
        }
    }
}