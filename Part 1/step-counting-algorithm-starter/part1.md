# Lab 2 Part 1 Writeup 

The code for this portion of the lab is implemented in lab2part1.py, which is located in the same directory as this file. This code has comments to help understanding of the overall flow along with the purpose of helper functions in the high-level step counting algorithm. Each part below contains relevant code snippets related to the task, but they are also organized and labeled in the source code for viewing there. 

## Task 1: Visualizing acceleration data

### 1. Plotting code
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

### 2. Plots 
`data/D1_lefthand_normal_20steps.csv`
![Alt text](writeup_images/D1_task1.png)

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`

### 3. Questions


## Task 2: Visualize the magnitude of acceleration

### 1. Plots 
`data/D1_lefthand_normal_20steps.csv`

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`

### 2. Questions


## Task 3: Denoising the magnitude data

### 1. Bandpass filter code


### 2. Plots 
`data/D1_lefthand_normal_20steps.csv`

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`

### 3. Exponential moving average code


### 4. Plots 
`data/D1_lefthand_normal_20steps.csv`

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`

## Task 4: Step counting on filtered magnitude data

### 1. Step counts


### 2. Plots 
`data/D1_lefthand_normal_20steps.csv`

`data/D3_righthand_normal_20steps_linearaccelerometer.csv`

### 3. Step count tables