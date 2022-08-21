package com.majd_alden.storyviewerlibrary.screen

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.format.DateFormat
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.google.android.exoplayer2.upstream.BuildConfig
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.majd_alden.storyviewerlibrary.R
import com.majd_alden.storyviewerlibrary.customview.StoriesProgressView
import com.majd_alden.storyviewerlibrary.data.Story
import com.majd_alden.storyviewerlibrary.data.StoryTextFont
import com.majd_alden.storyviewerlibrary.data.StoryUser
import com.majd_alden.storyviewerlibrary.databinding.FragmentStoryViewerBinding
import com.majd_alden.storyviewerlibrary.utils.*
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

    private var simpleExoPlayer: ExoPlayer? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private var pageViewOperator: PageViewOperator? = null
    private var counter = 0
    private var pressTime = 0L
    private var limit = 500L
    private var onResumeCalled = false
    private var onVideoPrepared = false
    private var onImagePrepared = false

    private var moreMenuDialogBottom: DialogBottom? = null
    private val dialogTextItemList: List<DialogText>? = null
    private val isAddDeleteItemToMoreMenu = true
    private val isViewAudienceToMoreMenu = true
    private var isAddedDialogTextItemList = false
    private var isUserDismissMoreMenu = false

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

        binding.closeBtn.setOnClickListener {
            activity?.finish()
        }
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

        if (stories[counter].isImage() && !onImagePrepared) {
            return
        }

        simpleExoPlayer?.seekTo(5)
        simpleExoPlayer?.playWhenReady = true

        if (counter == 0) {
            binding.storiesProgressView.startStories()
        } else {
            // restart animation
            val currentUserStoryPosition = arguments?.getInt(EXTRA_POSITION) ?: 0
            counter = StoryViewerActivity.progressState.get(currentUserStoryPosition)
            binding.storiesProgressView.startStories(counter)
        }
        binding.storiesProgressView.makeNewProgressBars(counter)
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
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "$counter: onNext stories[counter]: ${stories[counter]}")
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
        binding.root.setBackgroundColor(Color.TRANSPARENT)

        simpleExoPlayer?.stop()
        val story = stories[counter]
        if (story.isVideo()) {
            binding.storyDisplayVideo.show()
//            binding.storyDisplayImage.hide()
            binding.storyDisplayImage.visibility = View.INVISIBLE
            binding.storyDisplayText.hide()
            binding.storyDisplayVideoProgress.show()

            if (StoryViewerActivity.isMakeBackgroundColor) {
                binding.root.setBackgroundColor(Color.BLACK)
            } else if (StoryViewerActivity.isMakeBackgroundPalette) {
                Glide.with(this)
                    .load(story.storyUrl)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.storyDisplayImage.hide()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            try {
                                if (resource != null) {
                                    val pe = PaletteExtraction(
                                        binding.root,
                                        lifecycleScope,
                                        (resource as? BitmapDrawable?)?.bitmap
                                    )
                                    pe.execute()
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }

                            binding.storyDisplayImage.hide()
                            return false
                        }
                    })
                    .into(binding.storyDisplayImage)
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }

            initializePlayer()
        } else if (story.isText()) {
            binding.storyDisplayVideo.hide()
            binding.storyDisplayImage.hide()
            binding.storyDisplayText.show()
            binding.storyDisplayVideoProgress.hide()


            if (story.maxStoryTextLength > 0) {
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = InputFilter.LengthFilter(story.maxStoryTextLength)
                binding.storyDisplayText.filters = fArray
            }

            if (story.maxStoryTextLines > 0) {
                binding.storyDisplayText.maxLines = story.maxStoryTextLines
            }

            binding.storyDisplayText.text = story.storyText.trim()

            checkSizeText(activity, binding.storyDisplayText, story.maxStoryTextLength)


            val backgroundColor = try {
                if (story.storyTextBackgroundColor.trim().isNotEmpty()) {
                    Color.parseColor(story.storyTextBackgroundColor)
                } else {
                    Color.BLACK
                }
            } catch (e: Exception) {
                Color.BLACK
            }

            binding.storyDisplayText.setBackgroundColor(backgroundColor)
            binding.root.setBackgroundColor(backgroundColor)


            try {
                if (story.storyTextColor.trim().isNotEmpty())
                    binding.storyDisplayText.setTextColor(Color.parseColor(story.storyTextColor))
                else
                    binding.storyDisplayText.setTextColor(Color.WHITE)
            } catch (e: Exception) {
                binding.storyDisplayText.setTextColor(Color.WHITE)
            }

            try {
                val typeface = story.storyTextTypeface ?: getFontTypeFace(story.storyTextFont)
                if (typeface != null) {
                    binding.storyDisplayText.typeface = typeface
                }
            } catch (e: Exception) {
                // ignore
            }


        } else if (story.isImage()) {
            binding.storyDisplayVideo.hide()
            binding.storyDisplayImage.show()
            binding.storyDisplayText.hide()
            binding.storyDisplayVideoProgress.show()

//            toggleLoadMode(true)

            Glide.with(this)
                .load(story.storyUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.storyDisplayVideoProgress.hide()
//                        toggleLoadMode(false)
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
                        onImagePrepared = true
                        toggleLoadMode(false)
//                        resumeCurrentStory()

                        if (StoryViewerActivity.isMakeBackgroundColor) {
                            try {
                                binding.root.setBackgroundColor(StoryViewerActivity.backgroundColor)
                            } catch (e: Throwable) {
                                binding.root.setBackgroundColor(Color.BLACK)
                                e.printStackTrace()
                            }
                        } else if (StoryViewerActivity.isMakeBackgroundPalette) {
                            try {
                                if (resource != null) {
                                    val pe = PaletteExtraction(
                                        binding.root,
                                        lifecycleScope,
                                        (resource as? BitmapDrawable?)?.bitmap
                                    )
                                    pe.execute()
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }

                        return false
                    }
                })
                .into(binding.storyDisplayImage)
        } /*else if (story.isAudio()) {
            // ignore
        }*/

        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            timeInMillis = story.storyDate
        }
        binding.storyDisplayTime.text = DateFormat.format("MM-dd-yyyy HH:mm:ss", cal).toString()


        onStoryChangedListener?.invoke(position, counter)

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
        mediaDataSourceFactory =
            CacheDataSourceFactory(context, 100 * 1024 * 1024, 100 * 1024 * 1024)

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(Uri.parse(stories[counter].storyUrl))
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
//                Toast.makeText(activity, "onSwipeTop", Toast.LENGTH_LONG).show()
            }

            override fun onSwipeBottom() {
                activity?.finish()
//                Toast.makeText(activity, "onSwipeBottom", Toast.LENGTH_LONG).show()
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

        Glide.with(this)
            .load(storyUser.profilePicUrl)
            .circleCrop()
            .into(binding.storyDisplayProfilePicture)
        binding.storyDisplayNick.text = storyUser.username
    }

    private fun setupMoreMenu() {
        if (storyUser.isShowMoreMenu) {
            binding.moreIV.visibility = View.VISIBLE
            binding.moreIV.setOnClickListener {
                setupOnClickMoreMenu()
            }
        } else {
            binding.moreIV.visibility = View.GONE
        }
    }

    private fun setupOnClickMoreMenu() {
        val activity = activity ?: return

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
                    {
                        isUserDismissMoreMenu = true
                        val confirmationDeleteStoryDialog = DialogNormal(activity)
                        confirmationDeleteStoryDialog.setTitle(R.string.delete_this_story)
                            .setContent(activity.getString(R.string.are_you_sure_you_want_to_delete_this_story))
                            .setConfirm(
                                getString(R.string.delete),
                                {
                                    isUserDismissMoreMenu = true
                                    confirmationDeleteStoryDialog.dismiss()
                                    moreMenuDialogBottom?.dismiss()
//                                        onComplete()
                                    toggleLoadMode(isLoading = false)
                                    onClickDeleteStoryListener?.invoke(
                                        position,
                                        counter
                                    )
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
                ) { _ ->
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
        if (binding.storyOverlay.alpha != 0F) return

        binding.storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (binding.storyOverlay.alpha != 1F) return

        binding.storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        StoryViewerActivity.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        val newPosition = StoryViewerActivity.progressState.get(position)
        return newPosition
    }

    fun pauseCurrentStory() {
        if (BuildConfig.DEBUG)
            Log.e(
                TAG,
                "${counter}-pauseCurrentStory stories[counter].storyType: ${stories[counter].storyType}, onResumeCalled: $onResumeCalled, onVideoPrepared: $onVideoPrepared, onImagePrepared: $onImagePrepared"
            )

        simpleExoPlayer?.playWhenReady = false
        binding.storiesProgressView.pause()
    }

    fun resumeCurrentStory() {
        if (BuildConfig.DEBUG)
            Log.e(
                TAG,
                "${counter}-resumeCurrentStory stories[counter].storyType: ${stories[counter].storyType}, onResumeCalled: $onResumeCalled, onVideoPrepared: $onVideoPrepared, onImagePrepared: $onImagePrepared"
            )

        if (stories[counter].isVideo() && !onVideoPrepared) {
            simpleExoPlayer?.playWhenReady = false
            return
        }

        if (stories[counter].isImage() && !onImagePrepared) {
            return
        }

        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            binding.storiesProgressView.resume()
        }
    }

    private fun getFontTypeFace(textFont: StoryTextFont): Typeface? {
        val activity = activity ?: return null
        val typeface: Typeface?
        val font = when (textFont) {
            StoryTextFont.APP_ROBOTO_BOLD -> R.font.app_roboto_bold
            StoryTextFont.CAIRO_BOLD -> R.font.cairo_bold
            StoryTextFont.POPPINS_BOLD -> R.font.poppins_bold
            StoryTextFont.POPPINS_LIGHT -> R.font.poppins_light
            StoryTextFont.POPPINS_REGULAR -> R.font.poppins_regular
            StoryTextFont.POPPINS_SEMI_BOLD -> R.font.poppins_semi_bold
            StoryTextFont.ROBOTO_MEDIUM -> R.font.roboto_medium
            StoryTextFont.ROBOTO_REGULAR -> R.font.roboto_regular
            StoryTextFont.SF_PRO_DISPLAY_MEDIUM -> R.font.sf_pro_display_medium
            StoryTextFont.SOURCE_SAN_PRO_SEMIBOLD -> R.font.source_san_pro_semibold
            StoryTextFont.SOURCE_SAN_PROBOLD -> R.font.source_san_probold
            else -> R.font.app_roboto_bold
        }

        typeface = ResourcesCompat.getFont(activity, font)

        return typeface
    }

    private fun checkSizeText(
        context: Context?,
        textView: TextView?,
        maxStoryTextLength: Int,
    ) {
        if (context == null
            || textView?.text?.toString()?.trim().isNullOrEmpty()
        ) {
            return
        }
        val textStory = textView?.text?.toString()?.trim() ?: ""
        val textStoryLength = textStory.length
        val textStorySizeLarge = context.resources.getDimension(R.dimen._36sdp)
        val textStorySizeMedium = context.resources.getDimension(R.dimen._28sdp)
        val textStorySizeSmall = context.resources.getDimension(R.dimen._20sdp)
        val textStoryToSizeLarge: Float = 1.0f / 3.0f * maxStoryTextLength
        val textStoryToSizeMedium: Float = 2.0f / 3.0f * maxStoryTextLength
        if (textStoryToSizeLarge > textStoryLength) {
            textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textStorySizeLarge)
        } else if (textStoryToSizeMedium > textStoryLength) {
            textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textStorySizeMedium)
        } else {
            textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textStorySizeSmall)
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

        var onClickDeleteStoryListener: ((userPosition: Int, storyPosition: Int) -> Unit)? =
            null
        var onStoryChangedListener: ((userPosition: Int, storyPosition: Int) -> Unit)? = null

    }
}