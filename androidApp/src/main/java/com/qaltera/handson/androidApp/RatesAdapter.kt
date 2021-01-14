package com.qaltera.currencyrates.androidApp

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.qaltera.currencyrates.androidApp.RatesAdapter.RateViewHolder.CbrfRateViewHolder
import com.qaltera.currencyrates.androidApp.RatesAdapter.RateViewHolder.MoexRateViewHolder
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRateSet
import com.qaltera.currencyrates.kmm.shared.entity.Source

class RatesAdapter(var rates: List<CurrencyRateSet>) : RecyclerView.Adapter<RatesAdapter
.RateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CBRF_TYPE -> inflater
                .inflate(R.layout.item_rate_set_cbrf, parent, false)
                .run(::CbrfRateViewHolder)
            else -> inflater
                .inflate(R.layout.item_rate_set_moex, parent, false)
                .run(::MoexRateViewHolder)
        }
    }

    override fun getItemCount(): Int = rates.count()

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bindData(rates[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if (rates[position].source == Source.CBRF) {
            CBRF_TYPE
        } else {
            MOEX_TYPE
        }
    }

    companion object {
        private const val CBRF_TYPE = 0
        private const val MOEX_TYPE = 1

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
    }

    sealed class RateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindData(rate: CurrencyRateSet)

        class CbrfRateViewHolder(itemView: View) : RateViewHolder(itemView) {
            private val currencyTitle = itemView.findViewById<TextView>(R.id.title)
            private val currencyName1 = itemView.findViewById<TextView>(R.id.currencyName1)
            private val currencyValueToday1 = itemView.findViewById<TextView>(R.id.currencyValueToday1)
            private val currencyValueTomorrow1 = itemView.findViewById<TextView>(R.id
                .currencyValueTomorrow1)
            private val currencyName2 = itemView.findViewById<TextView>(R.id.currencyName2)
            private val currencyValueToday2 = itemView.findViewById<TextView>(R.id.currencyValueToday2)
            private val currencyValueTomorrow2 = itemView.findViewById<TextView>(R.id
                .currencyValueTomorrow2)

            override fun bindData(rate: CurrencyRateSet) {
                val ctx = itemView.context
                currencyTitle.text =
                    ctx.getString(R.string.cbrf_rate)
                currencyName1.text = ctx.getString(R.string.usd)
                currencyName2.text = ctx.getString(R.string.eur)
                val usdRate = rate.usdRate as CurrencyRate.CbrfCurrencyRate
                val eurRate = rate.eurRate as CurrencyRate.CbrfCurrencyRate

                currencyValueToday1.text = formatValue(usdRate.rateToday)
                currencyValueToday2.text = formatValue(eurRate.rateToday)
                currencyValueTomorrow1.text =
                    usdRate.rateTomorrow?.let { rateTomorrow ->
                        formatValue(rateTomorrow)
                    } ?: "-"

                currencyValueTomorrow2.text =
                    eurRate.rateTomorrow?.let { rateTomorrow ->
                        formatValue(rateTomorrow)
                    } ?: "-"

                colorValueWithChange(
                    currencyValueTomorrow1,
                    usdRate.rateTomorrow, usdRate.rateToday, ctx
                )

                colorValueWithChange(
                    currencyValueTomorrow2,
                    eurRate.rateTomorrow, eurRate.rateToday, ctx
                )
            }
        }

        class MoexRateViewHolder(itemView: View) : RateViewHolder(itemView) {
            private val currencyTitle = itemView.findViewById<TextView>(R.id.title)
            private val currencyName1 = itemView.findViewById<TextView>(R.id.currencyName1)
            private val currencyValue1 = itemView.findViewById<TextView>(R.id.currencyValue1)
            private val currencyName2 = itemView.findViewById<TextView>(R.id.currencyName2)
            private val currencyValue2 = itemView.findViewById<TextView>(R.id.currencyValue2)
            private val changeValue1 = itemView.findViewById<TextView>(R.id.changeValue1)
            private val changeValue2 = itemView.findViewById<TextView>(R.id.changeValue2)
            private val currencyValue3 = itemView.findViewById<TextView>(R.id.currencyValue3)
            private val changeValue3 = itemView.findViewById<TextView>(R.id.changeValue3)

            private val updatedAt = itemView.findViewById<TextView>(R.id.updatedAt)

            override fun bindData(rate: CurrencyRateSet) {
                val ctx = itemView.context
                currencyTitle.text =
                    ctx.getString(R.string.exchange_rate)
                currencyName1.text = ctx.getString(R.string.usd)
                currencyName2.text = ctx.getString(R.string.eur)
                val eurRate = rate.eurRate as CurrencyRate.MoexCurrencyRate
                val usdRate = rate.usdRate as CurrencyRate.MoexCurrencyRate
                val brentRate = rate.brentRate as CurrencyRate.MoexCurrencyRate
                colorValueWithChange(
                    currencyValue1,
                    usdRate.change, ctx
                )
                colorValueWithChange(
                    currencyValue2,
                    eurRate.change, ctx
                )
                colorValueWithChange(
                    currencyValue3,
                    brentRate.change, ctx
                )
                currencyValue1.text = formatValue(usdRate.rate)
                currencyValue2.text = formatValue(eurRate.rate)
                currencyValue3.text = formatValue(brentRate.rate)
                changeValue1.text = formatValue(usdRate.change, needSign = true)
                changeValue2.text = formatValue(eurRate.change, needSign = true)
                changeValue3.text = formatValue(brentRate.change, needSign = true)

                updatedAt.text = String.format(
                    ctx.getString(R.string.updated_at),
                    DateUtils.formatDateTime(ctx, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME)
                )
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
        }

        companion object {

            private fun colorValueWithChange (
                view: TextView, valueNew: Float?, valueOld: Float?, ctx: Context
            ) {
                val change = valueNew?.let { valueNew ->
                    valueOld?.let { valueOld ->
                        valueNew - valueOld
                    }
                }
                colorValueWithChange(
                    view,
                    change,
                    ctx
                )
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
}