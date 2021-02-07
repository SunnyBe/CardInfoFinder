package com.buchi.cardinfofinder.presentation

import androidx.lifecycle.*
import com.buchi.cardinfofinder.data.repository.MainRepository
import com.buchi.cardinfofinder.utils.ResultState
import com.buchi.listdetail.utils.SingleLiveEvent
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _stateEvent: SingleLiveEvent<MainStateEvent> = SingleLiveEvent()

    val _dataState: MutableLiveData<ResultState<MainViewState>> = MutableLiveData()
    val dataState: LiveData<ResultState<MainViewState>> = Transformations
        .switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                processEvents(stateEvent)
                    // Make use of the built-in cancellation for flow while switching to liveData
                    .asLiveData(viewModelScope.coroutineContext)
            }
        }

    private val _viewState: SingleLiveEvent<MainViewState> = SingleLiveEvent()
    val viewState: LiveData<MainViewState> get() = _viewState

    init {
        // Init event state as Idle state
        _stateEvent.value = MainStateEvent.Idle()
    }


    private fun processEvents(stateEvent: MainStateEvent): Flow<ResultState<MainViewState>> {
        return when (stateEvent) {
            is MainStateEvent.FetchCard -> {
                mainRepository.fetchCard(stateEvent.cardNumber)
            }

            is MainStateEvent.Idle -> {
                flow { emit(ResultState.data(null, MainViewState())) }
            }
        }
    }

    fun setViewState(viewState: MainViewState) {
        _viewState.value = viewState
    }

    fun fetchCardDetails(cardNumber: String?) {
        _stateEvent.value = MainStateEvent.FetchCard(cardNumber)
    }

}