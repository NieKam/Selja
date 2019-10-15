package io.selja.ui.newITem

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.selja.base.DeviceId
import io.selja.model.AdItem
import io.selja.repository.AdItemsDataModel
import io.selja.repository.LocationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class NewItemViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testScope = TestCoroutineScope()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var dataModel: AdItemsDataModel

    @Mock
    private lateinit var locationRepository: LocationRepository

    @Mock
    private lateinit var deviceId: DeviceId

    private lateinit var viewModel: NewItemViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = NewItemViewModel(dataModel, locationRepository, deviceId)
        viewModel.coroutineScope = testScope
        viewModel.backgroundDispatcher = testDispatcher
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        testScope.cleanupTestCoroutines()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `onAttached with known location`() {
        val location = mock(Location::class.java)
        whenever(locationRepository.getLastKnownLocation()).thenReturn(location)
        viewModel.onAttached()

        verify(locationRepository).getLastKnownLocation()
        assertTrue(viewModel.location.get()!!.isNotEmpty())
        assertEquals(location, viewModel.lastLocation)
    }

    @Test
    fun `onAttached with unknown location`() {
        whenever(locationRepository.getLastKnownLocation()).thenReturn(null)
        viewModel.onAttached()

        verify(locationRepository).getLastKnownLocation()
        verify(locationRepository).startLocationUpdates()
    }

    @Test
    fun testOnDetached() {
        viewModel.onDetached()
        verify(locationRepository).stopLocationUpdates()
    }

    @Test
    fun testOnPhotoFileCreated() {
        val uri = "uri"
        val path = "path"
        viewModel.onPhotoFileCreated(uri, path)

        assertEquals(uri, viewModel.photoFileUri.get())
        assertEquals(path, viewModel.absolutePath)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test save without photo`() = runBlockingTest {
        val location = mock(Location::class.java)
        val adItem = mock(AdItem::class.java)

        whenever(location.latitude).thenReturn(50.0)
        whenever(location.longitude).thenReturn(10.0)

        whenever(dataModel.createNew(any())).thenReturn(adItem)
        whenever(deviceId.getDeviceId()).thenReturn("1234")

        viewModel.lastLocation = location

        viewModel.save("name", "desc", "123456", 1.0, 1)

        verify(dataModel).createNew(any())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test save with photo`() = runBlockingTest {
        val location = mock(Location::class.java)
        val adItem = mock(AdItem::class.java)
        val id = 1L
        val path = "some path"

        whenever(adItem.id).thenReturn(id)

        whenever(location.latitude).thenReturn(50.0)
        whenever(location.longitude).thenReturn(10.0)

        whenever(dataModel.createNew(any())).thenReturn(adItem)
        whenever(deviceId.getDeviceId()).thenReturn("1234")

        viewModel.lastLocation = location
        viewModel.absolutePath = path

        viewModel.save("name", "desc", "123456", 1.0, 1)

        verify(dataModel).createNew(any())
        verify(dataModel).uploadFile(eq(id), eq(path))
    }
}