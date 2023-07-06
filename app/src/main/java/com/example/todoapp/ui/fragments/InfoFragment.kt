package com.example.todoapp.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.todoapp.databinding.InfoFragmentDialogBinding
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import java.text.SimpleDateFormat
import java.util.Date

class InfoFragment : DialogFragment() {

    private var _binding: InfoFragmentDialogBinding? = null
    private val binding get() = _binding!!

    private var toDoItemEntity : TaskModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: Bundle? = arguments
        if (args != null) {
            toDoItemEntity = args.getParcelable("ITEM")
        }
        if (savedInstanceState != null) {
            toDoItemEntity = savedInstanceState.getParcelable("ITEM")
        }

        val inflater = LayoutInflater.from(requireContext())
        val dialogBinding = InfoFragmentDialogBinding.inflate(inflater, null, false)
        _binding = dialogBinding

        init()

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(dialogBinding.root)
        return builder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ITEM", toDoItemEntity)
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

        val format = SimpleDateFormat("yyyy-MM-dd")

        when (toDoItemEntity!!.priority) {
            Importance.LOW -> binding.importanceInfo.text = "Low"
            Importance.BASIC -> binding.importanceInfo.text = "Normal"
            Importance.IMPORTANT -> binding.importanceInfo.text = "Urgent"
        }

        when (toDoItemEntity!!.deadline) {
            null -> binding.deadlineInfo.text = "-"
            else -> {

                val timestamp: Long = toDoItemEntity!!.deadline!!

                val date = Date(timestamp)

                val dateString = format.format(date)

                binding.deadlineInfo.text = dateString
            }
        }

        when (toDoItemEntity!!.isDone) {
            true -> binding.doneInfo.text = "Yes"
            false -> binding.doneInfo.text = "No"
        }

        val timestampCreation: Long = toDoItemEntity!!.creationTime!!

        val dateCreation = Date(timestampCreation)

        val dateCreationString = format.format(dateCreation)

        binding.creationInfo.text = dateCreationString


        when (toDoItemEntity!!.modifyingTime) {
            null -> binding.modificationInfo.text = "-"
            else -> {

                val timestampChanging: Long = toDoItemEntity!!.modifyingTime!!

                val dateChanging = Date(timestampChanging
                )

                val dateChangingString = format.format(dateChanging)

                binding.modificationInfo.text = dateChangingString
            }
        }

        binding.textInfo.text = toDoItemEntity!!.text
    }

    companion object {
        fun newInstance(bundle: Bundle): InfoFragment {
            val fragment = InfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}