import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class FaceRecognitionUtil {
    private static final String CASCADE_FILE = "haarcascade_frontalface_default.xml";
    private static final String DATASET_PATH = "faces/";

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Capture and save 3 face images for a user
    public void registerFace(int userId) {
        CascadeClassifier detector = new CascadeClassifier(CASCADE_FILE);
        new File(DATASET_PATH).mkdirs();
        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) {
            System.out.println("Camera not found");
            return;
        }

        Mat frame = new Mat();
        int count = 0;
        System.out.println("Capturing face… Look at the camera");

        while (count < 3) {
            cam.read(frame);
            Mat gray = new Mat();
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            MatOfRect faces = new MatOfRect();
            detector.detectMultiScale(gray, faces);
            for (Rect r : faces.toArray()) {
                Mat face = new Mat(gray, r);
                Imgcodecs.imwrite(DATASET_PATH + userId + "_" + count + ".png", face);
                count++;
                System.out.println("Saved face " + count);
            }
        }
        cam.release();
        System.out.println("Face registration done");
    }

    // Recognize a face and return the userId of the matched user
    public int recognizeFace() {
        CascadeClassifier detector = new CascadeClassifier(CASCADE_FILE);
        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) {
            System.out.println("Camera not found");
            return -1;
        }

        Mat frame = new Mat();
        int recognizedUserId = -1;

        while (recognizedUserId == -1) {
            cam.read(frame);
            Mat gray = new Mat();
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            MatOfRect faces = new MatOfRect();
            detector.detectMultiScale(gray, faces);

            for (Rect r : faces.toArray()) {
                Mat face = new Mat(gray, r);
                File folder = new File(DATASET_PATH);
                for (File f : folder.listFiles()) {
                    if (f.isFile() && f.getName().endsWith(".png")) {
                        String[] parts = f.getName().split("_");
                        int fileUserId = Integer.parseInt(parts[0]);
                        Mat saved = Imgcodecs.imread(f.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                        if (saved.empty()) continue;
                        Mat resized = new Mat();
                        Imgproc.resize(face, resized, saved.size());
                        Core.absdiff(saved, resized, resized);
                        Scalar s = Core.sumElems(resized);
                        double diff = s.val[0] + s.val[1] + s.val[2];
                        if (diff < 1.5e6) { // threshold, adjust if needed
                            recognizedUserId = fileUserId;
                            break;
                        }
                        resized.release();
                        saved.release();
                    }
                }
            }
        }

        cam.release();
        return recognizedUserId; // -1 if no face matched
    }
}
