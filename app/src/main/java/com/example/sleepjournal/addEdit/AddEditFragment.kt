package com.example.sleepjournal.addEdit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.media.quality.MediaQualityContract
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sleepjournal.DataClasses.SleepRecord
import com.example.sleepjournal.databinding.FragmentAddEditBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditFragment : Fragment() {
////////////////////////////////////////////////////////////////////
    //editing
    private  var edtRecordId: Long = 0L
    //VM
    private val viewModel: AddEditViewModel by viewModels()
    // binding
    private  var _binding: FragmentAddEditBinding?=null
    private val binding get() = _binding!!

    //data i vremya
    private var bedTime: Long = 0L
    private  var wakeUpTime: Long = 0L
    private  val dateTimeFormatter = SimpleDateFormat(
        "dd.MM.yyyy, HH:mm",
        Locale.getDefault()
    )
/////////////////////////////////////////////////////////////////////

    //nastroyka binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditBinding.inflate(inflater,container,false)
        return binding.root
    }
    //настройка кнопок
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtRecordId = arguments?.getLong("recordId",0L)?:0L

        setupQualitySelector()
        setupDateTimePickers()
        updateDateTimeBtns()

        if (edtRecordId>0L)
        {
            loadRecord()
        }
        binding.saveBtn.setOnClickListener {
            val record = createRecord()
            if (record != null) {
                viewModel.saveRecord(record) {
                    findNavController().popBackStack()
                }
            }
        }
    }
    //очищение
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //настройка вида эмодзи до выбора и после
    private var selectedQuality = 0
    private fun setupQualitySelector()
    {
        val  emojis = listOf(
            binding.quality1,
            binding.quality2,
            binding.quality3,
            binding.quality4,
            binding.quality5
        )
        emojis.forEachIndexed { index, emoji ->
            emoji.alpha = 0.4f

            emoji.setOnClickListener {
                selectedQuality = index + 1

                emojis.forEachIndexed { i, item ->
                    //если текущий эмодзи равен выбранному = непрозрачно, иначе прозрачно
                    item.alpha = if (i==index) 1f else 0.4f
                }
            }
        }
    }

    //подключение даты и времени для кнопок
    private fun setupDateTimePickers()
    {
        val now = System.currentTimeMillis()
        //начально значиение для засыпания и пробуждения
        bedTime = now - 8*60*60*1000L
        wakeUpTime = now

        binding.bedBtn.setOnClickListener {
            showDTPicker(bedTime){
                bedTime = it
                updateDateTimeBtns()
            }
        }
        binding.wakeUpBtn.setOnClickListener {
            showDTPicker(wakeUpTime)
            {
                wakeUpTime=it
                updateDateTimeBtns()
            }
        }

    }

    //настройка DatePicker & TimePicker x3
    private fun showDTPicker(initialTimestamp: Long, onSelected:(Long)-> Unit)
    {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = initialTimestamp
        }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(
                    requireContext(), { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        onSelected(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }


    private  fun  createRecord(): SleepRecord?{
        val fallAsleep = binding.fallAsleepInput.text?.toString()?.toIntOrNull()
        val awakenings = binding.awakeningsInput.text?.toString()?.toIntOrNull()
        val preSleepNote = binding.preSleepNoteInput.text?.toString()?.trim().orEmpty()
        val posSleepNote = binding.postSleepNoteInput.text?.toString()?.trim().orEmpty()

        //проверки обязательных полей
        if (selectedQuality !in 1..5){
            binding.qualitySelector.requestFocus()
            Toast.makeText(requireContext(),
                "Выберитк качество сна",
                Toast.LENGTH_SHORT).show()
            return null
        }

        if (fallAsleep == null || fallAsleep<0) {
            binding.fallAsleepInput.error = "Введите корректное время"
            return null
        }
        if (awakenings == null || awakenings<0) {
            binding.awakeningsInput.error = "Введите корректное количество"
            return null
        }
        if (wakeUpTime<= bedTime)
        {
            Toast.makeText(
                requireContext(),
                "Время пробуждения должно быть позже отхода ко сну",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        //время в кровати в минутах для проверки корректного времени засыпания
        val timeInBed  = (wakeUpTime-bedTime)/60000L

        if (fallAsleep.toLong()>=timeInBed)
        {
            binding.fallAsleepInput.error = "Время засыпания больше времени в кровати!"
            return  null
        }

        return SleepRecord(
            id = edtRecordId,
            bedTime = bedTime,
            fallAsleepMinutes = fallAsleep,
            awakeningsCount = awakenings,
            wakeUpTime = wakeUpTime,
            quality = selectedQuality,
            preSleepNote = preSleepNote,
            postSleepNote = posSleepNote,
        )

    }
    private fun loadRecord() {
        viewLifecycleOwner.lifecycleScope.launch {
            val record = viewModel.getRecordById(edtRecordId)
            record?.let {
                bedTime = it.bedTime
                wakeUpTime = it.wakeUpTime
                updateDateTimeBtns()

                binding.fallAsleepInput.setText(it.fallAsleepMinutes.toString())
                binding.awakeningsInput.setText(it.awakeningsCount.toString())
                binding.preSleepNoteInput.setText(it.preSleepNote)
                binding.postSleepNoteInput.setText(it.postSleepNote)

                setSelectedQuality(it.quality)
            }

        }
    }

    private fun setSelectedQuality(quality: Int) {

        selectedQuality = quality
        val emojis = listOf(
            binding.quality1,
            binding.quality2,
            binding.quality3,
            binding.quality4,
            binding.quality5
        )
        emojis.forEachIndexed { index, emoji ->
            emoji.alpha = if (index + 1==quality) 1f else 0.4f
        }
    }
//обновить отображаемое время
    private  fun updateDateTimeBtns(){
        binding.bedBtn.text = dateTimeFormatter.format(Date(bedTime))
        binding.wakeUpBtn.text = dateTimeFormatter.format(Date(wakeUpTime))
    }
}