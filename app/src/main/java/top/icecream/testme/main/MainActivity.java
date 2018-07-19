package top.icecream.testme.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.icecream.testme.R;
import top.icecream.testme.WeChatShareUtil;
import top.icecream.testme.info.AboutActivity;
import top.icecream.testme.main.utils.AnimatorHelper;
import top.icecream.testme.main.utils.PermissionHelper;
import top.icecream.testme.opengl.CameraRender;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.changeCameraBtn) Button changeCameraBtn;
    @BindView(R.id.infoBtn) Button infoBtn;
    @BindView(R.id.stickerBtn) Button stickerBtn;
    @BindView(R.id.takePictureBtn) Button takePictureBtn;
    @BindView(R.id.filterBtn) Button filterBtn;

    @BindView(R.id.stickRV) RecyclerView stickerRV;
    @BindView(R.id.filterRV) RecyclerView filterRV;

    @BindView(R.id.glSV) GLSurfaceView glSV;
    @BindView(R.id.imageView) ImageView imageView;
    private CameraRender cameraRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initCamera();
        initList();
    }

    @OnClick(R.id.changeCameraBtn) void changeCamera() {
        cameraRender.changCamera();
    }

    @OnClick(R.id.infoBtn) void openInfo(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.takePictureBtn) void takePicture() {
        changeBtnStateToPicture();
        cameraRender.takePicture();
    }

    @OnClick(R.id.stickerBtn) void showSticker() {
        AnimatorHelper.buttonVanish(filterBtn, stickerBtn, takePictureBtn);
        AnimatorHelper.listEmerge(stickerRV);
    }

    @OnClick(R.id.filterBtn) void showFilter(){
        AnimatorHelper.buttonVanish(filterBtn, stickerBtn, takePictureBtn);
        AnimatorHelper.listEmerge(filterRV);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(PermissionHelper.checkRequest(requestCode,grantResults)){
            initCameraRender();
        }
    }

    public interface Callback {

        void listVanish();

        void takePicture();

        void showMessage(String info);

        void setImageView(Bitmap bitmap);
    }


    private void initCamera() {
        if (!PermissionHelper.checkPermissions(this)){
            PermissionHelper.requestPermissions(this);
        }else{
            initCameraRender();
        }
    }

    private void initList() {
        GridLayoutManager filterGridLayoutManager = new GridLayoutManager(this, 1);
        filterGridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        filterRV.setLayoutManager(filterGridLayoutManager);
        filterRV.setAdapter(new FilterListAdapter(this, cameraRender, listVanish));

        GridLayoutManager stickerGridLayoutManager = new GridLayoutManager(this, 1);
        stickerGridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        stickerRV.setLayoutManager(stickerGridLayoutManager);
        stickerRV.setAdapter(new StickerListAdapter(this, cameraRender, listVanish));
    }

    private void initCameraRender() {
        glSV.setEGLContextClientVersion(2);
        cameraRender = new CameraRender(this, glSV, new Callback(){

            @Override
            public void listVanish() {

            }

            @Override
            public void takePicture() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.takePicture();
                    }
                });
            }

            @Override
            public void showMessage(final String info) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void setImageView(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        glSV.setRenderer(cameraRender);
        glSV.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private void changeBtnStateToPicture() {
        stickerBtn.setBackgroundResource(R.drawable.ic_left_arrow);
        takePictureBtn.setBackgroundResource(R.drawable.ic_download);
        filterBtn.setBackgroundResource(R.drawable.ic_share);
        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
                changeBtnStateToPreview();
                cameraRender.openCamera();
            }
        });
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
                changeBtnStateToPreview();
                cameraRender.savePicture();
                cameraRender.openCamera();
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatShareUtil.sharePhotoTo(MainActivity.this, cameraRender.getBitmap());
            }
        });
    }

    private void changeBtnStateToPreview() {
        stickerBtn.setBackgroundResource(R.drawable.ic_sentiment);
        takePictureBtn.setBackgroundResource(R.drawable.ic_ring);
        filterBtn.setBackgroundResource(R.drawable.ic_blur);
        stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSticker();
            }
        });
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });
    }

    private Callback listVanish = new Callback() {
        @Override
        public void listVanish() {
            if(filterRV.getVisibility() == View.VISIBLE){
                AnimatorHelper.buttonEmerge(filterBtn, stickerBtn, takePictureBtn);
                AnimatorHelper.listVanish(filterRV);
            } else if (stickerRV.getVisibility() == View.VISIBLE) {
                AnimatorHelper.buttonEmerge(filterBtn, stickerBtn, takePictureBtn);
                AnimatorHelper.listVanish(stickerRV);
            }
        }

        @Override
        public void takePicture() {}

        @Override
        public void showMessage(String info) {}

        @Override
        public void setImageView(Bitmap bitmap) {

        }
    };
}