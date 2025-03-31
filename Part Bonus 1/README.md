# Part 1 BONUS: Refactoring The GeoQuiz App

## Group Members
- William Tyrrell - wftyrrell@wpi.edu
- Michael Duggan - mpduggan@wpi.edu

## Lab Goal
This submission includes a refactored version of the GeoQuiz app using a **single-activity, multiple-fragment architecture**. 
The app now presents a list of questions on launch, and tapping a question navigates to a detail screen where users can answer 
the question, use the cheat feature, or navigate back to the question list.

---

## Implementation

### List-Detail Navigation
- Uses `ListFragment` to display a scrollable list of questions.
- Uses `DetailFragment` for answering and cheating on individual questions.
- Navigation is handled using Android's Navigation Component with `NavHostFragment`.

### ViewModel + LiveData
- `QuizViewModel` stores app state, including the list of questions, selected question index, cheated questions, and scroll position.
- `LiveData` ensures UI stays in sync and data persists through configuration changes.

### RecyclerView + CardView
- `RecyclerView` shows a stylized list of question entries using `MaterialCardView`.
- Each entry displays a numbered question (e.g., "Question 1") and subtitle ("Click to answer").

### Cheat Feature
- `CheatActivity` is retained and launched from `DetailFragment`.
- Uses `ActivityResultLauncher` and `Intent` to pass and receive cheat state.
- Cheat info is stored per-question in the ViewModel.

### Animations & Transitions
- Smooth slide-in/out transitions between fragments using `nav_graph.xml` and `res/anim/` files.

---

## Added and Edit Files

### Edited Files

| File | Purpose / Features |
|------|---------------------|
| `MainActivity.kt` | Now acts only as the host for `NavHostFragment`, no longer contains quiz logic. Uses the single-activity, multi-fragment pattern. |
| `QuizViewModel.kt` | Stores all app state, including questions, selected index, and cheat tracking. Uses `LiveData` and `SavedStateHandle`. |
| `CheatActivity.kt` | Updated to handle orientation changes and persist `isAnswerShown`. Communicates cheat result back to the fragment. |
| `activity_main.xml` | Now hosts only the `NavHostFragment`, which manages navigation between fragments. |

---

### New Files

| File | Purpose / Features                                                                                                                   |
|------|--------------------------------------------------------------------------------------------------------------------------------------|
| `DetailFragment.kt` | Shows question detail view. Handles answer buttons, cheat button, and back navigation. Retrieves data from `QuizViewModel`.          |
| `ListFragment.kt` | Displays a scrollable list of questions using `RecyclerView`. Handles click events, saves and restores scroll position via ViewModel. |
| `QuestionAdapter.kt` | Adapter for `RecyclerView`. Binds each question to a card view with "Question X" and a subtitle. Handles click events via lambda.    |
| `list_item_question.xml` | Layout for each item in the RecyclerView. Uses a vertical `LinearLayout` to show question title.|
| `nav_graph.xml` | Navigation graph that defines routes between `ListFragment` and `DetailFragment`. Used by Navigation Component.                      |
| `fragment_detail.xml` | Layout for `DetailFragment` including the question text, True/False buttons, Cheat button, and Back to List button.                  |
| `fragment_list.xml` | Layout for `ListFragment`, includes a `RecyclerView` properly padded below the ActionBar. Uses `fitsSystemWindows`.                  |
| `res/anim/slide_in_right.xml` | Slide-in animation for navigating to the detail view.                                                                                |
| `res/anim/slide_out_left.xml` | Slide-out animation for leaving the list view.                                                                                       |
| `res/anim/slide_in_left.xml` | Slide-in animation for navigating back to the list.                                                                                  |
| `res/anim/slide_out_right.xml` | Slide-out animation for leaving the detail view.                                                                                     |
| `strings.xml` | Updated to include all labels, questions, and message strings, including "Click to answer" for accessibility.                        |
| `themes.xml` | Theme using `MaterialComponents.DayNight.DarkActionBar`. Custom primary/secondary colors and styles applied.                         |





