package au.com.test.weather_app.home.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import au.com.test.weather_app.R
import kotlinx.android.synthetic.main.layout_searchbar.view.*


class LocationSearchToolBar : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        initialize(context)
    }

    var title: String = ""
        set(value) {
            field = value
            updateTitle()
        }

    private var onGpsButtonClickListener: (() -> Unit)? = null

    private var onSearchButtonClickListener: ((String) -> Unit)? = null

    fun setOnGpsButtonClick(listener: (() -> Unit)) {
        this.onGpsButtonClickListener = listener
    }

    fun setOnSearchButtonClick(listener: ((String) -> Unit)) {
        this.onSearchButtonClickListener = listener
    }

    private fun initialize(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_searchbar, this)

        updateTitle()
        registerGpsButtonClickListener()
        registerSearchButtonClickListener()
    }

    private fun registerGpsButtonClickListener() {
        imgBtnGps.setOnClickListener {
            onGpsButtonClickListener?.invoke()
        }
    }

    private fun registerSearchButtonClickListener() {
        imgBtnSearch.setOnClickListener {
            onSearchButtonClickListener?.invoke(editTxtSearh.text.toString())
        }
        editTxtSearh.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) { //do what you want on the press of 'done'
                onSearchButtonClickListener?.invoke(editTxtSearh.text.toString())
            }
            false
        }
    }

    private fun updateTitle() {
        editTxtSearh?.setText(title)
    }
}