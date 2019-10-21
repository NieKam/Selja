package io.selja.ui.details

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import io.selja.base.DeviceId
import io.selja.model.AdItem
import io.selja.model.getPriceFormatted
import io.selja.model.imageUrlWithHost
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
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class AdItemDetailsViewModelTest {

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

    private lateinit var viewModel: AdItemDetailsViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = AdItemDetailsViewModel(dataModel, locationRepository, deviceId)
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
    fun testGetDetailedView() {
        assertTrue(viewModel.detailedView)
    }

    @Test
    fun testGetPrice() {
        val adItemMock = mock(AdItem::class.java)
        whenever(adItemMock.currency).thenReturn("zl")
        whenever(adItemMock.price).thenReturn(199.0)

        viewModel.oAdItem.set(adItemMock)
        assertEquals(adItemMock.getPriceFormatted(), viewModel.getPrice())
    }

    @Test
    fun testGetImageUrl() {
        val adItemMock = mock(AdItem::class.java)
        whenever(adItemMock.imageUrl).thenReturn("url")
        viewModel.oAdItem.set(adItemMock)

        assertEquals(adItemMock.imageUrlWithHost(), viewModel.getImageUrl())
    }

    @Test
    fun testIsMine() {
        val id = "123"
        whenever(deviceId.getDeviceId()).thenReturn(id)
        val adItemMock = mock(AdItem::class.java)
        whenever(adItemMock.deviceId).thenReturn(id)
        viewModel.oAdItem.set(adItemMock)

        assertTrue(viewModel.isMine())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testRefresh() = runBlockingTest {
        val adItemMock = mock(AdItem::class.java)
        whenever(adItemMock.id).thenReturn(1)
        whenever(adItemMock.phoneObfuscated).thenReturn("***")
        whenever(dataModel.getOne(anyOrNull(), anyOrNull())).thenReturn(adItemMock)

        viewModel.oAdItem.set(adItemMock)

        viewModel.refresh()

        verify(dataModel).getOne(1, null)
    }

    @Test
    fun testInit() {
        viewModel.initPermissionState(true)
        assertTrue(viewModel.hasLocationPermission.get())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `onAttached when last location is known`() = runBlockingTest {
        val location = mock(Location::class.java)
        val adItemMock = mock(AdItem::class.java)

        whenever(locationRepository.getLastKnownLocation()).thenReturn(location)
        whenever(dataModel.getOne(anyOrNull(), anyOrNull())).thenReturn(adItemMock)
        whenever(adItemMock.id).thenReturn(1)

        viewModel.oAdItem.set(adItemMock)
        viewModel.onAttached()

        verify(locationRepository, times(0)).startLocationUpdates()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `onAttached when last location is unknown`() = runBlockingTest {
        val adItemMock = mock(AdItem::class.java)
        whenever(adItemMock.id).thenReturn(1)
        whenever(locationRepository.getLastKnownLocation()).thenReturn(null)
        whenever(dataModel.getOne(anyOrNull(), anyOrNull())).thenReturn(adItemMock)

        viewModel.initPermissionState(true)
        viewModel.oAdItem.set(adItemMock)
        viewModel.onAttached()

        verify(locationRepository).startLocationUpdates()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `onAttached when permission is not granted`() = runBlockingTest {
        val adItemMock = mock(AdItem::class.java)
        whenever(adItemMock.id).thenReturn(1)

        whenever(dataModel.getOne(anyOrNull(), anyOrNull())).thenReturn(adItemMock)

        viewModel.initPermissionState(false)
        viewModel.oAdItem.set(adItemMock)
        viewModel.onAttached()

        verify(dataModel).getOne(eq(1), isNull())
    }

    @Test
    fun testOnDetached() {
        viewModel.onDetached()
        verify(locationRepository).stopLocationUpdates()
    }
}