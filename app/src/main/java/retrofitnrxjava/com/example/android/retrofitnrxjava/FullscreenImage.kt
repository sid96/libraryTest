package retrofitnrxjava.com.example.android.retrofitnrxjava

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide

class FullscreenImage : AppCompatActivity() {

    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        val intent = intent
        val url = intent.extras!!.getString("url")

        imageView = findViewById<View>(R.id.fullImage) as ImageView?

        Glide.with(this).load(url).into(imageView!!)

    }
}
