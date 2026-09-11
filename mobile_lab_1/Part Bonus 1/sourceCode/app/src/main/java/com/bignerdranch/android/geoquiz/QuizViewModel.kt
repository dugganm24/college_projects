package com.bignerdranch.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"


class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    //Question Bank for App
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    // Store the first visible item position (RecyclerView scroll position)
    private val _scrollPosition = MutableLiveData(0)
    val scrollPosition: LiveData<Int> = _scrollPosition

    // Public getter for the entire question list (used by the adapter)
    val questions: List<Question>
        get() = questionBank

    // Track which question is selected in the list
    private val _currentIndex = MutableLiveData(savedStateHandle.get<Int>(CURRENT_INDEX_KEY) ?: 0)
    val currentIndex: LiveData<Int> = _currentIndex

    // List to track if the user cheated on each question
    private val cheatedQuestions = MutableList(questionBank.size) { false }

    //Check if the current question has been cheated on
    val currentQuestionCheated: Boolean
        get() = cheatedQuestions[_currentIndex.value ?: 0]

    // Current question answer (based on currentIndex)
    val currentQuestionAnswer: Boolean
        get() = questionBank[_currentIndex.value ?: 0].answer

    // Get the question text resource ID for the current question
    val currentQuestionText: Int
        get() = questionBank[_currentIndex.value ?: 0].textResId


    // Called when a list item is clicked
    fun selectQuestion(index: Int) {
        _currentIndex.value = index
        savedStateHandle[CURRENT_INDEX_KEY] = index
    }

    //Mark the current question as cheated
    fun markCurrentQuestionAsCheated() {
        _currentIndex.value?.let { index ->
            cheatedQuestions[index] = true
        }
    }

    fun setScrollPosition(position: Int) {
        _scrollPosition.value = position
    }
}

