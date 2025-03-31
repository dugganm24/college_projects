package com.bignerdranch.android.geoquiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.geoquiz.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel used across fragments
    private val quizViewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //inflate the layout using view binding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set up RecyclerView
        binding.questionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = QuestionAdapter(quizViewModel.questions) { position ->
            // Update selected question in ViewModel
            quizViewModel.selectQuestion(position)

            // Navigate to DetailFragment
            findNavController().navigate(R.id.action_listFragment_to_detailFragment)
        }
        //assign adapter to recycler view
        binding.questionRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
