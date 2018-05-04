package retrofitnrxjava.com.example.android.retrofitnrxjava.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import java.util.ArrayList

import retrofitnrxjava.com.example.android.retrofitnrxjava.FullscreenImage
import retrofitnrxjava.com.example.android.retrofitnrxjava.MainActivity
import retrofitnrxjava.com.example.android.retrofitnrxjava.R
import retrofitnrxjava.com.example.android.retrofitnrxjava.contact.Worldpopulation

class WorldPopulationAdapter(private val list: ArrayList<Worldpopulation>) : RecyclerView.Adapter<WorldPopulationAdapter.ViewHolder>() {
    private var context: Context? = null
    internal var url: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val imageUrl = list[position].flag
        val country_name = list[position].country
        val rank = list[position].rank
        val population = list[position].population
        Log.e("Url is: ", imageUrl)
        Glide.with(context).load(imageUrl).into(holder.flagImageView)
        holder.country_name.text = "Name:" + country_name!!
        holder.rank.text = "Rank:" + rank!!.toString()
        holder.population.text = "Population:" + population!!

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

         val flagImageView: ImageView
         val country_name: TextView
        val rank: TextView
        val population: TextView

        init {
            view.setOnClickListener {
                url = list[adapterPosition].flag
                val intent = Intent(context, FullscreenImage::class.java)
                intent.putExtra("url", url)
                context!!.startActivity(intent)
                //Toast.makeText(view.getContext(), "Item is clicked", Toast.LENGTH_SHORT).show();
            }
            flagImageView = view.findViewById<View>(R.id.image) as ImageView
            country_name = view.findViewById(R.id.countryname)
            rank = view.findViewById(R.id.rank)
            population = view.findViewById(R.id.population)
        }
    }

}