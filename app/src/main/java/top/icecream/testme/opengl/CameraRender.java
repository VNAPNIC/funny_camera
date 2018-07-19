package top.icecream.testme.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.SparseArray;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import top.icecream.testme.camera.Camera;
import top.icecream.testme.main.MainActivity;
import top.icecream.testme.opengl.filter.AsciiFilterRender;
import top.icecream.testme.opengl.filter.FilterRender;
import top.icecream.testme.opengl.filter.GrayFilterRender;
import top.icecream.testme.opengl.filter.OriginalFilterRender;
import top.icecream.testme.opengl.filter.ReliefFilterRender;
import top.icecream.testme.opengl.sticker.GlassesStickerRender;
import top.icecream.testme.opengl.sticker.MaskStickerRender;
import top.icecream.testme.opengl.sticker.MoustacheStickerRender;
import top.icecream.testme.opengl.sticker.StickerRender;
import top.icecream.testme.opengl.utils.TextureHelper;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;
import static top.icecream.testme.camera.Camera.RAW_HEIGHT;
import static top.icecream.testme.camera.Camera.RAW_WIDTH;

/**
 * AUTHOR: 86417
 * DATE: 5/4/2017
 */

public class CameraRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "CameraRender";

    private final Context context;
    private final MainActivity.Callback callback;
    private int imageId;
    private Texture texture;
    private volatile FilterRender filterRender;
    private volatile StickerRender stickerRender;
    private SurfaceTexture cameraTexture;
    private GLSurfaceView glSV;
    private List<FilterRender> filterRenderList = new LinkedList<>();
    private List<StickerRender> stickerRenderList = new LinkedList<>();

    private volatile Camera camera;
    private final float[] projectionMatrix = new float[16];

    private volatile Bitmap bitmap;
    private volatile boolean isTakePicture = false;
    private int previewWidth;
    private int previewHeight;

    public CameraRender(Context context, GLSurfaceView glSV, MainActivity.Callback callback) {
        this.context = context;
        this.glSV = glSV;
        this.callback = callback;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        filterRenderList.add(new OriginalFilterRender(context));
        filterRenderList.add(new GrayFilterRender(context));
        filterRenderList.add(new ReliefFilterRender(context));
        filterRenderList.add(new AsciiFilterRender(context));

        stickerRenderList.add(null);
        stickerRenderList.add(new GlassesStickerRender(context));
        stickerRenderList.add(new MoustacheStickerRender(context));
        stickerRenderList.add(new MaskStickerRender(context));

        glClearColor(0f,0f,0f,1f);

        texture = new Texture();
        filterRender = filterRenderList.get(0);
        stickerRender = stickerRenderList.get(0);
        imageId = TextureHelper.genTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        cameraTexture = new SurfaceTexture(imageId);
        cameraTexture.setOnFrameAvailableListener(this);

        camera = new Camera(context);
        camera.setSurfaceTexture(cameraTexture);
        camera.openCamera();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.previewWidth = width;
        this.previewHeight = height;
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ?
                (float) width / (float) height:
                (float) height / (float) width;
        if (width > height) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        FilterRender filterRender = this.filterRender;
        StickerRender stickerRender = this.stickerRender;
        drawImage(filterRender);

        synchronized (camera.LOCK) {
            if (camera.getFaces() == null) {
                try {
                    camera.LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            drawSticker(stickerRender);
            camera.cleanFaces();
        }

        cameraTexture.updateTexImage();

        if (isTakePicture) {
            bitmap = createBitmapFromGLSurface(0, 0, previewWidth, previewHeight);
            callback.setImageView(bitmap);
            camera.close();
            isTakePicture = false;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        glSV.requestRender();
    }

    public void selectFilter(int position) {
        filterRender = filterRenderList.get(position);
    }

    public void selectSticker(int position) {
        stickerRender = stickerRenderList.get(position);
    }

    public void changCamera() {
        camera.changeCamera();
    }

    public void openCamera() {
        camera.openCamera();
    }

    public void takePicture() {
        isTakePicture = true;
    }

    public void savePicture() {
        saveBitmap(bitmap);
    }

    private void saveBitmap(Bitmap bitmap){
        if (bitmap == null) {
            callback.showMessage("图片为空");
            return;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator +"Me"+ File.separator+System.currentTimeMillis() + "image.jpg");
        try {
            boolean fileCreateSuccessful = file.createNewFile();
            if (!fileCreateSuccessful) {
                callback.showMessage("图片创建失败");
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            bytes.flush();
            bytes.close();
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            GLES20.glReadPixels(x, y, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            e.printStackTrace();
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    private void drawImage(FilterRender filterRender) {
        filterRender.useProgram();
        filterRender.bindTexture(imageId, projectionMatrix);
        filterRender.changeCameraDirection(camera);
        texture.bindData(this.filterRender);
        texture.draw();
    }

    private void drawSticker(StickerRender stickerRender) {
        if (stickerRender == null) {
            return;
        }

        SparseArray<Face> faces = camera.getFaces();
        if (faces == null || faces.size() == 0) {
            return;
        }

        PointF faceCenter = null;
        PointF leftEye = null;
        PointF rightEye = null;
        PointF noseBase = null;
        float[] rotationZMatrix = new float[16];
        float[] scratch = new float[16];
        float faceWidth;
        float faceHeight;

        Face face = faces.get(faces.keyAt(0));
        if (face.getIsSmilingProbability() > 0.7f) {
            callback.takePicture();
        }

        int cameraId = camera.getCameraId();
        int cameraState = cameraId == Camera.BACK ? 1 : -1;

        List<Landmark> landmarks = face.getLandmarks();
        for (Landmark landmark : landmarks) {
            switch (landmark.getType()) {
                case Landmark.LEFT_EYE:leftEye = rawPointToRealPoint(landmark.getPosition());break;
                case Landmark.RIGHT_EYE:rightEye = rawPointToRealPoint(landmark.getPosition());break;
                case Landmark.NOSE_BASE:noseBase = rawPointToRealPoint(landmark.getPosition());break;
            }
        }
        faceCenter = rawPointToRealPoint(face.getPosition());
        faceWidth = face.getWidth() / RAW_HEIGHT;
        faceHeight = face.getHeight() / RAW_HEIGHT;
        faceCenter.x = faceCenter.x - faceWidth;
        faceCenter.y = faceCenter.y - faceHeight;

        if (leftEye != null && rightEye != null && stickerRender instanceof GlassesStickerRender) {
            float centerX, centerY;
            float xRadius, yRadius;
            xRadius = faceWidth / 1.2f;
            yRadius = xRadius * 128 / 408;
            centerX = (leftEye.x + rightEye.x) / 2.0f;
            centerY = (leftEye.y + rightEye.y) / 2.0f;

            Matrix.setIdentityM(rotationZMatrix, 0);
            Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
            Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
            Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
            Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);

            stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
        }

        if (noseBase != null && stickerRender instanceof MoustacheStickerRender) {
            float centerX, centerY;
            float xRadius, yRadius;
            xRadius = faceWidth;
            yRadius = xRadius * 250 / 492;
            centerX = noseBase.x;
            centerY = noseBase.y;

            Matrix.setIdentityM(rotationZMatrix, 0);
            Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
            Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
            Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
            Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);

            stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
        }

        if (leftEye != null && rightEye != null && stickerRender instanceof MaskStickerRender) {
            float centerX, centerY;
            float xRadius, yRadius;
            xRadius = faceWidth * 1.2f;
            yRadius = xRadius * 1.0f;
            centerX = (leftEye.x + rightEye.x) / 2.0f;
            centerY = (leftEye.y + rightEye.y) / 2.0f;

            Matrix.setIdentityM(rotationZMatrix, 0);
            Matrix.translateM(rotationZMatrix, 0, centerX, centerY, 0);
            Matrix.rotateM(rotationZMatrix, 0, face.getEulerZ(), 0, 0, cameraState);
            Matrix.translateM(rotationZMatrix, 0, -centerX, -centerY, 0);
            Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, rotationZMatrix, 0);

            stickerRender.setPosition(new float[]{centerX, centerY}, xRadius, yRadius);
        }

        stickerRender.useProgram();
        stickerRender.setMatrix(scratch);
        stickerRender.bindTexture();

        texture.bindData(stickerRender);
        texture.draw();
    }

    private PointF rawPointToRealPoint(PointF rawPoint) {
        int cameraId = camera.getCameraId();
        PointF result = null;
        if (cameraId == Camera.FRONT) {
            result = new PointF(
                    1 - rawPoint.x / RAW_HEIGHT * 2.0f,
                    1.0f * RAW_WIDTH / RAW_HEIGHT - rawPoint.y / RAW_WIDTH * 2.0f * RAW_WIDTH / RAW_HEIGHT
            );
        } else if (cameraId == Camera.BACK) {
            result = new PointF(
                    rawPoint.x / RAW_HEIGHT * 2.0f - 1,
                    1.0f * RAW_WIDTH / RAW_HEIGHT - rawPoint.y / RAW_WIDTH * 2.0f * RAW_WIDTH / RAW_HEIGHT
            );
        }
        return result;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}