package com.example.softwareprojectapp.di.modules

import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class DependenciesViewModelModule {

    @Provides
    @ActivityRetainedScoped
    fun provideFirebaseRepo(): FirebaseRepo = FirebaseRepo()

}