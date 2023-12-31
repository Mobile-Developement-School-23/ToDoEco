package com.example.todoapp.ui.fragments

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import com.example.todoapp.R
import com.example.todoapp.databinding.SettingsFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment : BottomSheetDialogFragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.apply {
            isHideable = true
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { sheet ->
            val animator = ObjectAnimator.ofFloat(sheet, View.TRANSLATION_Y,
                sheet.height.toFloat(), 0f)
            animator.duration = 500
            animator.start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs",
            Context.MODE_PRIVATE)
        buttonsInit()
        return root
    }

    private fun buttonsInit() {
        val editor = sharedPreferences.edit()
        var theme = ""
        binding.darkRadioButton.setOnClickListener {
            theme = "dark"
            setAnimation(66, 60, 99)
            binding.lightRadioButton.isChecked = false
            binding.defaultRadioButton.isChecked = false
        }
        binding.lightRadioButton.setOnClickListener {
            theme = "light"
            setAnimation(117, 92, 72)
            binding.darkRadioButton.isChecked = false
            binding.defaultRadioButton.isChecked = false
        }
        binding.defaultRadioButton.setOnClickListener {
            theme = "default"
            setAnimation(105, 105, 105)
            binding.darkRadioButton.isChecked = false
            binding.lightRadioButton.isChecked = false
        }
        binding.saveButton.setOnClickListener {
            if (theme != "") {
                editor.putString("theme", theme)
                editor.apply()
                when (theme) {
                    "dark" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    "light" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    "default" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
            }
        }
    }

    private fun setAnimation(R : Int, G : Int, B : Int) {
        val layout = binding.settingsLayout
        val originalColor = (layout.background as ColorDrawable).color
        val newColor = Color.rgb(R, G, B)
        val animator = ObjectAnimator.ofObject(layout, "backgroundColor",
            ArgbEvaluator(), originalColor, newColor)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
        layout.postDelayed({
            val reverseAnimator = ObjectAnimator.ofObject(layout, "backgroundColor",
                ArgbEvaluator(), newColor, originalColor)
            reverseAnimator.duration = 1000
            reverseAnimator.interpolator = AccelerateDecelerateInterpolator()
            reverseAnimator.start()
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}