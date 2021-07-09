package com.example.softwareprojectapp.di.modules

import com.example.softwareprojectapp.firebase_repo.FirebaseRepo
import com.example.softwareprojectapp.viewmodels.ViewModelLogIn
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class DependenciesViewModelModule {

    @Provides
    fun provideFirebaseRepo(): FirebaseRepo = FirebaseRepo()

}