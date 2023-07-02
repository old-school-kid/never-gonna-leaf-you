## Table of Contents

-   [Introduction](#Intro-to-Project)
-   [Video](#Video-demo-of-app)
-   [Dataset](#Dataset)
-   [Tech Stack](#Tech-Stack)
-   [Workflow](#Workflow)
-   [To Do](#To-Do)

## Intro to Project

An Android app that identifies plant leaf diseases can revolutionize agriculture by providing farmers with quick and accurate diagnoses, enabling timely intervention to minimize crop losses. This cost-effective tool empowers users to take immediate action, implement preventive measures, and reduce the need for excessive chemical interventions. Researchers can leverage the app to contribute to a comprehensive database of plant diseases, aiding in scientific advancements. Additionally, plant enthusiasts can maintain the health of their plants and gain valuable knowledge about disease management. With the power of AI and ML, this app has the potential to transform plant disease management and contribute to a more sustainable future.

## Dataset

The dataset contains a total of **38 classes** of plant disease and **1** class of background images listed below:
| | | | |
| :---: | :----: | :---: | :---: |  
| Apple Scab | Apple Black Rot | Apple Cedar Rust | Apple Healthy |
| Blueberry Healthy | Cherry Healthy | Cherry Powdery Mildew | Corn Northern Leaf Blight|
|Corn Gray Leaf Spot |Corn Common Rust |Corn healthy | Grape Black Rot |  
|Grape Black Measles | Grape Leaf Blight | Grape Healthy | Bell Pepper Healthy |
| Orange Huanglongbing|Peach Bacterial Spot | Peach Healthy |Bell Pepper Bacterial Spot|
| Potato Early Blight | Potato Healthy | Potato Late Blight |Raspberry Healthy |
| Soybean Healthy | Squash Powdery Mildew| Strawberry Healthy | Strawberry Leaf Scorch |
|Tomato Bacterial Spot| Tomato Early Blight | Tomato Late Blight |Tomato Leaf Mold |
|Tomato Septoria Leaf Spot| Tomato Two Spotted Spider Mite | Tomato Target Spot |Tomato Mosaic Virus |
|Tomato Yellow Leaf Curl Virus | Tomato Healthy | | |

All images in the dataset are released under the Creative Commons Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0), with the clarification that algorithms trained on the data fall under the same license.

```bibtex
@article{Hughes2015PlantVillageDisease,
    title   = {An open access repository of images on plant health to enable the development of mobile disease diagnostics},
    author  = {David. P. Hughes and Marcel Salathe},
    journal = {ArXiv},
    year    = {2015},
    volume  = {abs/1511.08060}
}
```

## Tech-Stack

</br>
<p>
<img alt="Python" src="https://img.shields.io/badge/python%20-%2314354C.svg?&style=for-the-badge&logo=python&logoColor=white"/>
<img alt="Tensorflow" src="https://img.shields.io/badge/TensorFlow-%23FF6F00.svg?style=for-the-badge&logo=TensorFlow&logoColor=white"/>
<img alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"/>  
<img alt="Android Studio" src="https://img.shields.io/badge/Android%20Studio-3DDC84.svg?style=for-the-badge&logo=android-studio&logoColor=white"/>  
</p>
</br>

## Workflow

First we use a contrastive supervised learning pretraining to train a CNN encoder. We use a [modified DenseNet](https://www.sciencedirect.com/science/article/abs/pii/S0927025621005383?via%3Dihub) as the encoder. We have used [sharpness aware minimization](https://arxiv.org/abs/2010.01412) for optimization of the model. Sharpness aware minimization attempts to simultaneously minimize loss value as well as loss curvature thereby seeking parameters in neighborhoods having uniformly low loss value. This is indeed different from traditional SGD-based optimization that seeks parameters having low loss values on an individual basis. The original [DenseNet model](https://arxiv.org/abs/1608.06993) has 20.2M parameters and is of 80mb disk size in float32 precision. Our model has 0.42M parameters and requires a 3.7mb disk space. For faster inference we have a 8 bit model too. We have distilled the model further to a size of 0.5mb for faster inference on edge in mobile devices.

We plug this tflite model to a simple to use app that can predict leaf disease instantaneously. One can capture a photo directly and run inference or can upload an image from gallery and run inference on it. You can find the apk of the app in releases.

[![App UI](leaf%20disease%20app.jpg)](https://github.com/old-school-kid/never-gonna-leaf-you/blob/main/leaf%20disease%20app.jpg)

## To-Do

-   [x] Create a license
-   [x] Add 8bit model
-   [x] Add optimized and tflite model
-   [x] [Add a model optimized with sharpness aware minimization](https://arxiv.org/abs/2010.01412)
-   [x] Add app
