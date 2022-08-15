package com.majd_alden.storyviewer

import android.os.Bundle
import android.widget.Toast
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
            startActivity(
                StoryViewerActivity.newInstance(
                    this@MainActivity,
                    StoryGenerator.generateStories(),
                    2,
                    0
                )
            )
            StoryViewerFragment.onClickDeleteStoryListener = {
                Toast.makeText(this@MainActivity, "it: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }
}