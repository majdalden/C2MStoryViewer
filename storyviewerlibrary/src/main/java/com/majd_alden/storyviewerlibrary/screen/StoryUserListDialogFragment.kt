package com.majd_alden.storyviewerlibrary.screen

import android.content.*
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.majd_alden.storyviewerlibrary.R
import com.majd_alden.storyviewerlibrary.data.StoryUser
import com.majd_alden.storyviewerlibrary.data.StoryViewer
import com.majd_alden.storyviewerlibrary.databinding.FragmentItemListDialogListDialogBinding
import com.majd_alden.storyviewerlibrary.databinding.FragmentItemListDialogListDialogItemBinding

// TODO: Customize parameter argument names


class StoryUserListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = StoryUserAdapter()

    private var storyId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        storyId = arguments?.getInt("story_id", 0) ?: 0

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {

                    if (p1?.getIntExtra("story_id" , 0)?.equals(storyId ?: 0) == true) {
                        val users = p1?.extras?.getParcelableArrayList<StoryViewer>("viewers")
                            ?: arrayListOf()

                        Log.d("DDDD", "onReceive: users in story: ${users.size}")
                        adapter.models.clear()
                        adapter.models.addAll(users)
                        adapter.notifyDataSetChanged()

                    }
                }

            }, IntentFilter(StoryViewerFragment.SET_VIEW_VIEWERS_ACTION))

        view.findViewById<RecyclerView>(R.id.list)?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StoryUserListDialogFragment.adapter
        }
    }

    private inner class ViewHolder internal constructor(binding: FragmentItemListDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val image: ShapeableImageView = binding.image
        internal val text: TextView = binding.text
    }

    private inner class StoryUserAdapter : RecyclerView.Adapter<ViewHolder>() {

        var models: java.util.ArrayList<StoryViewer> = arrayListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentItemListDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = models[position].phone ?: ""
            models[position].pictureUrl.let {

                Glide.with(holder.image).load(it)
            }
        }

        override fun getItemCount(): Int {
            return models.size
        }
    }

    companion object {

        var onDismiss: (() -> Unit)? = null

        // TODO: Customize parameters
        fun newInstance(storyId: Int): StoryUserListDialogFragment =
            StoryUserListDialogFragment().apply {
                Bundle().apply {
                    putInt("story_id", storyId)
                }.let {
                    arguments = it
                }

            }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.let { it() }
    }


}