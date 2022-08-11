package com.majd_alden.storyviewerlibrary.screen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.civitasv.ioslike.dialog.DialogBottom
import com.civitasv.ioslike.dialog.DialogNormal
import com.civitasv.ioslike.model.DialogText
import com.civitasv.ioslike.model.DialogTextStyle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.majd_alden.storyviewerlibrary.R
import com.majd_alden.storyviewerlibrary.customview.StoriesProgressView
import com.majd_alden.storyviewerlibrary.data.Story
import com.majd_alden.storyviewerlibrary.data.StoryUser
import com.majd_alden.storyviewerlibrary.databinding.FragmentStoryViewerBinding
import com.majd_alden.storyviewerlibrary.utils.CacheDataSourceFactory
import com.majd_alden.storyviewerlibrary.utils.OnSwipeTouchListener
import com.majd_alden.storyviewerlibrary.utils.hide
import com.majd_alden.storyviewerlibrary.utils.show
import java.util.*

class StoryViewerFragment : Fragment(),
    StoriesProgressView.StoriesListener {

    private lateinit var binding: FragmentStoryViewerBinding
    private val position: Int by
    lazy { arguments?.getInt(EXTRA_POSITION) ?: 0 }

    private val storyUser: StoryUser by
    lazy {
        (arguments?.getParcelable<StoryUser>(
            EXTRA_STORY_USER
        ) as StoryUser)
    }

    private val stories: MutableList<Story> by
    lazy { storyUser.stories }

//    private var position = 0
//    private lateinit var storyUser: StoryUser
//    private var stories: MutableList<Story> = mutableListOf()

    private var simpleExoPlayer: ExoPlayer? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private var pageViewOperator: PageViewOperator? = null
    private var counter = 0
    private var pressTime = 0L
    private var limit = 500L
    private var onResumeCalled = false
    private var onVideoPrepared = false

    private var moreMenuDialogBottom: DialogBottom? = null
    private val dialogTextItemList: List<DialogText>? = null
    private val isAddDeleteItemToMoreMenu = true
    private val isViewAudienceToMoreMenu = true
    private var isAddedDialogTextItemList = false
    private var isUserDismissMoreMenu = false
    private val onClickDeleteStoryListener: ((position: Int) -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.storyDisplayVideo.useController = false

        isAddedDialogTextItemList = false
        isUserDismissMoreMenu = false

        updateStory()
        setUpUi()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.pageViewOperator = context as PageViewOperator
    }

    override fun onStart() {
        super.onStart()
        counter = restorePosition()
    }

    override fun onResume() {
        super.onResume()

        onResumeCalled = true
        if (stories[counter].isVideo() && !onVideoPrepared) {
            simpleExoPlayer?.playWhenReady = false
            return
        }

        simpleExoPlayer?.seekTo(5)
        simpleExoPlayer?.playWhenReady = true
        if (counter == 0) {
            binding.storiesProgressView.startStories()
        } else {
            // restart animation
            counter = StoryViewerActivity.progressState.get(arguments?.getInt(EXTRA_POSITION) ?: 0)
            binding.storiesProgressView.startStories(counter)
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        binding.storiesProgressView.abandon()
    }

    override fun onComplete() {
        simpleExoPlayer?.release()
        pageViewOperator?.nextPageView()
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        --counter
        savePosition(counter)
        updateStory()
    }

    override fun onNext() {
        if (stories.size <= counter + 1) {
            return
        }
        Log.e("StoryViewerFragment", "$counter: onNext stories[counter]: ${stories[counter]}")
        ++counter
        savePosition(counter)
        updateStory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleExoPlayer?.release()
    }

    private fun updateStory() {
        simpleExoPlayer?.stop()
        if (stories[counter].isVideo()) {
            binding.storyDisplayVideo.show()
            binding.storyDisplayImage.hide()
            binding.storyDisplayVideoProgress.show()
            initializePlayer()
        } else {
            binding.storyDisplayVideo.hide()
            binding.storyDisplayVideoProgress.show()
            binding.storyDisplayImage.show()

            toggleLoadMode(true)

            Glide.with(this)
                .load(stories[counter].url)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.storyDisplayVideoProgress.hide()
                        toggleLoadMode(false)
                        if (counter == stories.size.minus(1)) {
                            pageViewOperator?.nextPageView()
                        } else {
                            binding.storiesProgressView.skip()
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.storyDisplayVideoProgress.hide()
                        toggleLoadMode(false)
                        return false
                    }
                })
                .into(binding.storyDisplayImage)
        }

        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            timeInMillis = stories[counter].storyDate
        }
        binding.storyDisplayTime.text = DateFormat.format("MM-dd-yyyy HH:mm:ss", cal).toString()

        setupMoreMenu()
    }

    private fun initializePlayer() {
        val context = context ?: return
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context)
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(context, videoTrackSelectionFactory)

        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayer
                .Builder(context)
                .setTrackSelector(trackSelector)
                .setBandwidthMeter(bandwidthMeter.build())
                .build()
        } else {
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            simpleExoPlayer = ExoPlayer
                .Builder(context)
                .setTrackSelector(trackSelector)
                .setBandwidthMeter(bandwidthMeter.build())
                .build()
        }
