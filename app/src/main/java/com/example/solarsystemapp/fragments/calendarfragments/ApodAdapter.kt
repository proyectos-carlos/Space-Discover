package com.example.solarsystemapp.fragments.calendarfragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.discovernasa.R
import com.example.discovernasa.databinding.ItemApodBinding
import com.example.solarsystemapp.solar_system_local.DataApodLocal

class ApodAdapter(val apodList : List<DataApodLocal>) :  RecyclerView.Adapter<ApodViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_apod, parent, false)
        return ApodViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApodViewHolder, position: Int) {
        val currentApod = apodList[position]
        holder.render(currentApod)
    }

    override fun getItemCount(): Int = apodList.size
}

class ApodViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val binding = ItemApodBinding.bind(view)
    val context = binding.root.context

    fun render(apodResponse : DataApodLocal){
        with(binding){
            if(apodResponse.media_type == "image"){
                Glide.with(context)
                    .load(apodResponse.url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_static)
                    .into(apodImageView)
            }else{
                apodImageView.setImageResource(R.drawable.ic_no_image)
            }


            tvApodTitle.text = apodResponse.title
            tvApodDescription.text = apodResponse.explanation.ifBlank { context.getString(R.string.no_apod_explanation) }
            tvApodDate.text = apodResponse.date.ifBlank { context.getString(R.string.no_apod_date) }
            tvApodCopyright.text = apodResponse.copyright ?: context.getString(R.string.nasa_author)
        }
    }
}