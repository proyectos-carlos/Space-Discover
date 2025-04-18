package com.example.discovernasa.fragments.apifragments

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.discovernasa.databinding.ItemBodyBinding
import android.view.View
import android.view.ViewGroup
import com.example.discovernasa.R
import com.example.discovernasa.api.BodiesDataResponse

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
        binding.tvType.text = body.bodyType
        binding.tvDiscoveryDate.text = body.discoveryDate

    }
}