package com.example.sleepjournal.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sleepjournal.R
import com.example.sleepjournal.databinding.FragmentHomeBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sleepjournal.DataClasses.SleepRecord
import kotlinx.coroutines.launch
class HomeFragment : Fragment() {
    private  var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!

    private  val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: SleepRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Теперь binding не null, и мы можем с ним работать!
        setupRecyclerView()
        observeViewModel()

        binding.fabAddRecord.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addEditFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = SleepRecordAdapter(
            onRecordClick = { record ->
                val bundle = Bundle().apply {
                    putLong("recordId",record.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_addEditFragment,bundle)
        },
            onRecordLongClick = {
                record -> showDeleteDialog(record)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }
    private fun showDeleteDialog(record: SleepRecord) {

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить запись?")
            .setMessage("Вы уверены что хотите удалить эту запись о сне?")
            .setPositiveButton("Удалить"){_,_->
                viewModel.deleteRecord(record)
            }
            .setNegativeButton("Отмена",null)
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sleepRecords.collect { records ->
                    adapter.submitList(records)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}