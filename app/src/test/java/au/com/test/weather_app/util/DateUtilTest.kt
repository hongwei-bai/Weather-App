package au.com.test.weather_app.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import org.junit.Rule
import java.util.Date

class DateUtilTest : FunSpec() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    init {
        test("test toLocalString - given a date - get correct date string") {
            val testData = table(
                headers("date", "expectedResult"),
                row(Date(2020 - 1900, 1, 2, 22, 45, 0), "2 Feb 2020 22:45"),
                row(Date(2020 - 1900, 0, 1, 0, 1, 0), "1 Jan 2020 00:01"),
                row(Date(2019 - 1900, 11, 31, 23, 59, 59), "31 Dec 2019 23:59")
            )

            forAll(testData) { date: Date, expectedResult: String ->
                println("date: $date, expectedResult: $expectedResult")
                date.toLocalString() shouldBe expectedResult
            }
        }
    }
}