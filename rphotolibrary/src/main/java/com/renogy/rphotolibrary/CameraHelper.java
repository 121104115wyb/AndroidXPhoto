package com.renogy.rphotolibrary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.provider.MediaStore;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

/**
 * @author wyb
 * Date :2019/11/8 0008 9:52
 * Description: 打开相机，拍照的工具类 打开相机器
 * 适配 android 10
 */
public class CameraHelper {
    /**
     * 是否是Android 10以上手机
     */
    private Activity mContext;

    private int code = 10000;
    /**
     * your provider android:authorities
     * 你的清单文件中的provider中的authorities
     */
    private String authorities = ProviderConfig.APP_COMMOM_PROVIDER;
    /**
     * 图片名称
     */
    private String imgName = "";
    /**
     * 压缩宽度
     */
    private int compressWidth = 640;
    /**
     * 压缩高度
     */
    private int compressHeight = 640;
    /**
     * 质量压缩
     */
    private int quality = 90;
    /**
     * 水印
     */
    private WaterMark waterMark = null;

//    /**
//     * 原始的返回的uri
//     */
//    private Uri mCameraUri = null;
    /**
     * 临时缓存的图片路径
     */
    private String filePath = "";
    /**
     * 最终保存的图片地址
     */
    private String resultFilePath = "";

    private SaveImgCallBack callBack;

    public void setCallBack(SaveImgCallBack callBack) {
        this.callBack = callBack;
    }

    public int getCode() {
        return code;
    }

    public String getFilePath() {
        return filePath;
    }


    public void setCode(int code) {
        this.code = code;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public CameraHelper(@NonNull Activity context) {
        this.mContext = context;
    }

    public void setCompressWidth(int compressWidth) {
        this.compressWidth = compressWidth;
    }

    public void setCompressHeight(int compressHeight) {
        this.compressHeight = compressHeight;
    }

    public void setQuality(@IntRange(from = 0, to = 100) int quality) {
        this.quality = quality;
    }

    public void setWaterMark(WaterMark waterMark) {
        this.waterMark = waterMark;
    }

    public CameraHelper(@NonNull Activity context, String authorities, String filePath, int code) {
        this.mContext = context;
        if (!TextUtils.isEmpty(authorities)) {
            this.authorities = authorities;
        }
        if (!TextUtils.isEmpty(filePath)) {
            this.filePath = filePath;
        }
        if (code != 0) {
            this.code = code;
        }
    }

    //打开相机
    public void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;
            if (Build.VERSION.SDK_INT >= 29) {
                // 适配android 10
                photoUri = createImageUri();
                filePath = new File(photoUri.getPath()).getAbsolutePath();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    filePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(mContext, authorities, photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }
            //你可以在activity的回掉中接收Uri自己进行处理
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                mContext.startActivityForResult(captureIntent, code);
            }
        }
    }


    /**
     * 创建保存图片的文件
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     *
     * @return 图片的uri
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return mContext.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }


    /**
     * 线程保存到本地，并返回filePath 和Uri
     */
    public void saveAndCompress() {

        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Uri>() {
            @Override
            public Uri doInBackground() throws Throwable {
                // 首先保存图片
                File appDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath());
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                File file;
                if (TextUtils.isEmpty(imgName)) {
                    file = new File(appDir, System.currentTimeMillis() + ".jpg");
                } else {
                    file = new File(appDir, imgName);
                }
                Bitmap bitmap = ImageUtils.compressBySampleSize(BitmapFactory.decodeFile(filePath), compressWidth, compressHeight);
                Log.d("filePath", "doInBackground: ----filePath:" + filePath);
                Bitmap qualityBitmap =  ImageUtils.getBitmap(ImageUtils.compressByQuality(bitmap, quality),0);

                if (waterMark != null) {
                    float x = waterMark.getX();
                    float y = waterMark.getY();
//                    int quaBitmapY = qualityBitmap.getHeight();
//                    int quaBitmapX = qualityBitmap.getWidth();
                    switch (waterMark.getLoacation()) {
                        case 1:
                            x = 8f;
                            y = 8f;
                            break;
                        case 2:
                            x = 8f;
                            y = 4 * qualityBitmap.getHeight() / 5f;
                            break;
                        case 3:
                            x = qualityBitmap.getWidth() / 2f;
                            y = 8f;
                            break;
                        case 4:
                            x = qualityBitmap.getWidth() / 2f;
                            y = 4 * qualityBitmap.getHeight() / 5f;
                            break;
                        default:
                            break;
                    }
                    Log.d("size", "doInBackground: ---x:" + x + "--y:" + y + "\n" + "getHeight:" + qualityBitmap.getHeight() + "---getWidth" + qualityBitmap.getWidth());
                    qualityBitmap = ImageUtils.addTextWatermark(qualityBitmap, waterMark.getImgWaterDate(), SizeUtils.sp2px(waterMark.getTextSize()), waterMark.getTextColor(), x, y);
                }
                ImageUtils.save(qualityBitmap, file, Bitmap.CompressFormat.JPEG);
                FileUtils.delete(filePath);
                resultFilePath = file.toString();
                Log.d("filePath", "doInBackground: ----resultFilePath:" + resultFilePath);
                return Uri.fromFile(new File(resultFilePath));
            }

            @Override
            public void onSuccess(Uri uri) {
                if (callBack != null) {
                    callBack.onSuccess(resultFilePath, uri);
                }
            }
        });
    }

    public interface SaveImgCallBack {
        void onSuccess(String filePath, Uri uri);
    }


    void recycle() {
        if (mContext != null) {
            mContext = null;
        }
    }


}
