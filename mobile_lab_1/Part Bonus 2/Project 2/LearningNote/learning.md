Learning Note:

In this project, I explored the handling of user input and navigation within a Jetpack Compose application and also persisting data across screen rotation and process death. 
I got experience working with a chat interface and how data is passed between different states and updated in the UI.

One key thing that was demonstrated in this project was the effective handling of the back button behavior using the BackHandler composable. 
For example, when the emoji selector panel is displayed, the default back button action is intercepted which prevents navigation to previous screens and instead, 
closes the panel and displays the keyboard. This is achieved by conditionally activating a BackHandler based on the visibility of the input selector. When active it 
triggers a lambda that closes the selector rather than going to a previous screen.

Another key takeaway is the way the app handles text input and focus management. This is achieved by synchronizing the keyboard’s visibility with the display of the emoji panel. 
When the emoji panel is shown, the keyboard must be hidden, and vice versa. This behavior is implemented using FocusRequester and onFocusChanged APIs. 
These handle the exchange between the keyboard and emoji panel and set the focus for the appropriate based on selection.

Both of these features highlight the strength and flexibility of Jetpack Compose in handling UI interactions. 
I have not worked with these interactions before so seeing how they work will aid me in future projects where I will need these types of UI and persistent activities. 
