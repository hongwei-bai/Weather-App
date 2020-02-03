package au.com.test.weather_app.test

import au.com.test.weather_app.util.CoroutineContextProvider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestContextProvider : CoroutineContextProvider() {
    override val Main: CoroutineContext = Dispatchers.Unconfined
    override val IO: CoroutineContext = Dispatchers.Unconfined
    override val Default: CoroutineContext = Dispatchers.Unconfined
}