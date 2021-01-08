package com.qaltera.currencyrates.androidApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.Source

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
        private val currencyRateTextView = itemView.findViewById<TextView>(R.id.currencyRateValue)
        private val currencySourceTextView = itemView.findViewById<TextView>(R.id.currencySource)

        fun bindData(rate: CurrencyRate) {
            val ctx = itemView.context
            currencyNameTextView.text = rate.name.toString()
            currencyRateTextView.text = rate.rate.toString()
            currencySourceTextView.text =
                ctx.getString(
                    if (rate.source == Source.MOEX) {
                        R.string.exchange
                    } else {
                        R.string.cbrf
                    }
                )
        }
    }
}