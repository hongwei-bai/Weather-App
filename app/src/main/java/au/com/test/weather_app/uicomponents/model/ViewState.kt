package au.com.test.weather_app.uicomponents.model

sealed class ViewState

object Default : ViewState()

object Loading : ViewState()

class Error(val exception: Throwable) : ViewState()

class Success<T>(val data: T) : ViewState()

object Completed : ViewState()
