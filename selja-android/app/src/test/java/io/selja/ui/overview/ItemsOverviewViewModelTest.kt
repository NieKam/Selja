package io.selja.ui.overview

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.selja.model.AdItem
import io.selja.repository.AdItemsDataModel
import io.selja.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class ItemsOverviewViewModelTest {

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

    private lateinit var viewModel: ItemsOverviewViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)

        viewModel = ItemsOverviewViewModel(dataModel, locationRepository)
        viewModel.coroutineScope = testScope
        viewModel.backgroundDispatcher = testDispatcher
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testOnDetached() {
        viewModel.onDetached()
        verify(locationRepository).stopLocationUpdates()
    }

    @Test
    fun `when permission granted should start location updates`() {
        viewModel.onRequestPermissionsResult(true)
        verify(locationRepository).startLocationUpdates()
    }

    @Test
    fun `when permission not granted should not start location updates`() {
        viewModel.onRequestPermissionsResult(false)
        verify(locationRepository, times(0)).startLocationUpdates()
    }

    @Test
    fun testAddNewItem() {
        viewModel.adItems.value = listOf()
        val adItem = mock(AdItem::class.java)
        viewModel.addNewItem(adItem)

        assertFalse(viewModel.showEmptyView.get())
        assertEquals(adItem, viewModel.adItems.value?.first())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `init without location permission`() = runBlockingTest {
        val adItem = mock(AdItem::class.java)
        whenever(dataModel.getAll(anyOrNull())).thenReturn(listOf(adItem))

        viewModel.initPermissionState(false)

        assertFalse(viewModel.hasLocationPermission.get())
        verify(dataModel).getAll(null)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `init with location permission`() = runBlockingTest {
        val adItem = mock(AdItem::class.java)
        val location = mock(Location::class.java)
        whenever(locationRepository.getLastKnownLocation()).thenReturn(location)
        whenever(dataModel.getAll(anyOrNull())).thenReturn(listOf(adItem))

        viewModel.initPermissionState(true)

        assertTrue(viewModel.hasLocationPermission.get())
        verify(dataModel).getAll(location)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `init with location permission and without last location`() = runBlockingTest {
        val adItem = mock(AdItem::class.java)
        whenever(locationRepository.getLastKnownLocation()).thenReturn(null)
        whenever(dataModel.getAll(anyOrNull())).thenReturn(listOf(adItem))

        viewModel.initPermissionState(true)

        assertTrue(viewModel.hasLocationPermission.get())
        verify(locationRepository).startLocationUpdates()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testRefresh() = runBlockingTest {
        val adItem = mock(AdItem::class.java)
        whenever(dataModel.getAll(anyOrNull())).thenReturn(listOf(adItem))

        viewModel.refresh()

        verify(dataModel).getAll(null)
        assertFalse(viewModel.showEmptyView.get())
    }
}