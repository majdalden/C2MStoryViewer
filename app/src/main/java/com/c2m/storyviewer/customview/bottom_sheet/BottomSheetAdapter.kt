package com.majd_alden.storyviewerlibrary.customview.bottom_sheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.majd_alden.storyviewerlibrary.databinding.ItemBottomSheetBinding

class BottomSheetAdapter(
    val mModels: ArrayList<BottomSheetItem>,
    public val mSelected: List<Int> = arrayListOf(),
    private val showIcon: Boolean = false,
    private val multipleSelect: Boolean = false,
) :
    RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {


    val selected: ArrayList<Int> = arrayListOf()

    init {
        selected.addAll(mSelected)
    }

    class ViewHolder(val binding: ItemBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mModels[position]
        holder.binding.let {
            it.imgChecked.visibility = if (selected.contains(model.id)) {
                View.VISIBLE
            } else View.GONE

            it.tvName.text = mModels[position].name

            it.imgIcon.visibility = if (showIcon) {
                View.VISIBLE
            } else View.GONE

            it.root.setOnClickListener {
                if (multipleSelect) {
                    if (selected.contains(model.id)) {
                        selected.remove(model.id)
                    } else {
                        selected.add(model.id)
                    }
                } else {
                    if (selected.contains(model.id)) {
                        selected.remove(model.id)
                    } else {
                        selected.clear()
                        selected.add(model.id)
                    }
                }
                notifyDataSetChanged()
            }

            if (showIcon) {
                try {
//                    Picasso.get().load(model.image).into(holder.binding.imgIcon)
                } catch (ex: Exception) {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mModels.size
    }
}