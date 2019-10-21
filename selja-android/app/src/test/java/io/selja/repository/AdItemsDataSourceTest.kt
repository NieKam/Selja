package io.selja.repository

import android.location.Location
import com.nhaarman.mockitokotlin2.*
import io.selja.api.SeljaApi
import io.selja.model.NewAdItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.io.File


@RunWith(JUnit4::class)
class AdItemsDataSourceTest {

    @Mock
    private lateinit var mockApi: SeljaApi

    private lateinit var dataModel: AdItemsDataModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dataModel = AdItemsDataSource(mockApi)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `get all with null location should call getAllItems`() = runBlockingTest {
        val deferred = async {
            dataModel.getAll(null)
        }

        deferred.await()
        verify(mockApi).getAllItems()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `get all with not null location should call getAllItemsWithLocation`() = runBlockingTest {
        val lat = 50.0
        val long = 10.0
        val mockLocation = mock(Location::class.java)
        whenever(mockLocation.latitude).thenReturn(lat)
        whenever(mockLocation.longitude).thenReturn(long)

        val deferred = async {
            dataModel.getAll(mockLocation)
        }

        deferred.await()
        verify(mockApi).getAllItemsWithLocation(lat = lat.toString(), lon = long.toString())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test creating new ad without photo`() = runBlockingTest {
        val newAdItem = mock(NewAdItem::class.java)
        val deferred = async {
            dataModel.createNew(newAdItem)
        }

        deferred.await()
        verify(mockApi).createNewAd(newAdItem, null)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun `test creating new ad with photo`() = runBlockingTest {
        val newAdItem = mock(NewAdItem::class.java)
        val deferred = async {
            dataModel.createNew(newAdItem,  "path")
        }

        deferred.await()
        verify(mockApi).createNewAd(eq(newAdItem), notNull())
    }
}
