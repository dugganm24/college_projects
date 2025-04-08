# Lab 2 Part 1 Writeup 

The code for this portion of the lab is implemented in lab2part1.py, which is located in the same directory as this file. This code has comments to help understanding of the overall flow along with the purpose of helper functions in the high-level step counting algorithm. Each part below contains relevant code snippets related to the task, but they are also organized and labeled in the source code for viewing there. 

## Task 1: Visualizing acceleration data

### 1. Plotting code

```python
def visualize_xyz_data(data):
    """
    Visualize the x, y, z acceleration data.
    """
    plt.figure(figsize=(12, 6))
    plt.plot(data['timestamp'], data['x'], label='X-axis', color='red', alpha=0.5)
    plt.plot(data['timestamp'], data['y'], label='Y-axis', color='green', alpha=0.5)
    plt.plot(data['timestamp'], data['z'], label='Z-axis', color='blue', alpha=0.5)
    plt.xlabel('Time')
    plt.ylabel('Acceleration (m/s²)')
    plt.title('Raw Acceleration Data (X, Y, Z)')
    plt.xticks(rotation=45)
    plt.legend()
    plt.tight_layout()
    plt.show()
```

### 2. Plots 

`data/D1_lefthand_normal_20steps.csv`
![Alt text](writeup_images/D1_task1.png)

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`
![Alt text](writeup_images/D3_task1.png)


### 3. Questions
__Question 1__ 

The value ranges for the D1 trace were as follows:

    - X-axis: Approximately 6 - 12 m/s^2
    - Y-axis: Approximately 1 - 6 m/s^2
    - Z-axis: Approximately -2 - 2 m/s^2

The value ranges for the D3 trace were as follows:

    - X-axis: Approximately -1 - 2 m/s^2
    - Y-axis: Approximately -3 - 3 m/s^2
    - Z-axis: Approximately -4 - 4 m/s^2

These values were roughly obtained by intepreting the plots listed above. 

__Question 2__ 

The accelerometer data looks different for two main reasons. First, the data in D1 was collected with the phone in the left hand and the data in D3 was collected with the phone in the right hand. Depending on which hand was dominant for the person holding the phone, the amount of swaying may vary from hand to hand. The data in D1 was also collected with a regular accelerometer (which accounts for all accleration, including gravity), while the data in D3 was recorded with a linear accelerometer (which does not account for gravity). This discrepancy in sensor type can also account for some differences in data readings. 

## Task 2: Visualize the magnitude of acceleration

### 1. Plots 

`data/D1_lefthand_normal_20steps.csv`
![Alt text](writeup_images/D1_task2.png)


`data/D3_righthand_normal_20steps_linearaccelerometer.csv`
![Alt text](writeup_images/D3_task2.png)


### 2. Questions

__Question 1__

When observing the magnitude plots compared to the previous raw acceleration data plots, it is easy to see that the magnitude plot combines all three aces into one signal, which makes it easier to spot any peaks that correspond to step data. It also makes the signal smoother and better suited for step detection overall. 

## Task 3: Denoising the magnitude data

### 1. Bandpass filter code

```python
def band_pass_butterworth_filter(data, low_cutoff, high_cutoff, sampling_rate, order=4):
    """
    Apply a band-pass Butterworth filter to remove frequencies below and above specified cutoffs.
    """
    nyquist = 0.5 * sampling_rate
    low = low_cutoff / nyquist
    high = high_cutoff / nyquist
    b, a = butter(order, [low, high], btype='band', analog=False)
    y = filtfilt(b, a, data)
    return y

def apply_band_pass_filter(data, sampling_rate, low_cutoff=0.5, high_cutoff=5.0):
    """
    Apply band-pass filter to the magnitude of the acceleration data.
    """
    data['magnitude_band_filtered'] = band_pass_butterworth_filter(data['magnitude'], low_cutoff, high_cutoff, sampling_rate)
    visualize_data(data['timestamp'], data['magnitude_band_filtered'], 'Band Filtered Magnitude', 'Band-Pass Filtered Magnitude Over Time')
    return data
```

### 2. Plots 

`data/D1_lefthand_normal_20steps.csv`
![Alt text](writeup_images/D1_task3.png)

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`
![Alt text](writeup_images/D3_task3.png)


### 3. Exponential moving average code

```python
def apply_exponential_smoothing(data, alpha=0.5):
    """
    Apply exponential smoothing to reduce noise.
    """
    data['magnitude_smoothed'] = data['magnitude_band_filtered'].ewm(alpha=alpha).mean()
    visualize_data(data['timestamp'], data['magnitude_smoothed'], 'Exponentially Smoothed Magnitude', 'Exponentially Smoothed Magnitude Over Time')
    return data
```

### 4. Plots 

`data/D1_lefthand_normal_20steps.csv`
![Alt text](writeup_images/D1_task3.2.png)

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`
![Alt text](writeup_images/D3_task3.2.png)

## Task 4: Step counting on filtered magnitude data

### 1. Step counts

__D1 Steps Detected:__ 21 

__D3 Steps Detected:__ 20

### 2. Plots 

`data/D1_lefthand_normal_20steps.csv`
![Alt text](writeup_images/D1_task4.png)


`data/D3_righthand_normal_20steps_linearaccelerometer.csv`
![Alt text](writeup_images/D3_task4.png)

### 3. Step count tables

```
Total Step Count: 20
        ID      Actual    Detected     Passed?
         1          20          21        True
         2          20          21        True
         3          20          20        True
         4          20          21        True
         5          20          20        True
```

These values were obtainable by editing the height_threshold and distance in detect_peaks. We decreased the height_threshold by multiplying it by 0.85 to allow for more peaks to be included, and decreased the distance by dividing sampling_rate by 1.8 rather than 2 to allow for more peaks to be included that are close together.

```python
def detect_peaks(data, sampling_rate):
        """
        Detect peaks in the smoothed magnitude data to count steps.
        """
        height_threshold = 0.85 * (data.mean() + data.std()) # Adjusted height threshold for improved accuracy
        print ("height threshold", height_threshold)
        peaks, _ = find_peaks(data, height=height_threshold, distance=sampling_rate/1.8, prominence=0.25) # Adjusted distance for improved accuracy
        return peaks
```