package com.arthur.examples.shorturl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthur.examples.shorturl.data.DataResult
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import com.arthur.examples.shorturl.data.remote.models.Alias
import com.arthur.examples.shorturl.data.repository.AliasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShortUrlViewModel @Inject constructor(private val aliasRepository: AliasRepository) :
    ViewModel() {

    private val _storedAlias: Flow<List<AliasLocal>> = aliasRepository.getStoredAlias()
    val storedAlias: StateFlow<List<AliasLocal>> = _storedAlias.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        listOf()
    )

    private val _shortenResult: MutableStateFlow<DataResult<Alias>?> = MutableStateFlow(null)
    val shortenResult: StateFlow<DataResult<Alias>?> = _shortenResult

    fun shortUrl(url: String) = viewModelScope.launch {
        _shortenResult.emit(DataResult.Loading())
        _shortenResult.emit(aliasRepository.shortUrl(url))
    }

}