package com.example.discovernasa.fragments.apifragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.discovernasa.Tools
import com.example.discovernasa.api.ApiService
import com.example.discovernasa.api.BodiesDataResponse
import com.example.discovernasa.api.BodyType
import com.example.discovernasa.databinding.FragmentApiBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiFragment : Fragment() {


    private lateinit var mBinding: FragmentApiBinding
    private lateinit var retrofit: Retrofit
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
        retrofit = getRetrofit()
        renderUI()
        setupListeners()

        CoroutineScope(Dispatchers.IO).launch {
            Log.i("BigoReport", "All bodies be: ${getAllBodies()}")
        }

    }

    private fun renderUI() {
        CoroutineScope(Dispatchers.IO).launch {
            val bodies = getAllBodies()
            adapter = BodyAdapter(bodies ?: emptyList())

                activity?.runOnUiThread{
                   mBinding.recyclerViewBody.adapter = adapter
                   mBinding.recyclerViewBody.layoutManager = LinearLayoutManager(context)
                }
        }


    }

    private fun setupListeners() {

        mBinding.searchViewBody.setOnSearchClickListener {
            mBinding.searchViewBody.queryHint = "Buscar planeta"
        }


        mBinding.searchViewBody.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                val bodyQuery = query.orEmpty().trim()
                if(bodyQuery.isEmpty()){
                    Snackbar.make(mBinding.root, "Por favor ingrese un nombre a buscar", Snackbar.LENGTH_SHORT).show()
                }else{
                    Snackbar.make(mBinding.root, "Buscando $bodyQuery", Snackbar.LENGTH_SHORT).show()
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
                   else -> selectedTypes.add(BodyType.UNKNOWN)
           }
       }
           updateList()
           Log.i("BigoReport", "Selected types: $selectedTypes")

       }


    }

    //Update list whether chips are selected or query is sent
    private fun updateList() {
        val query = mBinding.searchViewBody.query.toString()

        CoroutineScope(Dispatchers.IO).launch {
            //If query is empty, show all bodies
            val bodies = if(query.isBlank()) getAllBodies() else searchBodyById(query)

            activity?.runOnUiThread {


                bodies?.let { it ->
                    //If no bodies were found (most likely because the ID in search view is invalid), show a snackbar)
                    if(it.isEmpty()){
                        Snackbar.make(mBinding.root, "No se encontraron resultados de $query", Snackbar.LENGTH_SHORT).show()
                        return@let
                    }

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
                        Snackbar.make(mBinding.root, "No se encontraron resultados en los filtros especificados", Snackbar.LENGTH_SHORT).show()
                        return@let
                    }

                    Log.i("BigoReport", "Current list filtered $filteredBodies")
                    adapter.updateList(filteredBodies)

                } ?: run {
                    Snackbar.make(mBinding.root, "Error en la consulta", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun getRetrofit() : Retrofit {
        val retrofitConfig = Retrofit.Builder()
            .baseUrl(Tools.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofitConfig
    }

    private suspend fun searchBodyById(query : String) : List<BodiesDataResponse>?{
        return withContext(Dispatchers.IO){

            val myResponse = retrofit.create(ApiService::class.java).getBodiesById(query)

            if(!myResponse.isSuccessful || myResponse.body() == null){
                Log.i("BigoReport", "La respuesta no funciona ${myResponse.body()}")
                return@withContext null
            }

            val bodiesResult = myResponse.body()!!

            Log.i("BigoReport", "El resultado es $bodiesResult")
            if (bodiesResult.englishName.isBlank() && bodiesResult.bodyType.isBlank()) {
                Log.i("BigoReport", "El cuerpo no se encontró, probablemente el ID es inválido")
                return@withContext null
            }

            return@withContext listOf(bodiesResult)
        }
    }

    private suspend fun getAllBodies() : List<BodiesDataResponse>?{
          return withContext(Dispatchers.IO){
              val myResponse = retrofit.create(ApiService::class.java).getAllBodies()
              if(!myResponse.isSuccessful || myResponse.body() == null){
                  Log.i("BigoReport", "La respuesta no funciona ${myResponse.body()}")
                  return@withContext null
              }
              return@withContext myResponse.body()!!.bodies
          }
    }



}