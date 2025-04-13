package edu.wpi.cs.cs4518.stepcounter_starter

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StepChartFragment : Fragment() {

    private lateinit var barChart: BarChart
    private var currentDate = Date()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var database: StepDatabase
    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_step_chart, container, false)

        // Initialize views
        barChart = view.findViewById(R.id.barChart)

        // Initialize DB
        database = StepDatabase.getDatabase(requireContext())

        // Swipe gesture detector
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val deltaX = e2.x - e1.x
                if (deltaX < -100) goToPreviousDay()
                if (deltaX > 100) goToNextDay()
                return true
            }
        })

        // Touch listener for swipe
        view.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Restore date if rotated
        savedInstanceState?.getLong("date")?.let {
            currentDate = Date(it)
        }

        loadStepsForDate(currentDate)
    }

    // Save selected date on rotation
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("date", currentDate.time)
        super.onSaveInstanceState(outState)
    }

    // Load data for current day
    private fun loadStepsForDate(date: Date) {
        val dateString = dateFormat.format(date)

        lifecycleScope.launch {
            val entries = database.stepDao().getHourlySteps(dateString)
            showBarChart(entries)
        }
    }

    private fun showBarChart(data: List<HourStepEntry>) {
        val entries = data.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.totalSteps.toFloat())
        }

        val labels = data.map { it.hour }

        val dataSet = BarDataSet(entries, "Hourly Steps")
        val barData = BarData(dataSet)

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.invalidate()
    }

    private fun goToPreviousDay() {
        currentDate = Calendar.getInstance().apply {
            time = currentDate
            add(Calendar.DAY_OF_YEAR, -1)
        }.time
        loadStepsForDate(currentDate)
    }

    private fun goToNextDay() {
        val today = dateFormat.format(Date())
        val target = dateFormat.format(currentDate)
        if (today != target) {
            currentDate = Calendar.getInstance().apply {
                time = currentDate
                add(Calendar.DAY_OF_YEAR, 1)
            }.time
            loadStepsForDate(currentDate)
        }
    }
}
