package com.example.todoapp.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.databinding.InfoFragmentDialogBinding
import com.example.todoapp.ui.util.factory
import com.example.todoapp.ui.viewmodels.InfoViewModel
import java.text.SimpleDateFormat

class InfoFragment : DialogFragment() {

    private val infoViewModel: InfoViewModel by viewModels { factory() }

    private var _binding: InfoFragmentDialogBinding? = null
    private val binding get() = _binding!!
    private var toDoItemID: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle? = arguments
        if (args != null) {
            toDoItemID = args.getString("TASK_ID_INFO").toString()
        }
        if (savedInstanceState != null) {
            toDoItemID = savedInstanceState.getString("TASK_ID_INFO_SIS").toString()
        }

        val inflater = LayoutInflater.from(requireContext())
        val dialogBinding = InfoFragmentDialogBinding.inflate(inflater, null, false)
        _binding = dialogBinding

        // Initialize your dialog content here if needed
        init()

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(dialogBinding.root)
        return builder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("TASK_ID_INFO_SIS", toDoItemID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = InfoFragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val toDoObject = infoViewModel.getItemById(id = this.toDoItemID)

        when (toDoObject.importance) {
            ToDoItem.Importance.LOW -> binding.importanceInfo.text = "Low"
            ToDoItem.Importance.NORMAL -> binding.importanceInfo.text = "Normal"
            ToDoItem.Importance.URGENT -> binding.importanceInfo.text = "Urgent"
        }

        when (toDoObject.deadline) {
            null -> binding.deadlineInfo.text = "-"
            else -> {
                binding.deadlineInfo.text = formatter.format(toDoObject.deadline)
            }
        }

        when (toDoObject.isDone) {
            true -> binding.doneInfo.text = "Yes"
            false -> binding.doneInfo.text = "No"
        }

        binding.creationInfo.text = formatter.format(toDoObject.creationDate)

        when (toDoObject.modificationDate) {
            null -> binding.modificationInfo.text = "-"
            else -> {
                binding.modificationInfo.text = formatter.format(toDoObject.modificationDate)
            }
        }

        binding.textInfo.text = toDoObject.text
    }

    companion object {
        fun newInstance(bundle: Bundle): InfoFragment {
            val fragment = InfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}