//        mediaDataSourceFactory = CacheDataSourceFactory(context, 100 * 1024 * 1024, 5 * 1024 * 1024)
        mediaDataSourceFactory =
            CacheDataSourceFactory(context, 100 * 1024 * 1024, 100 * 1024 * 1024)

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(Uri.parse(stories[counter].url))
        )
        simpleExoPlayer?.setMediaSource(mediaSource)
        simpleExoPlayer?.prepare()

        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
        }

        binding.storyDisplayVideo.setShutterBackgroundColor(Color.BLACK)
        binding.storyDisplayVideo.player = simpleExoPlayer
        simpleExoPlayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                binding.storyDisplayVideoProgress.hide()
                if (counter == stories.size.minus(1)) {
                    pageViewOperator?.nextPageView()
                } else {
                    binding.storiesProgressView.skip()
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                if (isLoading) {
                    binding.storyDisplayVideoProgress.show()
                    pressTime = System.currentTimeMillis()
                    pauseCurrentStory()
                } else {
                    binding.storyDisplayVideoProgress.hide()
                    binding.storiesProgressView
                        .getProgressWithIndex(counter)
                        .setDuration(simpleExoPlayer?.duration ?: 8000L)
                    onVideoPrepared = true
                    resumeCurrentStory()
                }
            }
        })
    }

    fun toggleLoadMode(
        isLoading: Boolean
    ) {
        if (isLoading) {
            pressTime = System.currentTimeMillis()
            pauseCurrentStory()
        } else {
            resumeCurrentStory()
        }
    }

    private fun setUpUi() {
        val touchListener = object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeTop() {
                Toast.makeText(activity, "onSwipeTop", Toast.LENGTH_LONG).show()
            }

            override fun onSwipeBottom() {
                Toast.makeText(activity, "onSwipeBottom", Toast.LENGTH_LONG).show()
            }

            override fun onClick(view: View) {
                when (view) {
                    binding.next -> {
                        if (counter == stories.size - 1) {
                            pageViewOperator?.nextPageView()
                        } else {
                            binding.storiesProgressView.skip()
                        }
                    }
                    binding.previous -> {
                        if (counter == 0) {
                            pageViewOperator?.backPageView()
                        } else {
                            binding.storiesProgressView.reverse()
                        }
                    }
                }
            }

            override fun onLongClick() {
                hideStoryOverlay()
            }

            override fun onTouchView(view: View, event: MotionEvent): Boolean {
                super.onTouchView(view, event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        pressTime = System.currentTimeMillis()
                        pauseCurrentStory()
                        return false
                    }
                    MotionEvent.ACTION_UP -> {
                        showStoryOverlay()
                        resumeCurrentStory()
                        return limit < System.currentTimeMillis() - pressTime
                    }
                }
                return false
            }
        }
        binding.previous.setOnTouchListener(touchListener)
        binding.next.setOnTouchListener(touchListener)

        binding.storiesProgressView.setStoriesCountDebug(
            stories.size, position = arguments?.getInt(EXTRA_POSITION) ?: -1
        )
        binding.storiesProgressView.setAllStoryDuration(4000L)
        binding.storiesProgressView.setStoriesListener(this)

        Glide.with(this).load(storyUser.profilePicUrl).circleCrop()
            .into(binding.storyDisplayProfilePicture)
        binding.storyDisplayNick.text = storyUser.username
    }

    private fun setupMoreMenu() {
        if (storyUser.isShowMoreMenu) {
            binding.moreIV.visibility = View.VISIBLE
            binding.moreIV.setOnClickListener { view ->
                setupMoreMenu(view)
            }
        } else {
            binding.moreIV.visibility = View.GONE
        }
    }

    private fun setupMoreMenu(view: View) {
        val activity = activity ?: return

        val currentItem = position

        if (moreMenuDialogBottom == null) {
            moreMenuDialogBottom = DialogBottom(activity)
            moreMenuDialogBottom
                ?.setCancel(
                    getString(R.string.cancel),
                    true
                )
                ?.setOnDismissListener {
                    if (!isUserDismissMoreMenu) {
                        toggleLoadMode(isLoading = false)
                    }
                    isUserDismissMoreMenu = false
                }
        }
        if (moreMenuDialogBottom != null && !isAddedDialogTextItemList) {
            isAddedDialogTextItemList = true
            if (dialogTextItemList != null && dialogTextItemList.isNotEmpty()) {
                moreMenuDialogBottom?.setBottomList(dialogTextItemList)
            }
            if (isAddDeleteItemToMoreMenu) {
                moreMenuDialogBottom?.addBottomItem(
                    getString(R.string.delete),
                    { view1 ->
                        isUserDismissMoreMenu = true
                        val confirmationDeleteStoryDialog = DialogNormal(activity)
                        confirmationDeleteStoryDialog.setTitle(R.string.delete_this_story)
                            .setContent(activity.getString(R.string.are_you_sure_you_want_to_delete_this_story))
                            .setConfirm(
                                getString(R.string.delete),
                                { view2 ->
                                    isUserDismissMoreMenu = true
                                    confirmationDeleteStoryDialog.dismiss()
                                    moreMenuDialogBottom?.dismiss()
//                                        onComplete()
                                    toggleLoadMode(isLoading = false)
                                    onClickDeleteStoryListener?.invoke(currentItem)
                                },
                                DialogTextStyle.Builder(activity)
                                    .color(R.color.ios_like_red).build()
                            )
                            .setCancel(
                                getString(R.string.cancel),
                                true,
//                                DialogTextStyle.Builder(activity).color(R.color.ios_like_blue).build()
                                DialogTextStyle.Builder(activity).color(R.color.black).build()
                            )
                            .setOnDismissListener {
                                if (!isUserDismissMoreMenu) {
                                    toggleLoadMode(isLoading = false)
                                }
                                isUserDismissMoreMenu = false
                            }
                            .setCanceledOnTouchOutside(true)
                        confirmationDeleteStoryDialog.show()
                        moreMenuDialogBottom?.dismiss()
                    },
                    DialogTextStyle.Builder(activity).color(R.color.ios_like_red).build()
                )
            }
            if (isViewAudienceToMoreMenu) {
                moreMenuDialogBottom?.addBottomItem(
                    getString(R.string.view_audience)
                ) { view1 ->
                    isUserDismissMoreMenu = true
                    moreMenuDialogBottom?.dismiss()
                    toggleLoadMode(isLoading = false)
                }
            }
        }

        toggleLoadMode(isLoading = true)
        moreMenuDialogBottom?.show()
    }

    private fun showStoryOverlay() {
        if (binding.storyOverlay == null || binding.storyOverlay.alpha != 0F) return

        binding.storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (binding.storyOverlay == null || binding.storyOverlay.alpha != 1F) return

        binding.storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        StoryViewerActivity.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        return StoryViewerActivity.progressState.get(position)
    }

    fun pauseCurrentStory() {
        simpleExoPlayer?.playWhenReady = false
        binding.storiesProgressView.pause()
    }

    fun resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            binding.storiesProgressView.resume()
        }
    }

    companion object {
        private const val TAG = "StoryViewerFragment"
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_STORY_USER = "EXTRA_STORY_USER"
        fun newInstance(position: Int, story: StoryUser): StoryViewerFragment {
            return StoryViewerFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_POSITION, position)
                    putParcelable(EXTRA_STORY_USER, story)
                }
            }
        }
    }
}