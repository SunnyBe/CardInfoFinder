package com.buchi.cardinfofinder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.buchi.cardinfofinder.data.model.Card
import com.buchi.cardinfofinder.data.repository.MainRepository
import com.buchi.cardinfofinder.presentation.MainViewModel
import com.buchi.cardinfofinder.presentation.MainViewState
import com.buchi.cardinfofinder.utils.MainCoroutineScopeRule
import com.buchi.cardinfofinder.utils.ResultState
import com.buchi.cardinfofinder.utils.getValueForTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*

@ExperimentalCoroutinesApi
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Mock
    lateinit var mainRepo: MainRepository

    @Captor
    private lateinit var captor: ArgumentCaptor<MainViewState>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(mainRepo)
    }

    @Test
    fun fetchCard_SuccessUpdatesViewStateAndDataState() {
        coroutineScope.runBlockingTest {
            val testFlow = flow {
                emit(ResultState.data(null, MainViewState(cardDetail = Card.testCard("012345678912345"))))
            }
            mainViewModel.fetchCardDetails(("012345678912345"))
            Mockito.`when`(mainRepo.fetchCard("012345678912345")).thenReturn(testFlow)

            val expectedType = "debit"
            Assert.assertEquals(expectedType, mainViewModel.dataState.getValueForTest()?.data?.getContentIfNotHandled()?.cardDetail?.type)
        }
    }

    @Test
    fun fetchCard_ErrorUpdatesViewStateAndDataState() {
        coroutineScope.runBlockingTest {
            val testFlow = flow {
                emit(ResultState.error<MainViewState>("Test Error"))
            }
            mainViewModel.fetchCardDetails("012345678912345")
            Mockito.`when`(mainRepo.fetchCard("012345678912345")).thenReturn(testFlow)

            val expectedType = "Test Error"
            Assert.assertEquals(expectedType, mainViewModel.dataState.getValueForTest()?.message?.getContentIfNotHandled())
        }
    }

    @Test
    fun fetchCard_LoadingUpdatesViewStateAndDataState() {
        coroutineScope.runBlockingTest {
            val testFlow = flow {
                emit(ResultState.loading<MainViewState>(true))
            }
            mainViewModel.fetchCardDetails("012345678912345")
            Mockito.`when`(mainRepo.fetchCard("012345678912345")).thenReturn(testFlow)

            val expectedType = true
            Assert.assertEquals(expectedType, mainViewModel.dataState.getValueForTest()?.loading)
        }
    }
}