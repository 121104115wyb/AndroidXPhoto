package com.renogy.androidxphoto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.bumptech.glide.Glide;

import com.renogy.rphotolibrary.CameraHelper;
import com.renogy.rphotolibrary.SelectHelper;
import com.renogy.rphotolibrary.WaterMark;
import com.zhihu.matisse.Matisse;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_EXTERNAL_STORAGE1 = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_STORAGE1 = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ImageView mView;
    private CameraHelper cameraHelper;
    private SelectHelper selectHelper;
    private ImageView iv_photo1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        findViewById(R.id.btn_select_pic).setOnClickListener(this);
        findViewById(R.id.openCamera).setOnClickListener(this);
        selectHelper = new SelectHelper();
        cameraHelper = new CameraHelper(this);
        cameraHelper.setWaterMark(new WaterMark());
        cameraHelper.setCallBack(callBack);
        mView = findViewById(R.id.iv_photo);
        iv_photo1 = findViewById(R.id.iv_photo1);
        initPermission();


    }

    private void initPermission() {
//        RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions.request(Manifest.permission.CAMERA
//                , Manifest.permission.WRITE_EXTERNAL_STORAGE
//        );

//        new RxPermissions(this).request(Manifest.permission.CAMERA
//                , Manifest.permission.WRITE_EXTERNAL_STORAGE);


//        rxPermissions.request(
//                Manifest.permission.CAMERA
//                , Manifest.permission.WRITE_EXTERNAL_STORAGE
//                , Manifest.permission.READ_EXTERNAL_STORAGE
//        ).subscribe(new Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean aBoolean) throws Exception {
//                if (aBoolean) {
//                    //申请的权限全部允许
//                    Toast.makeText(MainActivity.this, "允许了权限!", Toast.LENGTH_SHORT).show();
//                } else {
//                    //只要有一个权限被拒绝，就会执行
//                    Toast.makeText(MainActivity.this, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied() {

            }
        }).request();
//        verifyStoragePermissions(this);
    }

    /**
     * 在对sd卡进行读写操作之前调用这个方法
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
////        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
////        }
//        //int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
////        if (permission1 != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            //ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE1, REQUEST_EXTERNAL_STORAGE1);
////        }
        PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE).request();
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_pic:
                selectPic();

                break;
            case R.id.openCamera:

                cameraHelper.openCamera();
                break;
            default:
                break;
        }
    }

    private final int REQUEST_CODE_CHOOSE_PHOTO_ALBUM = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == selectHelper.getResultCode() && resultCode == RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            List<Uri> pathList = Matisse.obtainResult(data);
//            for (Uri _Uri : pathList) {
//
////                System.out.println(_Uri.getPath());
//            }

            selectHelper.saveAndCompress(MainActivity.this, Matisse.obtainPathResult(data));


        }
        if (requestCode == cameraHelper.getCode() && resultCode == RESULT_OK) {
            cameraHelper.saveAndCompress();
        }
        if (requestCode == 100) {
            //selectPic();
            Log.i("TAG", "onActivityResult: -----requestCode==1");
        }
        if (requestCode == 2) {
            Log.i("TAG", "onActivityResult: -----requestCode==2");
        }
    }

    public CameraHelper.SaveImgCallBack callBack = new CameraHelper.SaveImgCallBack() {
        @Override
        public void onSuccess(String filePath, Uri uri) {
            Log.d("test", "onActivityResult: ----file:" + filePath);
            Log.d("test", "onActivityResult: ----uri:" + uri);
//            Glide.with(MainActivity.this).load(filePath).into(mView);

            Glide.with(MainActivity.this).load(uri).into(mView);


        }
    };



    private void selectPic() {

        selectHelper.setMaxSelectable(2);
        selectHelper.selectPic(MainActivity.this);
        selectHelper.setSaveCallBack(new SelectHelper.SaveCallBack() {
            @Override
            public void onFinish(List<String> file) {
                for (String s : file) {
                    Log.d("file", "onFinish: ----file:" + file.size() + "\n" + "s:" + s);
                }
                Glide.with(MainActivity.this).load(file.get(0)).into(mView);
            }
        });

//        Matisse.from(this)
//                //设置选择的类型
//                .choose(MimeType.ofImage(), false)
//                //限制只显示一种类型的数据
//                .showSingleMediaType(true)
//                // 使用相机，和 captureStrategy 一起使用
//                .capture(true)
//                //适配authority 与清单文件中provider中保持一致
//                .captureStrategy(new CaptureStrategy(true, "com.jsf.piccompresstest"))
////        R.style.Matisse_Zhihu (light mode)
////        R.style.Matisse_Dracula (dark mode)
//                //风格，支持自定义
//                .theme(R.style.Matisse_Dracula)
//                //使用顺序自增计数器
//                .countable(true)
//                //最大能选几个
//                .maxSelectable(3)
////                .addFilter()
////                .gridExpectedSize((int) getResources().getDimension(R.dimen.imageSelectDimen))
//                //选择器的方向
//                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                //选择图片时，的图片的预览图，与原来图片的比例
//                .thumbnailScale(0.8f)
//                //使用Glide加载器
//                .imageEngine(new GlideLoadEngine())
//                .originalEnable(true)
//                .maxOriginalSize(2)
//                .autoHideToolbarOnSingleTap(true)
//                //设置返回值标记
//                .forResult(REQUEST_CODE_CHOOSE_PHOTO_ALBUM);
    }
}
