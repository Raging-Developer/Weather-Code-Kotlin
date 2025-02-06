package com.app.new_weather

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.app.new_weather.data.ForcUIState

//I went through all this so that the screen would update before the api call.
class QueryAPI : ViewModel() {
    private val _listQueryState: MutableState<ForcUIState> = mutableStateOf(ForcUIState())
    var listQueryState: State<ForcUIState> = _listQueryState

    init {
        _listQueryState.value = loadQuery(listQueryState.value)
    }

    private fun loadQuery(listQuerySate: ForcUIState): ForcUIState {
        return listQuerySate.copy()

    }

    // that was hard work.
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val viewModel = QueryAPI()
                viewModel
            }
        }
    }

}


