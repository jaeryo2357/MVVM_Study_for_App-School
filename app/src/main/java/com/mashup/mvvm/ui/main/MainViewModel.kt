package com.mashup.mvvm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mashup.mvvm.data.Response
import com.mashup.mvvm.data.model.Repository
import com.mashup.mvvm.data.repository.GithubRepository

// 비즈니스 로직을 담당
// 1) Github API에서 프로필 리스트를 가져오고 UI에 뿌려준다.
class MainViewModel(
    private val githubRepository: GithubRepository
) : ViewModel() {

    // observeField
    // 프로필 리스트를 가지고 있는 필드
    // ObservableField, LiveData, Flow -> LiveData
    private val _profiles = MutableLiveData<List<Repository>>()
    val profiles: LiveData<List<Repository>> = _profiles

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getGithubProfile(query: String) {
        // HttpURLConnection()
        // Retrofit은 네트워크 통신 라이브러리
        // 에러 처리
        // TimeOut 설정
        // retry 네트워크 실패했을 때,
        // Json -> Json을 DTO로 변환하거나
        // 네트워크 데이터를 캐싱하거나...
        Thread {
            when (val data = githubRepository.getRepositories(query)) {
                is Response.Success -> {
                    _profiles.postValue(data.data.items)
                }

                is Response.Error -> {
                    data.message?.run {
                        _errorMessage.postValue(this)
                    }
                }

                else -> {
                }
            }
        }.start()
    }
}