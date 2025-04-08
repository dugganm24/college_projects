 # Step 0: Import libraries and preparation
import numpy as np
import pandas as pd 
import matplotlib.pyplot as plt
from scipy.signal import butter, filtfilt, find_peaks

# Function to convert timestamp to datetime and timezone
def convert_timestamp(data):
    """
    Convert the timestamp to datetime format and set the correct timezone.
    """
    data['timestamp'] = pd.to_datetime(data['timestamp'], unit='ms')
    data['timestamp'] = data['timestamp'].dt.tz_localize('UTC').dt.tz_convert('US/Eastern')
    return data

# Function to load and preprocess accelerometer data
def load_accelerometer_data(file_path):
    """
    Load accelerometer data from the provided CSV file.
    """
    data = pd.read_csv(file_path, header=0, names=['timestamp', 'x', 'y', 'z'])

    # Analyze accelerometer data to calculate sampling rate
    def analyze_accelerometer_data(data):        
        num_samples = data.shape[0]
        time_differences = data['timestamp'].diff().dropna()
        average_sampling_interval = time_differences.mean()  # In milliseconds
        sampling_rate = 1000 / average_sampling_interval if average_sampling_interval > 0 else float('inf')
        return num_samples, sampling_rate

    num_samples, sampling_rate = analyze_accelerometer_data(data)
    print(f"Number of samples: {num_samples}")
    print(f"Sampling rate: {sampling_rate:.2f} Hz")
    
    data = convert_timestamp(data)
    return data, sampling_rate 

# Task 1: Visualizing raw acceleration data
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
    
# Task 2: Visualize the magnitude of acceleration
def visualize_data(data_x, data_y, label, title):
    """
    General function to visualize data with specified x and y columns.
    """
    plt.figure(figsize=(12, 6))
    plt.plot(data_x, data_y, color='purple', label=label, alpha=0.7)
    plt.xlabel('Time')
    plt.ylabel('Acceleration (m/s²)')
    plt.title(title)
    plt.xticks(rotation=45)
    plt.legend()
    plt.tight_layout()
    plt.show()

def calculate_magnitude(data):
    """
    Calculate the magnitude of acceleration vector.
    """
    data['magnitude'] = np.sqrt(data['x']**2 + data['y']**2 + data['z']**2)
    visualize_data(data['timestamp'], data['magnitude'], 'Magnitude', 'Acceleration Magnitude Over Time')
    return data

# Task 3: Denoising the magnitude data 
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

# Denoising with exponential smoothing 
def apply_exponential_smoothing(data, alpha=0.5):
    """
    Apply exponential smoothing to reduce noise.
    """
    data['magnitude_smoothed'] = data['magnitude_band_filtered'].ewm(alpha=alpha).mean()
    visualize_data(data['timestamp'], data['magnitude_smoothed'], 'Exponentially Smoothed Magnitude', 'Exponentially Smoothed Magnitude Over Time')
    return data

# Task 4: Step counting on filtered magnitude data 
# Helper function to detect peaks
def detect_peaks(data, sampling_rate):
        """
        Detect peaks in the smoothed magnitude data to count steps.
        """
        height_threshold = data.mean() + data.std()
        print ("height threshold", height_threshold)
        peaks, _ = find_peaks(data, height=height_threshold, distance=sampling_rate/2, prominence=0.25)
        return peaks
    
# Helper function to visualize step detection
def visualize_step_detection(data_x, data_y, data_y_label, peaks, step_count):
    """
    Visualize the detected steps on the smoothed magnitude data.
    """
    plt.figure(figsize=(12, 6))
    plt.plot(data_x, data_y, label=data_y_label, color='red', alpha=0.7)
    plt.scatter(data_x.iloc[peaks], data_y.iloc[peaks], color='blue', label='Detected Steps', zorder=3)
    plt.xlabel('Time (Eastern Time)')
    plt.ylabel('Acceleration Magnitude (m/s²)')
    plt.title(f'Step Counting Using Peak Detection on {data_y_label} (Total Steps: {step_count})')
    plt.xticks(rotation=45)
    plt.legend()
    plt.tight_layout()
    plt.show()

def step_detection_algorithm(file_path):    
    # Load and preprocess data
    data, sampling_rate = load_accelerometer_data(file_path)
    
    # Visualize raw x, y, z data before calculating magnitude
    visualize_xyz_data(data)
    
    # Step 1: Calculate Magnitude of Acceleration Vector
    data = calculate_magnitude(data)
    
    # Step 2: Apply Band-Pass Filtering
    sampling_rate = len(data) / (data['timestamp'].iloc[-1] - data['timestamp'].iloc[0]).total_seconds()
    data = apply_band_pass_filter(data, sampling_rate)
    
    # Step 3: Apply Exponential Smoothing
    data = apply_exponential_smoothing(data)
    
    # Step 4: Peak Detection for Step Counting
    peaks = detect_peaks(data['magnitude_smoothed'], sampling_rate)
    step_count = len(peaks)
    
    # Step 5: Visualization of Step Detection
    visualize_step_detection(data['timestamp'], data['magnitude_smoothed'], 'Exponentially Smoothed Magnitude', peaks, step_count)
    
    # Print total step count
    print(f'Total Step Count: {step_count}')
    
    return step_count

# Run algorithm with D1, D3, D5 for submission
step_detection_algorithm("data/D1_lefthand_normal_20steps.csv")
step_detection_algorithm("data/D3_righthand_normal_20steps_linearaccelerometer.csv")
step_detection_algorithm("data/D5_lefthand_slow_circle_hop_20steps_linearaccelerometer.csv")

import glob, re

directory_path = "data"
files = sorted(glob.glob(f"{directory_path}/D*"))

results = []

for f in files:
    num_steps = step_detection_algorithm(f)

    match = re.search(r'D(\d+)', f)
    id = match.group(1)
    
    actual_steps = 20
    test_pass = abs(num_steps - actual_steps) <= 1

    results.append([id, actual_steps, num_steps, test_pass])

# Print results table
print("{:>10}  {:>10}  {:>10}  {:>10}".format('ID', 'Actual', 'Detected', 'Passed?'))
for row in results:
    print("{:>10}  {:>10}  {:>10}  {:>10}".format(row[0], row[1], row[2], str(row[3])))

