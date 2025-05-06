package com.example.solarsystemapp.fragments.calendarfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.example.discovernasa.R
import com.example.discovernasa.databinding.FragmentCalendarBinding
import com.example.solarsystemapp.nasa_api.ApodDataResponse
import com.example.solarsystemapp.nasa_api.ApodNetwork
import com.example.solarsystemapp.solar_system_local.DatabaseInit.Companion.localDataDatabase
import com.example.solarsystemapp.solar_system_local.toLocal
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CalendarFragment : Fragment() {


    private lateinit var mBinding : FragmentCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCalendarBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val apod = ApodNetwork.getApod(date = null)
            requireActivity().runOnUiThread {
                apod?.let {
                    renderUI(it)
                    setupListeners(it)
                    mBinding.progressBar.visibility = View.GONE
                } ?: run{ Snackbar.make(mBinding.root, getString(R.string.error_load_apod), Snackbar.LENGTH_SHORT).show() }
            }
        }

    }

    private fun setupListeners(apod: ApodDataResponse) {
        mBinding.buttonFavorite.setOnClickListener { saveApodToLocal(apod) }
        mBinding.buttonGoToFavorite.setOnClickListener { navigateToFavorites() }
    }



    private fun saveApodToLocal(apod: ApodDataResponse) {
       CoroutineScope(Dispatchers.IO).launch {
           localDataDatabase.getApodDao().insertApod(apod.toLocal())

           requireActivity().runOnUiThread {
               Snackbar.make(mBinding.root, getString(R.string.apod_saved), Snackbar.LENGTH_SHORT)
                   .setAction("OK") {}
                   .show()
           }
       }
    }

    private fun navigateToFavorites() {
        findNavController().navigate(R.id.action_calendarFragment_to_savedApodFragment)
    }





    private fun renderUI(apodResponse : ApodDataResponse){

        with(mBinding){
            if(apodResponse.media_type == "image"){
                Glide.with(requireContext())
                    .load(apodResponse.hdurl ?: apodResponse.url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_static)
                    .error(R.drawable.ic_skull)
                    .into(mBinding.apodImageView)
            }

            tvApodTitle.text = apodResponse.title
            tvApodDescription.text = apodResponse.explanation.ifBlank { getString(R.string.no_apod_explanation) }
            tvApodDate.text = apodResponse.date.ifBlank { getString(R.string.no_apod_date) }
            tvApodCopyright.text = apodResponse.copyright ?: getString(R.string.no_copyright)
        }
    }
}