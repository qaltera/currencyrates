package com.qaltera.currencyrates.androidApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate

class RatesAdapter(var rates: List<CurrencyRate>) : RecyclerView.Adapter<RatesAdapter
.RateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rate, parent, false)
            .run(::RateViewHolder)
    }

    override fun getItemCount(): Int = rates.count()

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bindData(rates[position])
    }

    inner class RateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyNameTextView = itemView.findViewById<TextView>(R.id.currencyName)
        private val currencyRateTextView = itemView.findViewById<TextView>(R.id.currencyRate)

        fun bindData(rate: CurrencyRate) {
            val ctx = itemView.context
            currencyNameTextView.text = ctx.getString(R.string.currency_name_field, rate.name)
            currencyRateTextView.text = ctx.getString(R.string.currency_rate_field,
                rate.rate.toString())
        }
    }
}