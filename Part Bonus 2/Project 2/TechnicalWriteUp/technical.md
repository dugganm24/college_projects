Project Setup

The Jetchat app has a fairly straightforward setup process. You first need to download Android Studio and set it up with the default settings and SDK components. 
You can either download a local copy of the project or the easiest way is to clone the project from (https://github.com/android/compose-samples.git). 
In this repository, you will find many projects that utilize Jetpack compose. Copy the Jetchat folder into the desired local directory after cloning and open it in the IDE. 
The user should be able to compile and run the app with no difficulties. This is because this project uses Gradle to handle dependencies management and will download the 
necessary ones for the project on build and sync. The necessary dependencies for this project include Jetpack Compose, AndoridX Navigation, Material3, LiveData & ViewModel, 
and Drag and Drop API. These were all resolved using gradle without the need for editing and updating gradle files. The app was successfully run using a Medium Phone API 36 
emulator and no issues were encountered.

App Overview

The Jetchat app is a sample chat app built using Jetpack Compose. Its primary purpose is to simulate a messaging experience in a similar way to Slack. 
It allows users to chat, switch profiles, and explore other Compose capabilities. Jetchat compose is the recommended modern toolkit for Android for building native UI. 
It simplifies the process allowing more powerful UI components, tools, and Kotlin APIs while using less code. This sample showcases many features including UI state management, 
Integration with Architecture Components: Navigation, Fragments, ViewModel, back button handling, text input and focus management, many different transitions and animations, 
saved state across configurations changes, material design 3 theming and Material You dynamic color. The app is structured following the Model-ViewModel pattern and uses Jetpack 
Compose for the UI. The ConversationContent composable in Conversation.kt is the entry point for the app and takes a ConversationUiState that defines what is displayed. 
The ProfileFragment file explains how to pass between fragments with the navigation component. It also explains how to observe the state from ViewModel served via LiveData. 
UserInput.kt handles much of the user interactions, this includes back button management, extended controls, showing and hiding the keyboard, and saving states across 
configurations. Animations and transitions are defined in FloatingActionButton of the profile screen and implemented in AnimatingFabContent.kt.

Key Feature 1: Chat Messages

This feature enables users to send and view messages in the conversation screen. Messages are displayed in a Slack-style chat window that allows users to input text and emojis. 
While still under development, the chat feature demonstrates how a messaging service should handle text and emoji input.

The first component of this feature is the UI Input handling. UserInputText is a composable within the UserInput.kt file that defines a BasicTextField API for 
free-form input to take message input. Input is tracked using TextFieldValue API and stored with rememberSaveble. rememberSavebale is a compose function that remembers 
the value produced by calculation across configuration changes. It saves the state in a bundle which is restored when the activity or composable is recreated. 
This ensures that the text the user is typing is preserved across screen rotations. StakeyboardOptions and keyboardActions are used to customize the keyboard’s 
appearance and behavior for the app. OnFocusChanged API is used to detect when the text field gains or loses focus important for managing keyboard visibility. 
When the send button is pressed in UserInputSelector, the onMessageSent lambda is triggered inside UserInput. 
The content is passed to uiState.addMessage(Message(authorMe, content, timeNow)) which appends the message to the chat history in memory.

To display messages, they are rendered in ConversationContent.kt using Message(), AuthorAndTextMessage(), and ChatItemBubble. Messages() passes content to LazyColumn API 
to render chat bubbles. The MessageFormatter.kt file is used to format the text content of the messages with the correct styling. Once a message is sent and displayed, 
the chat automatically scrolls to the bottom. This is handled in ConversationContent.kt and the action is managed via LazyListStat and a coroutine scope.launch. 
This ensures that it jumps to the next message after it is sent.

Key Feature 2: View Profile using JetChat Drawer

The view profile feature allows a user to view detailed information about themselves or other users in the chat. This is functionally accessed via the sidebar by clicking on 
the icon in the top left corner. They can then access their profile or other users by selecting them from the list. The Profile shows information including their photo, name, 
and other bio information. The user can also click directly on profile pictures to direct to the user's profile in the chat window.

The flow execution begins when a user navigates to a profile screen from the navigation drawer or within the chat interface. In NavActivity.kt, 
when a user clicks on a profile in the drawer, the onProfileCLicked Lmabda is called. This lambda creates a bundle containing the user's ID and uses 
findNavController() to navigate the profile fragment. In ProfileFragment.kt, the ProfileFragment is the Android fragment that hosts the Compose UI for the profile screen. 
When the fragment is created, onCreateView() is called in ProfileFragment.kt. This inflates the fragment’s layout which contains two ComposeView API instances, 
one for the app bar and one for profile content. The viewModels() kotlin delegate is used to instantiate the ProfileViewModel. 
The ProfileViewModel.setUserID(userId) method is then called. The first ComposeView renders that app bar using JetchatAppBar and handles the “More” options click. 
The second ComposeView renders the profile content. This observes the userData and LiveData Lifecycle API from ProfileViewModel. ProfileViewMOdel is responsible for 
managing profile data that is statically set in the code in Fakedata.kt.

The ProfileScreen() composable displays the user’s profile information. It takes ProfileScreenState as input which includes various compose components. 
These include column, image, text, divider, and FloatinActionButton to display profile details. AnimatingFabContent.kt is used to animate the floating 
action button with several composables such as ProfileHeader(), UserInfoFields(), and ProfileProperty() to create the profile screen. NavACtivity.kt handles 
navigation to the profile screen. It uses findNavController() and navigate() API. 
