package com.majd_alden.storyviewer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.majd_alden.storyviewerlibrary.screen.StoryViewerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_viewer)

        startActivity(Intent(this, StoryViewerActivity::class.java))
    }
}