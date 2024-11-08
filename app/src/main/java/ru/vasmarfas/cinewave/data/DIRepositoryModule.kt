package ru.vasmarfas.cinewave.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.vasmarfas.cinewave.domain.MainRepositoryInterface

@Module
@InstallIn(ViewModelComponent::class)
abstract class  DIRepositoryModule {
    @Binds
    abstract fun injectInterface(mainInterface : MainRepository) : MainRepositoryInterface
}