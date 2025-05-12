package com.example.solarsystemapp.fragments.calendarfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solarsystemapp.databinding.FragmentSavedApodBinding
import com.example.solarsystemapp.solar_system_local.DatabaseInit.Companion.localDataDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SavedApodFragment : Fragment() {


    private lateinit var mBinding : FragmentSavedApodBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        mBinding = FragmentSavedApodBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            val apodList = localDataDatabase.getApodDao().getAllApod()

                if(apodList.isEmpty()) {
                    mBinding.textViewEmpty.visibility = View.VISIBLE
                    return@launch
                }

                    with(mBinding.recyclerViewApod){
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        adapter = ApodAdapter(apodList)
                        setHasFixedSize(true)
                    }

        }
    }

}