package com.majd_alden.storyviewerlibrary.customview.bottom_sheet

import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.majd_alden.storyviewerlibrary.R
import com.majd_alden.storyviewerlibrary.databinding.FragmentTestBottomShetBinding


class ListBottomSheetFragment(
    val title: String,
    private val models: List<BottomSheetItem>,
    private var selected: List<Int> = arrayListOf(),
    private val onClose: ((List<BottomSheetItem>) -> Unit)? = null,
    private val onClose2: ((String?, List<Int>) -> Unit)? = null,
    private val showIcon: Boolean = false,
    private val multipleSelect: Boolean = false,
    private val isCancel: Boolean = true,
    private val hasSearch: Boolean = true
) : BottomSheetDialogFragment() {





    private var binding: FragmentTestBottomShetBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentTestBottomShetBinding.inflate(inflater, container, false)

        binding?.edSearch?.setOnFocusChangeListener(object: View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, p1: Boolean) {

                val bottomSheet =
                    (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior = BottomSheetBehavior.from(bottomSheet!!)

                if (p1){
                    behavior.state  =  BottomSheetBehavior.STATE_EXPANDED
                }else{
                    behavior.state  =  BottomSheetBehavior.STATE_HALF_EXPANDED

                }
            }

        })
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = isCancel
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        dialog.setCancelable(isCancel)
        dialog.setOnShowListener {

            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)

            behavior.isHideable = false
            behavior.isDraggable = true

        }
//        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)




        return dialog
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    private fun finish() {
        val result = models.filter { adapter.selected.contains(it.id) }


        if (onClose2 == null) {
            onClose?.let { it(result) }
//            onClose(result)
        }

        onClose2?.let {
            if (result.isEmpty()) {
                it(null, result.map { it.id })
            } else {
                it(result.joinToString(" ,") { it.name }, result.map { it.id })
            }
        }
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.let {
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.let {
                val currentFocus = requireActivity().currentFocus
                currentFocus?.let {
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        }
    }


    val adapter = BottomSheetAdapter(ArrayList(models.sortedByDescending { selected.contains(it.id) }), selected, showIcon, multipleSelect)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let {
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.let {
                val currentFocus = requireActivity().currentFocus
                currentFocus?.let {
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        }

        binding?.tvTitle?.text = title


        binding?.tvOk?.setOnClickListener {
            finish()
        }
        binding?.rvList?.layoutManager = LinearLayoutManager(requireActivity())
        binding?.rvList?.adapter = adapter

        binding?.lblClose?.visibility = View.GONE
//        if (hasSearch){
        binding?.searchBar?.visibility = View.VISIBLE
//        }

        binding?.rvList?.requestFocus()
        binding?.edSearch?.addTextChangedListener { txt ->

            if (txt?.toString()?.isNotEmpty() == true) {
                val result : ArrayList<BottomSheetItem> = arrayListOf()
                models.forEach { item ->
                    if (item.name.contains(txt.toString() , true)){
                        result.add(item)
                    }
                }
                adapter.mModels.clear()
                adapter.mModels.addAll(result.sortedByDescending { selected.contains(it.id) })
                adapter.notifyDataSetChanged()
            } else {
                adapter.mModels.clear()
                adapter.mModels.addAll(models.sortedByDescending { selected.contains(it.id) })
                adapter.notifyDataSetChanged()
            }
        }

//        binding?.edSearch?.requestFocus()


    }


}