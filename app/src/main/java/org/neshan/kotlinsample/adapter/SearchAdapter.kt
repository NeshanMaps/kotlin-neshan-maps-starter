package org.neshan.kotlinsample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.servicessdk.search.model.Item

class SearchAdapter(
    private var items: List<Item>,
    private val onSearchItemListener: OnSearchItemListener
) : RecyclerView.Adapter<SearchAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = items[position].title
        holder.tvAddress.text = items[position].address
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateList(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvTitle: TextView = itemView.findViewById(R.id.textView_title)
        val tvAddress: TextView = itemView.findViewById(R.id.textView_address)
        override fun onClick(v: View) {
            val location = items[adapterPosition].location
            val LatLng = LatLng(location.latitude, location.longitude)
            onSearchItemListener.onSearchItemClick(LatLng)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnSearchItemListener {
        fun onSearchItemClick(LatLng: LatLng?)
    }
}