package io.xxlabs.messenger.backup.ui.save

import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.xxlabs.messenger.R
import io.xxlabs.messenger.databinding.ComponentEdittextTwoButtonDialogBinding
import io.xxlabs.messenger.support.dialog.info.InfoDialogUI

class EditTextTwoButtonInfoDialog : BottomSheetDialogFragment() {

    private lateinit var binding: ComponentEdittextTwoButtonDialogBinding
    private val dialogUI: EditTextTwoButtonDialogUI by lazy {
        requireArguments().get(ARG_UI) as EditTextTwoButtonDialogUI
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.component_edittext_two_button_dialog,
            container,
            false
        )

        dialogUI.spans?.let {
            binding.edittextDialogBody.text = getSpannableBody(dialogUI)
            binding.edittextDialogBody.movementMethod = LinkMovementMethod.getInstance()
        } ?: run {
            binding.edittextDialogBody.text = dialogUI.body
        }

        initClickListeners()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ui = dialogUI
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initClickListeners() {
        binding.edittextDialogPositiveButton.setOnClickListener {
            dismiss()
            dialogUI.onPositiveClick()
        }

        binding.edittextDialogNegativeButton.setOnClickListener {
            dialogUI.onNegativeClick()
            dismiss()
        }
    }

    private fun getSpannableBody(dialogUI: InfoDialogUI): Spannable {
        val builder = SpannableStringBuilder(dialogUI.body)

        dialogUI.spans?.forEach {
            val highlight = requireContext().getColor(it.color)
            val text = it.text
            val startIndex = dialogUI.body.indexOf(text, ignoreCase = true)
            val endIndex = startIndex + text.length

            builder.apply {
                it.url?.let { link ->
                    setSpan(
                        URLSpan(link),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
                setSpan(
                    ForegroundColorSpan(highlight),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        }
        return builder
    }

    override fun getTheme(): Int = R.style.RoundedModalBottomSheetDialog

    override fun onDismiss(dialog: DialogInterface) {
        dialogUI.onDismissed?.invoke()
        super.onDismiss(dialog)
    }

    companion object Factory {
        private const val ARG_UI: String = "ui"

        fun newInstance(dialogUI: EditTextTwoButtonDialogUI): EditTextTwoButtonInfoDialog =
            EditTextTwoButtonInfoDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_UI, dialogUI)
                }
            }
    }
}