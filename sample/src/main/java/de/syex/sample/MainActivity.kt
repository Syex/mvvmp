package de.syex.sample

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import de.syex.rode.RodePresenterProvider
import de.syex.rode.RodeViewModel
import de.tomseifert.sample.HelloWordView
import de.tomseifert.sample.HelloWorldPresenter

class MainActivity : AppCompatActivity(),
        HelloWordView, RodePresenterProvider<HelloWorldPresenter, HelloWordView> {

    // The presenter is available after the [onCreate] method.
    private lateinit var presenter: HelloWorldPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This is the earliest point we can retrieve a ViewModel for this activity and therefore the earliest
        // point we can get our presenter
        presenter = ViewModelProviders.of(this)
                .get(RodeViewModel::class.java)
                .providePresenter(this)
    }

    // Called one time to create a single surviving instance of HelloWorldPresenter
    override fun createPresenter(): HelloWorldPresenter {
        return HelloWorldPresenter("sample text")
    }

    override fun showHelloWord(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
