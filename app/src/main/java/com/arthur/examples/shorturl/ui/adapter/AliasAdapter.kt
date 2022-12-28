package com.arthur.examples.shorturl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import com.arthur.examples.shorturl.databinding.AliasUrlItemBinding

class AliasAdapter : RecyclerView.Adapter<AliasAdapter.ViewHolder>() {

    private val dataList: MutableList<AliasLocal> = mutableListOf()

    /**
     * Set a list to this Adapter's [dataList]
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    fun setData(aliasList: List<AliasLocal>) {
        dataList.clear()
        dataList.addAll(aliasList)
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    inner class ViewHolder(val binding: AliasUrlItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AliasUrlItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.binding.apply {
            tvSelf.text = item.self
            tvShort.text = item.shorted
        }
    }

    override fun getItemCount(): Int = dataList.size
}