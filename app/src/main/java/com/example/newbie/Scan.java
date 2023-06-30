package com.example.newbie;

import static org.tensorflow.lite.DataType.FLOAT32;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scan extends AppCompatActivity {
    ImageView photo;
    Button scan, upload, predict;
    TextView info;
    String imagePath;
    static final int CAMERA_REQUEST_ID = 100;
    static final int SELECT_PIC = 200;
    private Interpreter tfLiteModel;
    private Bitmap selectedImage;
    static final int inputWidth = 128;
    static final int inputHeight = 128;
    private static final List<String> CATEGORIES = Arrays.asList("Apple Black Rot", "Apple Cedar Rust", "Apple Healthy", "Apple Scab", "Bell Pepper Bacterial Spot", "Bell Pepper Healthy",
            "Blueberry Healthy", "Cherry Healthy", "Cherry Powdery Mildew", "Corn Common Rust", "Corn Gray Leaf Spot", "Corn Northern Leaf Blight",
            "Corn healthy", "Grape Black Measles", "Grape Black Rot", "Grape Healthy", "Grape Leaf Blight", "Orange Huanglongbing",
            "Peach Bacterial Spot", "Peach Healthy", "Potato Early Blight", "Potato Healthy", "Potato Late Blight", "Raspberry Healthy",
            "Soybean Healthy", "Squash Powdery Mildew", "Strawberry Healthy", "Strawberry Leaf Scorch", "Tomato Bacterial Spot", "Tomato Early Blight",
            "Tomato Healthy", "Tomato Late Blight", "Tomato Leaf Mold", "Tomato Mosaic Virus", "Tomato Septoria Leaf Spot", "Tomato Target Spot",
            "Tomato Two Spotted Spider Mite", "Tomato Yellow Leaf Curl Virus");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().hide();
        photo = findViewById(R.id.imageView2);
        info = findViewById(R.id.textView2);
        upload = findViewById(R.id.button3);
        scan = findViewById(R.id.button);
        predict = findViewById(R.id.button2);

       try {
            tfLiteModel = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        scan.setOnClickListener(view -> {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the activity with camera_intent, and request pic id
            startActivityForResult(camera_intent, CAMERA_REQUEST_ID);
        });

        upload.setOnClickListener(view -> imageChooser());

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage(selectedImage);
            }
        });

    }

    void imageChooser() {

        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PIC);
    }


    private MappedByteBuffer loadModelFile() throws IOException {
        // Load the model file from the assets folder

        AssetFileDescriptor fileDescriptor = getAssets().openFd("disease_classifier.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_ID && data != null) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                selectedImage=bitmap;
                photo.setImageBitmap(bitmap);
            } else if (requestCode == SELECT_PIC && data != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    selectedImage=bitmap;
                    photo.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
            // Process the captured image

    }




    private void processImage(Bitmap imageBitmap) {
        // Preprocess the image

        // Run inference
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 128, 128, true);
        ByteBuffer inputBuffer = preprocessImage(resizedBitmap);

        // Allocate output tensor buffer
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, CATEGORIES.size()}, DataType.FLOAT32);

        // Run inference
        tfLiteModel.run(inputBuffer, outputBuffer.getBuffer());

        // Get the prediction results
        float[] predictions = outputBuffer.getFloatArray();

        // Find the predicted category index and confidence
        int predictedIndex = getPredictedClassIndex(predictions);
        float confidence = predictions[predictedIndex] * 100.0f;

        // Get the predicted category label
        String predictedCategory;
        predictedCategory = CATEGORIES.get(predictedIndex);
        String result = "The predicted disease category is: " + predictedCategory + " with " + String.format("%.2f", confidence) + "% confidence";
        info.setText(result);
    }

    private ByteBuffer preprocessImage(Bitmap image) {
        int inputSize = 128;
        int batchSize = 1;
        int channels = 3;

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(batchSize * inputSize * inputSize * channels * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[inputSize * inputSize];
        image.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize);

        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = pixels[pixel++];
                inputBuffer.putFloat((float) ((pixelValue >> 16) & 0xFF) / 255.0f);
                inputBuffer.putFloat((float) ((pixelValue >> 8) & 0xFF) / 255.0f);
                inputBuffer.putFloat((float) (pixelValue & 0xFF) / 255.0f);
            }
        }

        return inputBuffer;
    }

    private int getPredictedClassIndex(float[] output) {
        int maxIndex = 0;
        float maxProbability = output[0];

        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxProbability) {
                maxIndex = i;
                maxProbability = output[i];
            }
        }

        return maxIndex;
    }

   /* private float getConfidence(float[] output, int predictedClassIndex) {
        return output[predictedClassIndex] * 100.0f;
    }*/
}