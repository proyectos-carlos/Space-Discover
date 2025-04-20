package com.example.discovernasa.fragments.apifragments

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.discovernasa.databinding.ItemBodyBinding
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.discovernasa.R
import com.example.discovernasa.solar_system_api.BodiesDataResponse
import com.example.discovernasa.solar_system_api.BodyType

class BodyAdapter(var bodiesList : List<BodiesDataResponse>) : RecyclerView.Adapter<BodyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BodyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_body, parent, false)
        return BodyViewHolder(view)

    }

    override fun getItemCount() = bodiesList.size

    override fun onBindViewHolder(holder: BodyViewHolder, position: Int) {
        val currentBody = bodiesList[position]
        holder.render(currentBody)
    }

    fun updateList(list : List<BodiesDataResponse>){
        bodiesList = list
        notifyDataSetChanged()
    }

}

class BodyViewHolder(view : View) : RecyclerView.ViewHolder(view){
    private val binding = ItemBodyBinding.bind(view)
    private val context = binding.root.context

    fun render(body : BodiesDataResponse){
        binding.tvName.text = body.englishName
        binding.tvType.text = body.bodyType.ifBlank { "Unknown" }
        binding.tvDiscoveryDate.text =  "Discovered at: ${body.discoveryDate.ifBlank { "Unknown" }}"

        when(body.bodyTypeEnum){
            BodyType.PLANET -> addDrawable(R.drawable.ic_planet)
            BodyType.MOON -> addDrawable(R.drawable.ic_moon)
            BodyType.ASTEROID -> addDrawable(R.drawable.ic_asteroid)
            BodyType.COMET -> addDrawable(R.drawable.ic_comet)
            BodyType.STAR -> addDrawable(R.drawable.ic_sun)
            BodyType.DWARF_PLANET -> addDrawable(R.drawable.ic_dwarf_planet)
            BodyType.UNKNOWN -> addDrawable(R.drawable.ic_question_mark)
        }
    }

    private fun addDrawable(imageResource : Int) = binding.imageCelestialBody.setImageResource(imageResource)


//        body.imageURL.let {
//            Glide.with(context).load(body.imageURL)
//                .circleCrop()
//                .into(binding.imageCelestialBody)
//        } ?: run {
//            //Not implemented
//        }
}