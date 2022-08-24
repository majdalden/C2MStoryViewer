package com.majd_alden.storyviewerlibrary.screen

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.majd_alden.storyviewerlibrary.BuildConfig
import com.majd_alden.storyviewerlibrary.R
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
    private var localBroadcastManager: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        storyId = arguments?.getInt("story_id", 0) ?: 0

        registerReceiverLocalBroadcastManager()

        binding.audiencesListRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StoryUserListDialogFragment.adapter
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiverLocalBroadcastManager()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiverLocalBroadcastManager()
    }

    private inner class ViewHolder(binding: FragmentItemListDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val image: ShapeableImageView = binding.image
        val text: TextView = binding.text
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
            holder.text.text = models[position].phone
            models[position].pictureUrl.let {
                Glide.with(holder.image)
                    .load(it)
                    .placeholder(R.drawable.ic_user_placeholder_km)
                    .error(R.drawable.ic_user_placeholder_km)
            }
        }

        override fun getItemCount(): Int {
            return models.size
        }
    }

    companion object {

        var onDismiss: (() -> Unit)? = null
        var onCancel: (() -> Unit)? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancel?.let { it() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.let { it() }
    }

    private fun unregisterReceiverLocalBroadcastManager() {
        val context = context ?: return
        val localBroadcastManager = localBroadcastManager ?: return
        LocalBroadcastManager.getInstance(context)
            .unregisterReceiver(localBroadcastManager)
    }

    private fun registerReceiverLocalBroadcastManager() {
        val context = context ?: return
        if (localBroadcastManager == null) {
            localBroadcastManager = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    activity?.runOnUiThread {
                        if (intent?.getIntExtra("story_id", 0)?.equals(storyId ?: 0) == true) {
                            val users =
                                intent.extras?.getParcelableArrayList<StoryViewer>("viewers")
                                    ?: arrayListOf()

                            if (BuildConfig.DEBUG)
                                Log.d("DDDD", "onReceive: users in story: ${users.size}")
                            adapter.models.clear()
                            adapter.models.addAll(users)
                            adapter.notifyItemRangeInserted(0, users.size)
//                        adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(context)
            .registerReceiver(
                localBroadcastManager!!,
                IntentFilter(StoryViewerFragment.SET_VIEW_VIEWERS_ACTION)
            )
    }


}