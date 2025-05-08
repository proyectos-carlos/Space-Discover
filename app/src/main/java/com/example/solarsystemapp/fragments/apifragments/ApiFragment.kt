package com.example.solarsystemapp.fragments.apifragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.discovernasa.R
import com.example.discovernasa.databinding.FragmentApiBinding
import com.example.solarsystemapp.solar_system_api.BodiesDataResponse
import com.example.solarsystemapp.solar_system_api.BodyType
import com.example.solarsystemapp.solar_system_api.SolarSystemNetwork.getAllBodies
import com.example.solarsystemapp.solar_system_api.SolarSystemNetwork.searchBodiesByName
import com.example.solarsystemapp.solar_system_api.SolarSystemNetwork.searchBodyById
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApiFragment : Fragment() {


    private lateinit var mBinding: FragmentApiBinding
    private lateinit var adapter: BodyAdapter
    private var selectedTypes = mutableListOf<BodyType>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentApiBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderUI()
        setupListeners()
    }



    private fun renderUI() {
        CoroutineScope(Dispatchers.IO).launch {
            // Get earth as default for the first time
            val initBodies = searchBodyById("earth")?.let { listOf(it) } ?: run { emptyList() }

            requireActivity().runOnUiThread {
                adapter = BodyAdapter(bodiesList =  initBodies,
                    onItemSelected = { id -> navigateToDetail(id) } )
                    mBinding.recyclerViewBody.adapter = adapter
                    mBinding.recyclerViewBody.layoutManager = LinearLayoutManager(context)
                    mBinding.recyclerViewBody.itemAnimator = DefaultItemAnimator().apply {
                        addDuration = 300
                        removeDuration = 200
                    }
                mBinding.progressBar.visibility = View.GONE
            }
        }


    }

    private fun navigateToDetail(id : String){
        findNavController().navigate(ApiFragmentDirections.actionApiFragmentToBodyDetailFragment
            (bodyID = id, detailAction = DetailAction.LOAD_FROM_API))
    }

    private fun setupListeners() {

        mBinding.searchViewBody.setOnSearchClickListener {
            mBinding.searchViewBody.queryHint = getString(R.string.search_body)
        }


        mBinding.searchViewBody.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                val bodyQuery = query.orEmpty().trim()
                if(bodyQuery.isEmpty()){
                    Snackbar.make(mBinding.root, getString(R.string.empty_search_view), Snackbar.LENGTH_SHORT).show()
                }else{
                    Snackbar.make(mBinding.root, "${getString(R.string.searching_body)} $bodyQuery", Snackbar.LENGTH_SHORT).show()
                    updateList()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean { return false }
        })



       mBinding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
           selectedTypes.clear()

           for(id in checkedIds){
               when(group.findViewById<Chip>(id)){
                   mBinding.chipPlanet -> selectedTypes.add(BodyType.PLANET)
                   mBinding.chipMoon -> selectedTypes.add(BodyType.MOON)
                   mBinding.chipStar -> selectedTypes.add(BodyType.STAR)
                   mBinding.chipAsteroid -> selectedTypes.add(BodyType.ASTEROID)
                   mBinding.chipComet -> selectedTypes.add(BodyType.COMET)
                   mBinding.chipDwarfPlanet -> selectedTypes.add(BodyType.DWARF_PLANET)
                   else -> selectedTypes.add(BodyType.UNKNOWN)
           }
       }
           updateList()
           Log.i("BigoReport", "Selected types: $selectedTypes")

       }

        mBinding.searchViewBody.setOnClickListener { mBinding.searchViewBody.onActionViewExpanded() }


    }

    //Update list whether chips are selected or query is sent
    private fun updateList() {

        val query = mBinding.searchViewBody.query.toString()
        mBinding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {

            //Get bodies depending if query is blank or not. Get empty list if no bodies were found
            val bodies: List<BodiesDataResponse> = if (query.isBlank()) {
                getAllBodies() ?: emptyList()
            } else {
                searchBodiesByName(query)
            }


            activity?.runOnUiThread {

                //If no bodies were found (most likely because the ID in search view is invalid), show a snackbar
                if(bodies.isEmpty()){
                    Snackbar.make(mBinding.root, "${getString(R.string.no_results)} $query", Snackbar.LENGTH_SHORT).show()
                    mBinding.progressBar.visibility = View.GONE
                    return@runOnUiThread
                }

                bodies.let { it ->

                    Log.i("BigoReport", "Current list $it")
                    // If no types are selected, not filter any bodies at all
                    if (selectedTypes.isEmpty()) {
                        adapter.updateList(it)
                        return@let
                    }

                    //Filter bodies by selected types
                    val filteredBodies = it.filter { selectedTypes.contains(it.bodyTypeEnum) }

                    //If no bodies were found after filtering, show a snackbar
                    if(filteredBodies.isEmpty()){
                        Snackbar.make(mBinding.root, getString(R.string.no_results_in_filters), Snackbar.LENGTH_SHORT).show()
                        return@let
                    }

                    Log.i("BigoReport", "Current list filtered $filteredBodies")
                    adapter.updateList(filteredBodies)

                }
                mBinding.progressBar.visibility = View.GONE
            }
        }

    }
}