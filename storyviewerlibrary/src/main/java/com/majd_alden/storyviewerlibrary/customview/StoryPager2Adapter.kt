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
    private val storyList: MutableList<StoryUser>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    /*override fun getItem(position: Int): Fragment =
        StoryViewerFragment.newInstance(position, storyList[position])

    override fun getCount(): Int {
        return storyList.size
    }

    fun findFragmentByPosition(viewPager: ViewPager, position: Int): Fragment? {
        try {
            val f = instantiateItem(viewPager, position)
            return f as? Fragment
        } finally {
            finishUpdate(viewPager)
        }
    }*/
    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewerFragment.newInstance(position, storyList[position])
    }
}