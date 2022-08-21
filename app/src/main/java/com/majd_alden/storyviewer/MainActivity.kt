package com.majd_alden.storyviewer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.majd_alden.storyviewerlibrary.screen.StoryViewerActivity
import com.majd_alden.storyviewerlibrary.screen.StoryViewerFragment
import com.majd_alden.storyviewerlibrary.utils.StoryGenerator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayStoryBtn.setOnClickListener {
//            startActivity(Intent(this, StoryViewerActivity::class.java))
//            startActivity(StoryViewerActivity.newInstance(this@MainActivity, mutableListOf()))
//            StoryViewerActivity.isMakeBackgroundPalette = true
//            StoryViewerActivity.isMakeBackgroundColor = true
            startActivity(
                StoryViewerActivity.newInstance(
                    this@MainActivity,
                    StoryGenerator.generateStories(),
//                    2,
//                    0
                )
            )
            StoryViewerFragment.onClickDeleteStoryListener = { userPosition, storyPosition ->
//                Toast.makeText(this@MainActivity, "userPosition: $userPosition, storyPosition: $storyPosition", Toast.LENGTH_SHORT).show()
                if (BuildConfig.DEBUG)
                    Log.e(
                        "MainActivity",
                        "onCreate onClickDeleteStoryListener userPosition: $userPosition, storyPosition: $storyPosition"
                    )

                startActivity(
                    StoryViewerActivity.newInstance(
                        this@MainActivity,
                        StoryGenerator.generateStories(),
                    )
                )
            }

            StoryViewerFragment.onStoryChangedListener = { userPosition, storyPosition ->
//                Toast.makeText(this@MainActivity, "userPosition: $userPosition, storyPosition: $storyPosition", Toast.LENGTH_SHORT).show()
                if (BuildConfig.DEBUG)
                    Log.e(
                        "MainActivity",
                        "onCreate onStoryChangedListener userPosition: $userPosition, storyPosition: $storyPosition"
                    )
            }
        }
    }
}