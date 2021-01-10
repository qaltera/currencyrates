package com.qaltera.currencyrates.androidApp

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRateSet
import com.qaltera.currencyrates.kmm.shared.entity.Source

class RatesAdapter(var rates: List<CurrencyRateSet>) : RecyclerView.Adapter<RatesAdapter
.RateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rate_set, parent, false)
            .run(::RateViewHolder)
    }

    override fun getItemCount(): Int = rates.count()

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bindData(rates[position])
    }

    inner class RateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyTitleTextView = itemView.findViewById<TextView>(R.id.title)
        private val currencyName1TextView = itemView.findViewById<TextView>(R.id.currencyName1)
        private val currencyValue1TextView = itemView.findViewById<TextView>(R.id.currencyValue1)
        private val currencyName2TextView = itemView.findViewById<TextView>(R.id.currencyName2)
        private val currencyValue2TextView = itemView.findViewById<TextView>(R.id.currencyValue2)
        private val changeValue1TextView = itemView.findViewById<TextView>(R.id.changeValue1)
        private val changeValue2TextView = itemView.findViewById<TextView>(R.id.changeValue2)
        private val updatedAtTextView = itemView.findViewById<TextView>(R.id.updatedAt)

        fun bindData(rate: CurrencyRateSet) {
            val ctx = itemView.context
            currencyTitleTextView.text =
                ctx.getString(
                    if (rate.source == Source.MOEX) {
                        R.string.exchange_rate
                    } else {
                        R.string.cbrf_rate
                    }
                )
            currencyName1TextView.text = ctx.getString(R.string.usd)
            currencyName2TextView.text = ctx.getString(R.string.eur)
            colorValueWithChange(currencyValue1TextView,
                rate.usdRate.change, ctx)
            colorValueWithChange(currencyValue2TextView,
                rate.eurRate.change, ctx)
            currencyValue1TextView.text = formatValue(rate.usdRate.rate)
            currencyValue2TextView.text = formatValue(rate.eurRate.rate)
            changeValue1TextView.text = formatValue(rate.usdRate.change, needSign = true)
            changeValue2TextView.text = formatValue(rate.eurRate.change, needSign = true)
            if (rate.source == Source.MOEX) {
                updatedAtTextView.visibility = View.VISIBLE
                updatedAtTextView.text = String.format(ctx.getString(R.string.updated_at),
                    DateUtils.formatDateTime(ctx, System.currentTimeMillis(), 0))
            } else {
                updatedAtTextView.visibility = View.INVISIBLE
            }
        }

        private fun formatValue(number: Float?, needSign: Boolean = false): String? {
            return number?.let {
                val pattern = if (needSign) {
                    "%+.2f"
                } else {
                    "%.2f"
                }
                String.format(pattern, number)
            }
        }

        private fun colorValueWithChange(
            view: TextView, change: Float?, ctx: Context
        ) {
            val textColorId = when {
                change == null || change == 0F -> R.color.colorPrimaryText
                change > 0 -> R.color.green
                else -> R.color.red
            }
            view.setTextColor(
                ContextCompat.getColor(ctx, textColorId)
            )
        }
    }
}