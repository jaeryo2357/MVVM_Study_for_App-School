package com.mashup.mvvm.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mashup.mvvm.ServiceLocator
import com.mashup.mvvm.data.repository.GithubRepository
import com.mashup.mvvm.databinding.ActivityMainBinding
import com.mashup.mvvm.extensions.showToast
import com.mashup.mvvm.ui.main.adapter.RepositoryAdapter

class MainActivity : AppCompatActivity() {
    //lateinit var viewBinding: ActivityMainBinding
    private val viewBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val repositoryAdapter: RepositoryAdapter by lazy { RepositoryAdapter() }
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("지연초기화", "onCreate")

        setContentView(viewBinding.root)

//        viewBinding = ActivityMainBinding.inflate(
//            LayoutInflater.from(this)
//        )

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(GithubRepository(ServiceLocator.injectGithubApi()))
        ).get(MainViewModel::class.java)

        setUi()
        observeViewModel()
    }

    private fun setUi() {
        setUiOfEditText()
        setUiOfSearchButton()
        setUiOfRepositoryRecyclerView()
    }

    private fun observeViewModel() {
        viewModel.profiles.observe(this@MainActivity) { profiles ->
            // 데이터가 왔을 때 어떻게 표현할 지 -> 뷰의 로직
            viewBinding.rvRepository.post {
                repositoryAdapter.submitList(profiles)
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }
    }

    private fun setUiOfEditText() {
        viewBinding.etSearchRepository.doOnTextChanged { text, _, _, _ ->
            viewBinding.btnSearch.isEnabled = text?.isNotBlank() ?: false
        }
    }

    private fun setUiOfSearchButton() {
        viewBinding.btnSearch.setOnClickListener {
            // 뷰모델에게 값을 가져와라 액션만 전달.
            // 뷰의 비즈니스 로직을 제거.
            viewModel.getGithubProfile(viewBinding.etSearchRepository.text.toString())
        }
    }

    private fun setUiOfRepositoryRecyclerView() {
        viewBinding.rvRepository.apply {
            setHasFixedSize(true)
            adapter = repositoryAdapter
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}


// Fcatory는 ViewModel 객체를 실제로 만드는 곳.
class MainViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(repository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}