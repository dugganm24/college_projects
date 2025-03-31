package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bignerdranch.android.geoquiz.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel
    private val quizViewModel: QuizViewModel by activityViewModels()

    // Activity result launcher for CheatActivity
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.markCurrentQuestionAsCheated()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateQuestion()

        // Handle True button click
        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        // Handle False button click
        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }

        //Updated so it takes the user back to question list
        binding.nextButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //new location for cheat button
        binding.cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(requireContext(), answerIsTrue)
            cheatLauncher.launch(intent)
        }
    }

    //check answer and show correct toast message based on answer
    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT).show()
    }

    //Set Question text using ViewModel
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
