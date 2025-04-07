import logging
from flask import Flask, request, jsonify
import pandas as pd
import json
import numpy as np
from scipy.signal import butter, filtfilt, find_peaks
import random 

app = Flask(__name__)

# Configure logging
logging.basicConfig(
    level=logging.INFO,  # Set the logging level (e.g., DEBUG, INFO, WARNING, ERROR)
    format="%(asctime)s - %(levelname)s - %(message)s",
    handlers=[
        logging.FileHandler("server.log"),  # Log to a file
        logging.StreamHandler()            # Log to the console
    ]
)

logger = logging.getLogger(__name__)


@app.route('/data', methods=['POST'])
def process_data():
    # Get the JSON data from the request
    data = request.get_json()

    # Process the received data
    if not data or not isinstance(data, list):
        return jsonify({"error": "Invalid data format"}), 400

    newData = json_to_dataframe(data)

    # Calculate the sampling rate
    time_differences = newData['timestamp'].diff().dropna()  # Time differences between consecutive samples
    average_sampling_interval = time_differences.mean()  # Average time interval in milliseconds
    sampling_rate = 1000 / average_sampling_interval if average_sampling_interval > 0 else float('inf')  # Convert interval to Hz

    number_of_steps = step_detection(newData, sampling_rate)    
    logger.info(f"Sampling rate: {sampling_rate}, number of steps: {number_of_steps}")

    # Return processed results
    return jsonify({"status": "success", "results": number_of_steps}), 200

def json_to_dataframe(json_data):
    """
    Convert JSON data to a Pandas DataFrame.

    Parameters:
        json_data (str): JSON data as a string.

    Returns:
        pd.DataFrame: DataFrame with columns ['timestamp', 'x', 'y', 'z'].
    """
    try:
        # If json_data is a string, parse it into a Python object
        if isinstance(json_data, str):
            import json
            json_data = json.loads(json_data)
        
        # Ensure the data is a list of dictionaries
        if not isinstance(json_data, list):
            raise ValueError("Input data should be a list of dictionaries.")
        
        # Convert the JSON data to a DataFrame
        df = pd.DataFrame(json_data)
        
        # Ensure the DataFrame has the required columns
        required_columns = ['timestamp', 'x', 'y', 'z']
        if not all(column in df.columns for column in required_columns):
            raise ValueError(f"Input data is missing required columns: {required_columns}")
        
        # Select only the required columns
        df = df[required_columns]
        
        return df
    except (ValueError, KeyError, TypeError) as e:
        raise ValueError(f"Error processing JSON data: {e}")
    
    
# TODO: this is essentially the step detection algorithm you implemented in Part 1 
def step_detection(data, sampling_rate):
    step_count = random.randint(1, 5)
    
    return step_count


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5050, debug=True)

