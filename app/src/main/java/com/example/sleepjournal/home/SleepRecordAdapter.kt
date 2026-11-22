package com.example.sleepjournal.home
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sleepjournal.DataClasses.SleepRecord
import com.example.sleepjournal.R
import com.example.sleepjournal.databinding.ItemSleepRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SleepRecordAdapter(
    private val onRecordClick: (SleepRecord) -> Unit,
    private val onRecordLongClick: (SleepRecord)->Unit
) : ListAdapter<SleepRecord, SleepRecordAdapter.SleepViewHolder>(SleepDiffCallback())
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SleepViewHolder {
        val binding = ItemSleepRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SleepViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SleepViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onRecordClick,onRecordLongClick)
    }

    class SleepViewHolder(
        private val binding: ItemSleepRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val locale = Locale.getDefault()

        private val dateFormatter = SimpleDateFormat(
            "EEEE, d MMMM",
            locale
        )

        fun bind(
            record: SleepRecord,
            onRecordClick: (SleepRecord) -> Unit,
            onRecordLongClick: (SleepRecord) -> Unit
        ) {
            // Дата пробуждения
            val date = dateFormatter.format(Date(record.wakeUpTime))
            binding.dateText.text = date.replaceFirstChar {
                it.titlecase(locale)
            }
            // Продолжительность сна
            binding.sleepDurationText.text =
                formatDuration(record.actualSleepMinutes)
            // Оценка качества
            setQuality(record.quality)
            // Количество пробуждений
            val awakenings = record.awakeningsCount.coerceAtLeast(0)
            binding.awakeningsText.text =
                itemView.resources.getQuantityString(
                    R.plurals.awakenings_count,
                    awakenings,
                    awakenings
                )
            // Индикаторы заметок
            val hasPreSleepNote = record.preSleepNote.isNotBlank()
            val hasPostSleepNote = record.postSleepNote.isNotBlank()
            binding.preSleepNoteIcon.visibility =
                if (hasPreSleepNote) View.VISIBLE else View.GONE
            binding.postSleepNoteIcon.visibility =
                if (hasPostSleepNote) View.VISIBLE else View.GONE


            // Открытие подробной записи
            binding.root.setOnClickListener {
                onRecordClick(record)
            }
            binding.root.setOnLongClickListener {
                onRecordLongClick(record)
                true
            }
        }

        private fun setQuality(quality: Int) {
            val style = when (quality) {
                1 -> QualityStyle(
                    "😫", R.color.rate1
                )
                2 -> QualityStyle(
                    "😕", R.color.rate2
                )
                3 -> QualityStyle(
                    "😐", R.color.rate3
                )
                4 -> QualityStyle(
                    "🙂", R.color.rate4
                )
                5 -> QualityStyle(
                    "😌", R.color.rate5
                )
                else -> QualityStyle(
                    "—", R.color.no_rate
                )
            }
            binding.qualityEmoji.text = style.emoji
            //Динамический цвет фона оценки
            val density = itemView.resources.displayMetrics.density
            binding.qualityBadge.background =
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 10f * density
                    setColor(androidx.core.content.ContextCompat.getColor(itemView.context, style.backgroundColor))
                }
        }

        //форматирование времени
        private fun formatDuration(minutes: Long): String {
            val totalMinutes = minutes.coerceAtLeast(0L)

            val hours = totalMinutes / 60
            val remainingMinutes = totalMinutes % 60

            return when {
                hours > 0 && remainingMinutes > 0 -> "$hours ч $remainingMinutes мин"

                hours > 0 -> "$hours ч"

                else -> "$remainingMinutes мин"
            }
        }

        private data class QualityStyle(
            val emoji: String,
            val backgroundColor: Int
        )
    }
}

private class SleepDiffCallback : DiffUtil.ItemCallback<SleepRecord>() {

    override fun areItemsTheSame(
        oldItem: SleepRecord,
        newItem: SleepRecord
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SleepRecord, newItem: SleepRecord): Boolean {
        return oldItem == newItem
    }
}
