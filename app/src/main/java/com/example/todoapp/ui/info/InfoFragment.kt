package com.example.todoapp.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.todoapp.R
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.databinding.InfoFragmentDialogBinding
import com.example.todoapp.ui.home.HomeAndAddViewModel
import java.text.SimpleDateFormat

class InfoFragment : DialogFragment() {

    private val homeViewModel: HomeAndAddViewModel by activityViewModels()

    private var _binding: InfoFragmentDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = InfoFragmentDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()

        return root

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun init() {

        val formatter = SimpleDateFormat("yyyy-MM-dd")

        when (homeViewModel.getFilledModel().importance) {
            ToDoItem.Importance.LOW -> binding.importanceInfo.text = "Low"
            ToDoItem.Importance.NORMAL -> binding.importanceInfo.text = "Normal"
            ToDoItem.Importance.URGENT -> binding.importanceInfo.text = "Urgent"
        }

        when (homeViewModel.getFilledModel().deadline) {

            null -> binding.deadlineInfo.text = "-"
            else -> {

                binding.deadlineInfo.text = formatter.format(homeViewModel.getFilledModel().deadline)

            }

        }

        when (homeViewModel.getFilledModel().isDone) {

            true -> binding.doneInfo.text = "Yes"

            false -> binding.doneInfo.text = "No"

        }

        binding.creationInfo.text = formatter.format(homeViewModel.getFilledModel().creationDate)

        when (homeViewModel.getFilledModel().modificationDate) {

            null -> binding.modificationInfo.text = "-"
            else -> {

                binding.modificationInfo.text = formatter.format(homeViewModel.getFilledModel().modificationDate)

            }

        }

        binding.textInfo.text = homeViewModel.getFilledModel().text

        homeViewModel.removeFilledModel()

    }

}