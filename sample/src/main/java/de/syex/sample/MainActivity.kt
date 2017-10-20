package de.syex.sample

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import de.syex.rode.RodePresenterProvider
import de.syex.rode.RodeViewModel

class MainActivity : AppCompatActivity(),
        HelloWordView, RodePresenterProvider<HelloWorldPresenter, HelloWordView> {

    // The presenter is available after the [onCreate] method.
    private lateinit var presenter: HelloWorldPresenter

    private val helloWorldTextView by lazy { findViewById<TextView>(R.id.tv_helloWorld) }
    private val firstTextInputLayout by lazy { findViewById<TextInputLayout>(R.id.til_first_hint) }
    private val secondTextInputLayout by lazy { findViewById<TextInputLayout>(R.id.til_second_hint) }

    // Called one time to create a single surviving instance of HelloWorldPresenter
    override fun createPresenter(): HelloWorldPresenter {
        return HelloWorldPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This is the earliest point we can retrieve a ViewModel for this activity and therefore the earliest
        // point we can get our presenter
        presenter = ViewModelProviders.of(this)
                .get(RodeViewModel::class.java)
                .providePresenter(this)
    }

    fun onClickToggleFirst(v: View) {
        presenter.onClickToggleFirst()
    }

    fun onClickToggleSecond(v: View) {
        presenter.onClickToggleSecond()
    }

    override fun setHelloWorldText(text: String) {
        helloWorldTextView.text = text
    }

    override fun showFirstError() {
        firstTextInputLayout.error = "Random error"
    }

    override fun clearFirstError() {
        firstTextInputLayout.error = null
    }

    override fun showSecondError() {
        secondTextInputLayout.error = "Specific error"
    }

    override fun clearSecondError() {
        secondTextInputLayout.error = null
    }

}
