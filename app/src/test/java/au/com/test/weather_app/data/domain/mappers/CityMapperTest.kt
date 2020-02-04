package au.com.test.weather_app.data.domain.mappers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import au.com.test.weather_app.data.domain.entities.CityData
import au.com.test.weather_app.data.source.local.owm.models.City
import au.com.test.weather_app.data.source.remote.owm.models.Coordinate
import io.kotlintest.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

class CityMapperTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `on mapToDomainEntities() - standard api response - convert to domain entity correctly`() {
        // Given
        val cityInJson = City(id = 2147714L, name = "Sydney", country = "AU", coord = Coordinate(151.21, -33.87))

        // When
        val result: CityData? = CityMapper.mapToDomainEntities(cityInJson)

        // Then
        assert(result is CityData)
        assert(result != null)
        result?.id shouldBe 0
        result?.name shouldBe cityInJson.name
        result?.countryCode shouldBe cityInJson.country
        result?.latitude shouldBe cityInJson.coord.lat
        result?.longitude shouldBe cityInJson.coord.lon
        result?.owmCityId shouldBe cityInJson.id
        result?.searchCount shouldBe 0
        result?.lastSearch shouldBe 0
    }
}