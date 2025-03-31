package com.bignerdranch.android.geoquiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//RecyclerView adapter for the list of questions
class QuestionAdapter(
    private val questions: List<Question>, //List of Question to Display
    private val onClick: (Int) -> Unit  // Lambda function to handle item click
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    //ViewHolder represents each item in the RecyclerView
    class QuestionViewHolder(view: View, val onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val questionText: TextView = view.findViewById(R.id.question_text)
        val context = itemView.context

        fun bind(question: Question, position: Int) {
            //Used to Display as Question 1,2,... on list screen
            questionText.text = context.getString(R.string.question_number, position + 1)
            //use placeholder for question in list view

            //handle click event
            itemView.setOnClickListener {
                onClick(position) // Notify the fragment which item was clicked
            }
        }
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_question, parent, false)
        return QuestionViewHolder(view, onClick)
    }

    // Called to bind data to the ViewHolder
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    // Returns the total number of items
    override fun getItemCount(): Int = questions.size
}
