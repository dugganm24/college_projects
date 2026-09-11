# Bonus Part: Charting The Steps

## Group Members
- William Tyrrell - wftyrrell@wpi.edu
- Michael Duggan - mpduggan@wpi.edu

## Lab Goal
In this part, we will add to our step counter app, the ability to save the users recorded step data in an intuitive way. 
This involves the use of data storage, visualization, and lifecycle-aware UI data.

**For the purposes of this lab, dummy data is used to visualize the functionality of the UI and not actual data collected from sensors.**
---

## Saving Steps in a Database
Code is located at: `Part Bonus/sourceCode/StepCounter-starter/app/src/main/java/edu/wpi/cs/cs4518/stepcounter_starter`

Added Files:
 - `StepEntry.kt` (Room Entity)
   - Defines the Room entity representing an individual step event, including timestamp, hour, and step count
 - `HourStepEntry.kt` (Room Entity)
   - Defines a model for returning hourly step aggregates using a SQL query. This class is used to populate the bar chart
 - `StepDatabase.kt`
   - This sets up the room database instance and gives access to DAOs
 - `StepDao.kt`
   - Contains the database queries: provides a query to retrieve the total steps for a given day, 
   and a second query to aggregate step counts by hour

- `CounterViewModel.kt` was modified
  - This includes the initialization of the database and save steps inside the processSensorData function
  - in `processSensorData`, a database call is added which ensures step records are saved after they are returned from the server
- Added database initialization in `onCreate` of `MainActivity.kt`

## Display fragment for steps chart
Code is located at: `Part Bonus/sourceCode/StepCounter-starter/app/src/main/java/edu/wpi/cs/cs4518/stepcounter_starter`

Added Files:
- `StepChartActivity.kt`

  - Hosts the chart fragment
    
- `StepChartFragment.kt`
  
  - Loads hourly step data from database, for testing purposes dummy data is used not actual sensor data
  - Uses MPAndroidChart to display a bar chart of steps taken each hour
  - Supports swipe gestures using GestureDetector for navigating between days
  - Updates the chart and header with the corresponding date and total steps
  - Handles screen rotation and preserves selected date and chart state
  - Supports landscape mode with properly displayed UI
    
- `fragment_step_chart.xml`
  
  - UI layout for displaying the date and total steps at the top
  - Contains a BarChart that dynamically updates based on selected date
  - Uses LinearLayout with padding and spacing
    
- `activity_step_chart.xml`
  
  - Hosts the `StepChartFragment` inside the activity


