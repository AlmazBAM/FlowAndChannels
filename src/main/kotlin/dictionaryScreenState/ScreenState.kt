package dictionaryScreenState

sealed interface ScreenState {
    data object Initial: ScreenState
    data object Loading: ScreenState
    data class Success(val word: String, val definitions: List<String>): ScreenState
    data object Error: ScreenState
}