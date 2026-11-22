package com.example.sleepjournal.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sleepjournal.R
import com.example.sleepjournal.databinding.FragmentStatsBinding
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {

    private  var _binding: FragmentStatsBinding?=null
    private val binding get() = _binding!!

    private val viewModel: StatsVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPeriod()
        setupVM()

        viewModel.loadStats(7)
    }

    private fun setupVM() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.statsValue.collect { stats ->
                    if (stats.isLoading)
                        return@collect

                    //настраиваем длительость
                    val totalMinutes = stats.avgDuration.toLong()
                    val hours =totalMinutes/60
                    val minutes = totalMinutes%60
                    binding.avgSleepDuration.text = "${hours}ч ${minutes}м"
                    //оценку
                    binding.avgSleepQuality.text = String.format("%.1f из 5",stats.avgQuality)
                }


            }

        }
    }

    private fun setupPeriod() {
        binding.period.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked)
                when(checkedId)
                {
                    R.id.btnWeek ->viewModel.loadStats(7)
                    R.id.btnMonth->viewModel.loadStats(30)
                    R.id.btnAll->viewModel.loadStats(365)
                }
        }
    }


}