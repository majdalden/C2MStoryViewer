package com.majd_alden.storyviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.majd_alden.storyviewerlibrary.screen.StoryViewerActivity
import com.majd_alden.storyviewerlibrary.utils.StoryGenerator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayStoryBtn.setOnClickListener {
//            startActivity(Intent(this, StoryViewerActivity::class.java))
//            startActivity(StoryViewerActivity.newInstance(this@MainActivity, mutableListOf()))
            startActivity(
                StoryViewerActivity.newInstance(
                    this@MainActivity,
                    StoryGenerator.generateStories()
                )
            )
        }
    }
}