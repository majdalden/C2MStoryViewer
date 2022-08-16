package com.majd_alden.storyviewerlibrary.customview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.majd_alden.storyviewerlibrary.data.StoryUser
import com.majd_alden.storyviewerlibrary.screen.StoryViewerFragment

class StoryPager2Adapter constructor(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val storyList: MutableList<StoryUser>,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val storyFragmentList = mutableListOf<StoryViewerFragment>()

    fun findFragmentByPosition(position: Int): Fragment? {
        if (position < 0 || position >= storyFragmentList.size) {
            return null
        }
        return storyFragmentList[position]
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment =
            StoryViewerFragment.newInstance(position = position, story = storyList[position])

        storyFragmentList.add(fragment)
        return fragment
    }


}