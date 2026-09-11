package edu.wpi.cs.cs4518.stepcounter_starter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class StepChartFragment : Fragment() {

    private lateinit var barChart: BarChart
    private var currentDate = Date()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var database: StepDatabase
    private lateinit var gestureDetector: GestureDetector

    private lateinit var dateTextView: TextView
    private lateinit var totalStepsTextView: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_step_chart, container, false)

        // Initialize views
        barChart = view.findViewById(R.id.barChart)

        // Initialize DB
        database = StepDatabase.getDatabase(requireContext())

        // Initialize date text view
        dateTextView = view.findViewById(R.id.textViewDate)

        // Initialize total steps text view
        totalStepsTextView = view.findViewById(R.id.textViewTotalSteps)

        // Swipe gesture detector
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                Log.d("Gesture", "onFling triggered")
                // e1 and e2 must be nullable (MotionEvent?) to match the exact method signature from the superclass
                if (e1 == null || e2 == null) return false

                val deltaX = e2.x - e1.x
                Log.d("Gesture", "onFling triggered")
                Log.d("Gesture", "deltaX = $deltaX")

                if (deltaX > 100 ) {
                    Log.d("Gesture", "Swiped left → goToPreviousDay()")
                    goToPreviousDay()
                } else if (deltaX < -100) {
                    Log.d("Gesture", "Swiped right → goToNextDay()")
                    goToNextDay()
                }
                return true
            }
        })

        // Touch listener for swipe
        val chartRoot = view.findViewById<LinearLayout>(R.id.chartRoot)
        val chartTouchListener = View.OnTouchListener { v, event ->
            Log.d("Gesture", "Touch event: ${event.action}")
            val result = gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            result
        }

        chartRoot.isClickable = true
        chartRoot.setOnTouchListener(chartTouchListener)
        barChart.setOnTouchListener(chartTouchListener)

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Restore date if rotated
        if (savedInstanceState != null && savedInstanceState.containsKey("currentDate")) {
            currentDate = Date(savedInstanceState.getLong("currentDate"))
            Log.d("Rotation", "Restored currentDate = $currentDate")
        } else {
            Log.d("Rotation", "First launch, using today's date")
        }

        preloadDummyData {
            loadStepsForDate(currentDate)
        }
    }

    // Save selected date on rotation
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("date", currentDate.time)
        super.onSaveInstanceState(outState)
    }

    //Dummy data for demo
    private fun preloadDummyData(onComplete: () -> Unit) {
        lifecycleScope.launch {
          database.stepDao().clearAllEntries()
            val fullFormat = SimpleDateFormat("yyyy-MM-dd HH", Locale.US)
            val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            fullFormat.timeZone = TimeZone.getDefault()

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -3) // Start 3 days ago


            withContext(Dispatchers.IO) {
                for (i in 0..3) {
                    val date = calendar.time
                    val dateString = dateOnlyFormat.format(date)

                    val dummySteps = listOf(
                        0 to 20 * (i + 1),
                        6 to 60 * (i + 1),
                        9 to 100 + 10 * i,
                        12 to 150 + 10 * i,
                        15 to 200 + 10 * i,
                        18 to 150 + 5 * i,
                        21 to 100 + 5 * i,
                    )

                    dummySteps.forEach { (hour, steps) ->
                        val timestamp = fullFormat.parse("$dateString ${hour.toString().padStart(2, '0')}")!!.time
                        database.stepDao().insert(StepEntry(0, timestamp, hour, steps))
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, 1) // move to next day
                }
            }
            //call callback after dummy data inserted
            onComplete()
        }
    }

    // Load data for current day
    private fun loadStepsForDate(date: Date) {
        val formatted = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(date)
        val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateString = dateOnlyFormat.format(date)

        lifecycleScope.launch {
            val entries = withContext(Dispatchers.IO) {
                database.stepDao().getHourlySteps(dateString)
            }

            //Date Label
            dateTextView.text = getString(R.string.steps_on_date, formatted)

            //Get Total Steps for the day
            val total = entries.sumOf { it.totalSteps }
            totalStepsTextView.text = getString(R.string.total_steps_display, total)
            Log.d("StepChartFragment", "Loaded ${entries.size} entries")
            entries.forEach {
                Log.d("StepChartFragment", "Hour: ${it.hour}, Steps: ${it.totalSteps}")
            }
            showBarChart(entries)
        }
    }

    private fun showBarChart(data: List<HourStepEntry>) {
        val fullData = (0..23).map { hour ->
            data.find { it.hour == hour } ?: HourStepEntry(hour, 0)
        }

        val entries = fullData.map {
            BarEntry(it.hour.toFloat(), it.totalSteps.toFloat())
        }

        val dataSet = BarDataSet(entries, "Hourly Steps").apply {
            color = Color.CYAN
            valueTextColor = Color.BLACK
            valueTextSize = 14f
            setDrawValues(true)

            setBarBorderWidth(1f)
            barBorderColor = Color.BLACK

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
        }

        val barData = BarData(dataSet).apply {
            barWidth = 2.0f  // Perfect for 1 unit spacing between hours
        }

        barChart.data = barData

//        val hourLabels = (0..23).map {
//            when (it) {
//                0 -> "12am"
//                6 -> "6am"
//                12 -> "12pm"
//                18 -> "6pm"
//                23 -> "12am"
//                else -> ""
//            }
//        }

        // X-Axis (use ValueFormatter not IndexAxisValueFormatter)
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            axisMinimum = -0.5f
            axisMaximum = 23.5f
            textSize = 14f
            textColor = Color.DKGRAY

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "12am"
                        6 -> "6am"
                        12 -> "12pm"
                        18 -> "6pm"
                        23 -> "12am"
                        else -> ""
                    }
                }
            }
            // Enable gridlines only at labeled positions
            setDrawGridLines(false) // We'll manually add limit lines instead

            // Add gridlines at custom hours
            removeAllLimitLines()
            listOf(0f, 6f, 12f, 18f, 23f).forEach { hour ->
                val line = com.github.mikephil.charting.components.LimitLine(hour, "")
                line.lineColor = Color.LTGRAY
                line.lineWidth = 0.5f
                addLimitLine(line)
            }

            setAvoidFirstLastClipping(false)
            setLabelCount(24, false) // Let it know about full range, but don't force labels
            yOffset = 12f

            setDrawGridLines(true)
            gridColor = Color.LTGRAY
            gridLineWidth = 0.5f

        }

        // Y-Axis
        barChart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 500f
            granularity = 100f
            textSize = 14f
            textColor = Color.DKGRAY
            setLabelCount(6, true)
            setDrawGridLines(true)
            gridColor = Color.LTGRAY
            gridLineWidth = 1f
        }

        barChart.axisRight.isEnabled = false

        // General chart styling
        barChart.setFitBars(true)
        barChart.setVisibleXRangeMaximum(24f) // show full day
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(true)
        barChart.setDrawBorders(true)
        barChart.setExtraOffsets(10f, 10f, 10f, 10f)
        barChart.animateY(500)
        barChart.animateX(300)
        barChart.invalidate()
        barChart.setDragEnabled(false)
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.setDoubleTapToZoomEnabled(false)
    }


        private fun goToPreviousDay() {
            currentDate = Calendar.getInstance().apply {
                time = currentDate
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            Log.d("Swipe", "Previous day: ${dateFormat.format(currentDate)}")
            loadStepsForDate(currentDate)
    }

    //swipe logic for going to different days
    private fun goToNextDay() {
        val today = dateFormat.format(Date())
        val target = dateFormat.format(currentDate)

        if (target < today) {
            currentDate = Calendar.getInstance().apply {
                time = currentDate
                add(Calendar.DAY_OF_YEAR, 1)
            }.time

            Log.d("Swipe", "Next day: ${dateFormat.format(currentDate)}")
            loadStepsForDate(currentDate)
        }

    }
}
