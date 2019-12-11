package vgrm.colorcode;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vgrmm on 2019-12-05.
 */

public class Lab3Activity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private LocationManager locationManager;

    private Button startAndStop;

    private TextView xValue;
    private TextView yValue;
    private TextView zValue;

    private TextView coordinates;

    private boolean informationObtained;


    // ----- CAMERA -----

    private static final String TAG = "AndroidCamera";
    private Button takePictureButton;
    private TextureView textureView;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;


    // ----- ------ -----

    private float threshold = 0.25f;
    private TextView coordinates2;
    private TextView pitchText;
    private TextView tiltText;
    private LocationListener locationListener2;

    private Sensor compass;
    private ImageView compassImage;
    private float[] mMatrixR = new float[9];
    private float[] mMatrixValues = new float[3];

    private boolean isNorth = false;
    private float currentDegree = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lab3);

        informationObtained = false;
        startAndStop = (Button) findViewById(R.id.startstop_button);
        startAndStop.setOnClickListener(startandstopButtonClick);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



        xValue = (TextView) findViewById(R.id.x_value);
        yValue = (TextView) findViewById(R.id.y_value);
        zValue = (TextView) findViewById(R.id.z_value);

        xValue.setText("0.0");
        yValue.setText("0.0");
        zValue.setText("0.0");

        coordinates = (TextView) findViewById(R.id.coord);
        coordinates2 = (TextView) findViewById(R.id.coord2);
        pitchText = (TextView) findViewById(R.id.pitch);
        tiltText = (TextView) findViewById(R.id.tilt);

        compass = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        compassImage = (ImageView) findViewById(R.id.imageViewCompass);

        if (compass != null) {
            senSensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
        }

        locationListener2 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null)
                    coordinates2.setText(getString(R.string.latitude) + " "
                            + location.getLatitude() + " " + getString(R.string.longitude) + " " + location.getLongitude());
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, locationListener2);

        // ----- CAMERA -----

        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView !=null;
        textureView.setSurfaceTextureListener(textureListener);

        takePictureButton = (Button) findViewById(R.id.photo_button);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    View.OnClickListener startandstopButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (senAccelerometer == null){
                Toast.makeText(Lab3Activity.this,getString(R.string.no_sensor), Toast.LENGTH_LONG).show();
                return;
            }

            if(informationObtained){
                startAndStop.setText(R.string.start);
                senSensorManager.unregisterListener(Lab3Activity.this, senAccelerometer);
                informationObtained = false;
            }else{
                senSensorManager.registerListener(Lab3Activity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                startAndStop.setText(getString(R.string.stop));
                informationObtained = true;
            }
        }
    };

    private double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
            return null;

        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++)
            output[i] = input[i];

        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

            //xValue.setText(String.valueOf(event.values[0]));
            //yValue.setText(String.valueOf(event.values[1]));
            //zValue.setText(String.valueOf(event.values[2]));

            //  TASK 1
            float kFilteringFactor = 0.25f;
            float[] accel = new float[3];
            float[] result = new float[3];
            //high-pass filter to eliminate gravity
            accel[0] = event.values[0] * kFilteringFactor + accel[0] * (1.0f - kFilteringFactor);
            accel[1] = event.values[1] * kFilteringFactor + accel[1] * (1.0f - kFilteringFactor);
            accel[2] = event.values[2] * kFilteringFactor + accel[2] * (1.0f - kFilteringFactor);
            result[0] = event.values[0] - accel[0];
            result[1] = event.values[1] - accel[1];
            result[2] = event.values[2] - accel[2];

            if(Math.abs(Float.valueOf(xValue.getText().toString()) - result[0]) > threshold)
                xValue.setText(String.format("%.5f", result[0]));
            if(Math.abs(Float.valueOf(yValue.getText().toString()) - result[1]) > threshold)
                yValue.setText(String.format("%.5f", result[1]));
            if(Math.abs(Float.valueOf(zValue.getText().toString()) - result[2]) > threshold)
                zValue.setText(String.format("%.5f", result[2]));

            //  DEFENCE TASK
            if(result[2]<-7) {
                finish();
                moveTaskToBack(true);
                System.exit(0);
            }

            //  TASK 2

            float[] w = new float[3];
            w = event.values.clone();
            double[] g = new double[3];
            g = convertFloatsToDoubles(w);

            double norm_Of_g = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

            g[0] = g[0] / norm_Of_g;
            g[1] = g[1] / norm_Of_g;
            g[2] = g[2] / norm_Of_g;

            int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
            if (inclination < 25 || inclination > 155)
            {
                tiltText.setText("Flat");
            }
            else if (inclination > 25 && inclination < 80)
            {
                tiltText.setText("Screen upwards");
            }
            else if (inclination > 80 && inclination < 100)
            {
                tiltText.setText("Screen at 90 degrees");
            }
            else if (inclination > 100 && inclination < 155)
            {
                tiltText.setText("Screen downwards");
            }

            int rotation = (int) Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));
            if(rotation > 30)
            {
                pitchText.setText("Screen rotated left");
            }
            else if (rotation < -30)
            {
                pitchText.setText("Screen rotated right");
            }
            else if(rotation > -30 && rotation < 30)
            {
                pitchText.setText("Screen is vertical");
            }
        }

        //  TASK 3
        if(mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(mMatrixR, event.values);

            SensorManager.getOrientation(mMatrixR, mMatrixValues);

            // Use this value in degrees
            double mAzimuth = Math.toDegrees(mMatrixValues[0]);
            float degree = (float)mAzimuth;
            if(degree > 0 && degree < 2 && !isNorth)
            {
                isNorth = true;
                takePicture();
            }
            isNorth = false;
            RotateAnimation ra = new RotateAnimation(
                    currentDegree, -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(210);
            ra.setFillAfter(true);
            compassImage.startAnimation(ra);
            currentDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause(){
        super.onPause();
        if (senAccelerometer != null){
            senSensorManager.unregisterListener(Lab3Activity.this, senAccelerometer);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        this.locationManager.removeUpdates(this);

        stopBackgroundThread();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (senAccelerometer != null && informationObtained){
            senSensorManager.registerListener(Lab3Activity.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        startBackgroundThread();
        if(textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,400,1,this);

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
//            coordinates.setText("\n" + getString(R.string.latitude)+" " +location.getLatitude() + " \n"
//            + getString(R.string.longitude)+" " + location.getLongitude());
            coordinates.setText(getString(R.string.latitude)+" " +location.getLatitude() + " "
                    + getString(R.string.longitude)+" " + location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {

            cameraDevice.close();
            cameraDevice = null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(Lab3Activity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };

    protected void startBackgroundThread(){
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread(){
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    protected void takePicture(){
        if(null==cameraDevice){
            Log.e(TAG,"cameraDevice is null");
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try{
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if(characteristics!=null){
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640;
            int height = 480;
            if(jpegSizes != null && jpegSizes.length > 0){
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));


            //final File file = new File(Environment.getExternalStorageDirectory()+"pic.jpg");

            File sdCard = Environment.getExternalStorageDirectory();
            String path = sdCard.getAbsolutePath();
            file = new File(path,"pic.jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if(null != output) { output.close();}
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(Lab3Activity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createCameraPreview();
                        }
                    }, 3000);

                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    if(cameraDevice == null) {
                        return;
                    }
                    cameraCaptureSessions = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Toast.makeText(Lab3Activity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");

        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Lab3Activity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if(imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(Lab3Activity.this, "SORRY! you can't use this app without permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}
