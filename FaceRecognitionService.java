import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FaceRecognitionService {

    private CascadeClassifier faceDetector;
    private static final String HAAR_CASCADE_PATH = "haarcascade_frontalface_default.xml";
    private static final int CAPTURE_COUNT = 5;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public FaceRecognitionService() {
        faceDetector = new CascadeClassifier();

        if (!faceDetector.load(HAAR_CASCADE_PATH)) {
            String altPath = System.getProperty("user.dir") + "/opencv/data/" + HAAR_CASCADE_PATH;
            faceDetector.load(altPath);
        }
    }

    /**
     * Capture face from webcam and encode the detected face images.
     */
    public String captureFace() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Error: Cannot open camera");
            return null;
        }

        List<Mat> faceImages = new ArrayList<>();
        Mat frame = new Mat();

        try {
            // Warm up camera
            for (int i = 0; i < 10; i++) {
                camera.read(frame);
            }

            int capturedCount = 0;
            int attempts = 0;
            int maxAttempts = 100;

            while (capturedCount < CAPTURE_COUNT && attempts < maxAttempts) {
                camera.read(frame);
                if (frame.empty()) {
                    attempts++;
                    continue;
                }

                Mat gray = new Mat();
                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(gray, gray);

                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(gray, faces);

                Rect[] detected = faces.toArray();
                if (detected.length > 0) {
                    Rect r = detected[0];
                    Mat face = new Mat(gray, r);

                    // standard size
                    Mat resized = new Mat();
                    Imgproc.resize(face, resized, new Size(200, 200));

                    faceImages.add(resized.clone());
                    capturedCount++;

                    Thread.sleep(200);
                }
                attempts++;
            }

            if (faceImages.isEmpty()) return null;

            return encodeFaceData(faceImages);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            camera.release();
        }
    }

    /**
     * Compare face images WITHOUT LBPH.
     * Method: average pixel difference between images.
     */
    public boolean matchFaces(String capturedFaceData, String storedFaceData) {
        List<Mat> captured = decodeFaceData(capturedFaceData);
        List<Mat> stored = decodeFaceData(storedFaceData);

        if (captured.isEmpty() || stored.isEmpty()) {
            return false;
        }

        int matchCount = 0;

        for (Mat cap : captured) {
            for (Mat st : stored) {
                if (compareFaces(cap, st)) {
                    matchCount++;
                }
            }
        }

        // if any match is found
        return matchCount > 0;
    }

    /**
     * Pixel-based comparison of two 200x200 grayscale images.
     * Returns true if average pixel difference is small.
     */
    private boolean compareFaces(Mat a, Mat b) {
        if (a.size().width != b.size().width || a.size().height != b.size().height)
            return false;

        Mat diff = new Mat();
        Core.absdiff(a, b, diff);

        Scalar sum = Core.sumElems(diff);
        double total = sum.val[0];

        double avgDiff = total / (a.rows() * a.cols());

        // threshold – adjust depending on accuracy
        return avgDiff < 20.0;
    }

    /**
     * Encode face images to Base64.
     */
    private String encodeFaceData(List<Mat> faces) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeInt(faces.size());

            for (Mat face : faces) {
                MatOfByte mob = new MatOfByte();
                Imgcodecs.imencode(".png", face, mob);
                byte[] data = mob.toArray();

                oos.writeInt(data.length);
                oos.write(data);
            }

            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decode Base64 string into Mat face images.
     */
    private List<Mat> decodeFaceData(String encoded) {
        List<Mat> faces = new ArrayList<>();

        try {
            byte[] data = Base64.getDecoder().decode(encoded);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);

            int count = ois.readInt();

            for (int i = 0; i < count; i++) {
                int len = ois.readInt();
                byte[] faceBytes = new byte[len];
                ois.readFully(faceBytes);

                Mat face = Imgcodecs.imdecode(new MatOfByte(faceBytes), Imgcodecs.IMREAD_GRAYSCALE);
                if (!face.empty()) faces.add(face);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return faces;
    }
}
