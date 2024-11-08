package ru.vasmarfas.cinewave.ui.serviceItems

sealed class State {
    object Loading: State()
    object Success: State()
    object NoTextEnough: State()
}

