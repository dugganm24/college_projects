__Team Information__

Member | Email
-------|-------
Michael Duggan | mpduggan@wpi.edu
William Tyrrell | wftyrrell@wpi.edu

__Lab 2 Part 3 - Overview__

In this portion of the lab, the group was tasked with implementing a step-counting app, integrating what the group has learned and implemented in parts 1 and 2 of the lab. 

For this exercise, the group decided to utilize a client-server architecture, as the algorithm we implemented in Part 1 was already written in Python and was dependent on Python libraries. In order to utilize this server.py file, we implemented the algorithm from Part 1 into the step_detection function in server.py. 

After implementing the Python algorithm, we then improved the UI of the app in activity_main.xml. We utilized the editor to add an app title, a header above he step count, and a button to reset the step count which we would utilize later. These additions improved upon the app interface itself along with its functionality and clarity. 

After improving upon the UI, we then worked in CounterViewModel to implement the addSensorData() and processSensorData() functions. We implemented addSensorData() such that the sensor data was stored in a buffer, and upon reaching 50 samples we call processSensorData(). Upon its calling, processSensorData() pulled the data samples from the buffer, then passed this data to the step counting algorithm in the server using the okhttp3 library. After receiving the response from the  server, this function also updates the step count to be displayed in the app. 

After improving the ViewModel, the group then focused on the MainActivity file. We were able to add a reset function, which set the step count to 0 upon pressing the newly added reset button in the UI.

Upon testing these newly implemented features, the group found that the app was unable to connect to the server even though it was not running. Through some research, the group realized that we needed to add a network security configuration that allowed us to connect to the host that the server was running on through HTTP (see src/main/res/xml/network_security_config.xml). After creating this configuration and including it in AndroidManifest.xml, the app was able to connect to the server.

After getting the app to run, the group needed to ensure that the step counts should persist through screen rotations. In order to do this, the group placed the step count value in the CounterViewModel, which allows for its value to survive configuration changes like screen rotations. 

These changes allowed the group to implement a fully functional step counter app for this portion of the project per its requirements, which you can see in the screen recording included in this submission. For any questions refer to the source code and screen recording, or reach out to the team members listed above. 