import numpy as np
import tensorflow as tf

CATEGORIES= ['Apple Black Rot', 'Apple Cedar Rust', 'Apple Healthy', 'Apple Scab', 'Bell Pepper Bacterial Spot', 'Bell Pepper Healthy',
            'Blueberry Healthy', 'Cherry Healthy', 'Cherry Powdery Mildew', 'Corn Common Rust', 'Corn Gray Leaf Spot', 'Corn Northern Leaf Blight',
            'Corn healthy', 'Grape Black Measles', 'Grape Black Rot', 'Grape Healthy', 'Grape Leaf Blight', 'Orange Huanglongbing',
            'Peach Bacterial Spot', 'Peach Healthy', 'Potato Early Blight', 'Potato Healthy', 'Potato Late Blight', 'Raspberry Healthy',
            'Soybean Healthy', 'Squash Powdery Mildew', 'Strawberry Healthy', 'Strawberry Leaf Scorch', 'Tomato Bacterial Spot', 'Tomato Early Blight',
            'Tomato Healthy', 'Tomato Late Blight', 'Tomato Leaf Mold', 'Tomato Mosaic Virus', 'Tomato Septoria Leaf Spot', 'Tomato Target Spot',
            'Tomato Two Spotted Spider Mite', 'Tomato Yellow Leaf Curl Virus']

MODEL_PATH= "disease_classifier.tflite"
IMG_PATH= "data/Tomato Septoria Leaf Spot/0ab271a7-765e-4675-8bfc-e249c0c86fdd___Keller.St_CG 1778.JPG"
img= tf.keras.utils.load_img(IMG_PATH)
img = tf.image.resize(img, (128, 128))
img = tf.expand_dims(img, 0)
img= tf.cast(img, tf.float32)
img= img/255.0
tf.keras.backend.clear_session()
interpreter = tf.lite.Interpreter(model_path=MODEL_PATH)
classify_lite = interpreter.get_signature_runner('serving_default')

predictions_lite = classify_lite(input_2=img)['output_layer']
print(f"Image belongs to {CATEGORIES[np.argmax(predictions_lite)]} with {round(np.max(predictions_lite)*100.0, 2)}% confidence")