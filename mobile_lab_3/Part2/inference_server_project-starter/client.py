import cv2
import requests
import io
from PIL import Image

# Server URL
SERVER_URL = "http://127.0.0.1:5050/predict"

# OpenCV to access the camera
cap = cv2.VideoCapture(0)

if not cap.isOpened():
    print("Error: Unable to access the camera.")
    exit()

print("Press 'q' to quit the application.")

while True:
    # Capture frame-by-frame
    ret, frame = cap.read()

    if not ret:
        print("Failed to capture frame. Exiting.")
        break

    # Display the frame in a window
    cv2.imshow('Camera View', frame)

    # Convert the frame to RGB (OpenCV uses BGR by default)
    frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # Convert the frame to a PIL image
    pil_image = Image.fromarray(frame_rgb)
    # Save the image to an in-memory byte stream
    img_byte_arr = io.BytesIO()
    pil_image.save(img_byte_arr, format='JPEG')
    img_byte_arr.seek(0)

    # Send the frame to the server
    try:
        response = requests.post(SERVER_URL, files={'file': img_byte_arr})
        if response.status_code == 200:
            # print("Prediction:", response.json().get("predicted_label", "Unknown"))
            json_response = response.json()
            top5 = json_response.get("top5", [])      # Get top 5 predictions from server response
            score = json_response.get("score", 0.0)  # Default to 0.0 if no score is returned

            # Print all top 5 predictions with label and score
            print("Predictions:")
        for i, pred in enumerate(top5):
            label = pred.get("prediction_label", "Unknown")
            score = pred.get("score", 0.0)
            print(f"{i+1}. {label} (Score: {score:.2f})")
        else:
            print("Error:", response.json().get("error", "Unknown error"))
    except requests.exceptions.RequestException as e:
        print("Error connecting to the server:", e)

    # Break the loop when 'q' is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the camera and close windows
cap.release()
cv2.destroyAllWindows()

