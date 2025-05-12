package com.example.solarsystemapp.fragments.calendarfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.solarsystemapp.R
import com.example.solarsystemapp.databinding.FragmentCalendarBinding
import com.example.solarsystemapp.nasa_api.ApodDataResponse
import com.example.solarsystemapp.nasa_api.ApodNetwork
import com.example.solarsystemapp.solar_system_local.DatabaseInit.Companion.localDataDatabase
import com.example.solarsystemapp.solar_system_local.toLocal
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        setupOfflineListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            val apod = getApodByDate(null) // Null means today's date

                apod?.let {
                    renderUI(it)
                    setupOnlineListeners(it)
                    mBinding.progressBar.visibility = View.GONE
                } ?: run{ Snackbar.make(mBinding.root, getString(R.string.error_load_apod), Snackbar.LENGTH_SHORT).show() }

        }

    }

    private suspend fun setupOnlineListeners(apod: ApodDataResponse) {


           val apodExists = withContext(Dispatchers.IO){
               localDataDatabase.getApodDao().getApodByDate(apod.date)
           }

                apodExists?.let{
                    toggleButton(mBinding.buttonFavorite, toggle = false)
                } ?: run{
                    toggleButton(mBinding.buttonFavorite, toggle = true)
                    mBinding.buttonFavorite.setOnClickListener { saveApodToLocal(apod) }
                }
        mBinding.buttonSelectDate.setOnClickListener { showDatePicker() }

    }

    private fun setupOfflineListeners() {
        mBinding.buttonGoToFavorite.setOnClickListener { navigateToFavorites() }
    }




    private fun showDatePicker() {
        val datePicker = DatePickerFragment{ day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(parentFragmentManager, getString(R.string.date_picker))
    }

    private fun onDateSelected(day: String, month: String, year: String){
        Log.i("BigoCalendar", "$day/$month/$year")
        val dateSelected = "$year-$month-$day"

         viewLifecycleOwner.lifecycleScope.launch {
            val apod = getApodByDate(dateSelected)
            apod?.let { apodResult ->
                    renderUI(apodResult)
                    setupOnlineListeners(apodResult)
            } ?: run{
                Snackbar.make(mBinding.root, getString(R.string.error_load_apod), Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveApodToLocal(apod: ApodDataResponse) {
       viewLifecycleOwner.lifecycleScope.launch {
           localDataDatabase.getApodDao().insertApod(apod.toLocal())
               Snackbar.make(mBinding.root, getString(R.string.apod_saved), Snackbar.LENGTH_SHORT)
                   .setAction("OK") {}
                   .show()

               toggleButton(mBinding.buttonFavorite, toggle = false)

       }
    }

    private fun navigateToFavorites() {
        findNavController().navigate(R.id.action_calendarFragment_to_savedApodFragment)
    }



    private suspend fun getApodByDate(date : String?) = ApodNetwork.getApod(date = date)






    private fun renderUI(apodResponse : ApodDataResponse){

        //Toast.makeText(context, "this is a media type ${apodResponse.media_type}", Toast.LENGTH_SHORT).show()
        with(mBinding){
            if(apodResponse.media_type == "image"){
                Glide.with(requireContext())
                    .load(apodResponse.url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_static)
                    .error(R.drawable.ic_skull)
                    .into(mBinding.apodImageView)
            }else{
                apodImageView.setImageResource(R.drawable.ic_no_image)
            }

            tvApodTitle.text = apodResponse.title
            tvApodDescription.text = apodResponse.explanation.ifBlank { getString(R.string.no_apod_explanation) }
            tvApodDate.text = apodResponse.date.ifBlank { getString(R.string.no_apod_date) }
            tvApodCopyright.text = apodResponse.copyright ?: getString(R.string.nasa_author)
        }
    }

    private fun toggleButton(button: AppCompatButton, toggle : Boolean) {
        button.isEnabled = toggle
        button.alpha = if (toggle) 1f else 0.4f
    }
}