package com.buchi.cardinfofinder.utils

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException

/**
 * Created by Sunday Ndu<ndusunday@gmail.com>
 * A Reusable flow utility class to simplify and reduce rewriting of these processes for all repository.
 * functions. Different possible condition a covered
 */
class ReusableFlow<T>(private val context: Context) {

    /**
     * A Reusable flow function to simplify network checks before fetching from API
     * @param processFetchAction lambda function to process API fetching. The response from this is
     * emitted as through the flow.
     */
    fun internetCheckProcessFlow(processFetchAction: suspend () -> T): Flow<ResultState<T>> {
        return flow<ResultState<T>> {
            if (InternetReachability.isInternetAvailable(context)) {
                val apiRequest = processFetchAction()
                emit(ResultState.data(null, apiRequest))
            } else {
                emit(ResultState.error("Please check your internet connection"))
            }
        }.onStart {
            emit(ResultState.loading(true))
        }
            .catch { ex ->
                ex.printStackTrace()
                if (ex is HttpException && ex.code() == 404) {
                    emit(ResultState.error("This card does not exist. Try another card!"))
                } else {
                    emit(ResultState.error("Unable to perform operation, please try again!"))
                }
            }
    }

    /**
     * Perform a customize api to cache synching after emitting a non empty cache to the viewModel.
     * @param cacheSynchProcess is the interface to perform cache checks for data before doing a
     * network request that updates the db.
     */
    fun updateCacheBeforeSync(
        cacheSynchProcess: CacheSynchProcess<T>
    ): Flow<ResultState<T>> {
        return flow<ResultState<T>> {
            emit(ResultState.data(null, cacheSynchProcess.checkCache()))
            if (InternetReachability.isInternetAvailable(context)) {
                cacheSynchProcess.synchFromNetwork()
//                emit(ResultState.data(null, apiRequest))
                // Rerun a check on the cached data to make sure there's one source of data going to
                // viewModel
                emit(ResultState.data("List was synched", cacheSynchProcess.checkCache()))
            } else {
                emit(ResultState.error("Please check your internet connection"))
            }
        }.onStart {
            emit(ResultState.loading(true))
        }
            .catch { ex ->
                ex.printStackTrace()
                if (ex is HttpException && ex.code() == 404) {
                    emit(ResultState.error("This card does not exist. Try another card!"))
                } else {
                    emit(ResultState.error("Unable to perform operation, please try again!"))
                }
//                emit(ResultState.error(ex.message ?: ""))
            }
    }

    /**
     * Only updates the db once there is no current cache data.
     * @param cacheSynchProcess is the interface to perform cache checks for data before doing a
     * network request that updates the db.
     */

    /*fun synchCacheOnce(
        cacheSynchProcess: CacheSynchProcess<T>
    ): Flow<ResultState<T>> {
        return flow<ResultState<T>> {
            emit(ResultState.data(null, cacheSynchProcess.checkCache()))
            if (InternetReachability.isInternetAvailable(context)) {
                cacheSynchProcess.synchFromNetwork()
//                emit(ResultState.data(null, apiRequest))
                // Rerun a check on the cached data to make sure there's one source of data going to
                // viewModel
                emit(ResultState.data(null, cacheSynchProcess.checkCache()))
            } else {
                emit(ResultState.error("Please check your internet connection"))
            }
        }.onStart {
            emit(ResultState.loading(true))
        }
            .catch { ex ->
                emit(ResultState.error(ex.message ?: ""))
            }
    }*/

    /**
     * Process cache and network synching from the consuming class.
     * @param T usually the viewState to return from each of the processes. While implementing this
     * interface, T must be returned from every process.
     */
    interface CacheSynchProcess<T> {
        fun checkCache(): T
        fun synchFromNetwork(): T
    }
}