package com.c2m.storyviewer.screen

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.c2m.storyviewer.R
import com.c2m.storyviewer.customview.StoriesProgressView
import com.c2m.storyviewer.data.Story
import com.c2m.storyviewer.data.StoryUser
import com.c2m.storyviewer.utils.CacheDataSourceFactory
import com.c2m.storyviewer.utils.OnSwipeTouchListener
import com.c2m.storyviewer.utils.hide
import com.c2m.storyviewer.utils.show
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
import kotlinx.android.synthetic.main.fragment_story_display.*
import java.util.*

class StoryDisplayFragment : Fragment(),
    StoriesProgressView.StoriesListener {

    private val position: Int by
    lazy { arguments?.getInt(EXTRA_POSITION) ?: 0 }

    private val storyUser: StoryUser by
    lazy {
        (arguments?.getParcelable<StoryUser>(
            EXTRA_STORY_USER
        ) as StoryUser)
    }

    private val stories: ArrayList<Story> by
    lazy { storyUser.stories }

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
    ): View? {
        return inflater.inflate(R.layout.fragment_story_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storyDisplayVideo.useController = false

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
            storiesProgressView?.startStories()
        } else {
            // restart animation
            counter = MainActivity.progressState.get(arguments?.getInt(EXTRA_POSITION) ?: 0)
            storiesProgressView?.startStories(counter)
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        storiesProgressView?.abandon()
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
            storyDisplayVideo.show()
            storyDisplayImage.hide()
            storyDisplayVideoProgress.show()
            initializePlayer()
        } else {
            storyDisplayVideo.hide()
            storyDisplayVideoProgress.hide()
            storyDisplayImage.show()
            Glide.with(this).load(stories[counter].url).into(storyDisplayImage)
        }

        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            timeInMillis = stories[counter].storyDate
        }
        storyDisplayTime.text = DateFormat.format("MM-dd-yyyy HH:mm:ss", cal).toString()

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

        storyDisplayVideo.setShutterBackgroundColor(Color.BLACK)
        storyDisplayVideo.player = simpleExoPlayer

        simpleExoPlayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                storyDisplayVideoProgress.hide()
                if (counter == stories.size.minus(1)) {
                    pageViewOperator?.nextPageView()
                } else {
                    storiesProgressView?.skip()
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                if (isLoading) {
                    storyDisplayVideoProgress.show()
                    pressTime = System.currentTimeMillis()
                    pauseCurrentStory()
                } else {
                    storyDisplayVideoProgress.hide()
                    storiesProgressView?.getProgressWithIndex(counter)
                        ?.setDuration(simpleExoPlayer?.duration ?: 8000L)
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
                    next -> {
                        if (counter == stories.size - 1) {
                            pageViewOperator?.nextPageView()
                        } else {
                            storiesProgressView?.skip()
                        }
                    }
                    previous -> {
                        if (counter == 0) {
                            pageViewOperator?.backPageView()
                        } else {
                            storiesProgressView?.reverse()
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
        previous.setOnTouchListener(touchListener)
        next.setOnTouchListener(touchListener)

        storiesProgressView?.setStoriesCountDebug(
            stories.size, position = arguments?.getInt(EXTRA_POSITION) ?: -1
        )
        storiesProgressView?.setAllStoryDuration(4000L)
        storiesProgressView?.setStoriesListener(this)

        Glide.with(this).load(storyUser.profilePicUrl).circleCrop().into(storyDisplayProfilePicture)
        storyDisplayNick.text = storyUser.username
    }

    private fun setupMoreMenu() {
        if (storyUser.isShowMoreMenu) {
            moreIV.visibility = View.VISIBLE
            moreIV.setOnClickListener { view ->
                setupMoreMenu(view)
            }
        } else {
            moreIV.visibility = View.GONE
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
        if (storyOverlay == null || storyOverlay.alpha != 0F) return

        storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (storyOverlay == null || storyOverlay.alpha != 1F) return

        storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        MainActivity.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        return MainActivity.progressState.get(position)
    }

    fun pauseCurrentStory() {
        simpleExoPlayer?.playWhenReady = false
        storiesProgressView?.pause()
    }

    fun resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            storiesProgressView?.resume()
        }
    }

    companion object {
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_STORY_USER = "EXTRA_STORY_USER"
        fun newInstance(position: Int, story: StoryUser): StoryDisplayFragment {
            return StoryDisplayFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_POSITION, position)
                    putParcelable(EXTRA_STORY_USER, story)
                }
            }
        }
    }
}