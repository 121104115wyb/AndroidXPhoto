package com.renogy.rphotolibrary;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import android.util.Log;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;

/**
 * @author wyb
 * Date :2019/11/8 0008 14:29
 * Description: 选择图片器
 * Verson 1.1；date：2019/11/30
 * 1.添加是否显示原图的按钮选择
 * 1.添加自定义选择器
 */
public class SelectHelper {
    //是否使用相机
    private Boolean isCapture = false;
    //provider配置权限
    private String authorities = ProviderConfig.APP_COMMOM_PROVIDER;
    //最大选择图片个数
    private int maxSelectable = 9;
    //选择图片方向
    private int restrictOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    //缩放比
    private float thumbnailScale = 0.8f;
    //最大上传的图片的大小，单位M
    private int maxOriginalSize = 5;

    //预览是否支持自动隐藏标题栏
    private boolean autoHideToolbarOnSingleTap = true;
    //图片加载引擎
    private ImageEngine imageEngine;
    //返回的标识
    private int resultCode = 9999;
    //主题风格
    private int themeId = R.style.Matisse_Zhihu;
    //是否支持只显示一种类型的资源
    private boolean showSingleMediaType = true;
    private Boolean originalEnable = false;

    private Set<MimeType> mimeTypes = MimeType.ofImage();
    private Filter mFilter = null;
    private SaveCallBack saveCallBack;


    public void selectPic(Activity context) {
        Matisse.from(context)
                //设置选择的类型
                .choose(mimeTypes, false)
                //限制只显示一种类型的数据
                .showSingleMediaType(showSingleMediaType)
                // 使用相机，和 captureStrategy 一起使用
                .capture(isCapture)
                //适配authority 与清单文件中provider中保持一致
                .captureStrategy(new CaptureStrategy(true, authorities))
//        R.style.Matisse_Zhihu (light mode)
//        R.style.Matisse_Dracula (dark mode)
                //风格，支持自定义
                .theme(themeId)
                //使用顺序自增计数器
                .countable(true)
                //最大能选几个
                .maxSelectable(maxSelectable)
                .addFilter(mFilter == null ? imgFilter : mFilter)
//                .gridExpectedSize((int) getResources().getDimension(R.dimen.imageSelectDimen))
                //选择器的方向
                .restrictOrientation(restrictOrientation)
                //选择图片时，的图片的预览图，与原来图片的比例
                .thumbnailScale(thumbnailScale)
                //使用Glide加载器
                .imageEngine(new GlideLoadEngine())
                .originalEnable(originalEnable)
                .maxOriginalSize(maxOriginalSize)
                .autoHideToolbarOnSingleTap(autoHideToolbarOnSingleTap)
                //设置返回值标记
                .forResult(resultCode);
    }


    private Filter imgFilter = new Filter() {
        @Override
        protected Set<MimeType> constraintTypes() {
            return new HashSet<MimeType>() {{
                add(MimeType.PNG);
            }};
        }

        @Override
        public IncapableCause filter(Context context, Item item) {
            Long size = null;
            if (item != null) {
                size = item.size / 1024 / 1024;
            }
            return (size != null && size > 10) ? new IncapableCause(IncapableCause.DIALOG, context.getString(R.string.toast_select_notice_title), context.getString(R.string.toast_select_notice)) : null;
        }
    };

    public void setSaveCallBack(SaveCallBack saveCallBack) {
        this.saveCallBack = saveCallBack;
    }

    public void setmFilter(Filter mFilter) {
        this.mFilter = mFilter;
    }

    public Boolean getCapture() {
        return isCapture;
    }

    public void setCapture(Boolean capture) {
        isCapture = capture;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public int getMaxSelectable() {
        return maxSelectable;
    }

    public void setMaxSelectable(int maxSelectable) {
        this.maxSelectable = maxSelectable;
    }

    public int getRestrictOrientation() {
        return restrictOrientation;
    }

    public void setRestrictOrientation(int restrictOrientation) {
        this.restrictOrientation = restrictOrientation;
    }

    public float getThumbnailScale() {
        return thumbnailScale;
    }

    public void setThumbnailScale(float thumbnailScale) {
        this.thumbnailScale = thumbnailScale;
    }

    public int getMaxOriginalSize() {
        return maxOriginalSize;
    }

    public void setMaxOriginalSize(int maxOriginalSize) {
        this.maxOriginalSize = maxOriginalSize;
    }

    public boolean isAutoHideToolbarOnSingleTap() {
        return autoHideToolbarOnSingleTap;
    }

    public void setAutoHideToolbarOnSingleTap(boolean autoHideToolbarOnSingleTap) {
        this.autoHideToolbarOnSingleTap = autoHideToolbarOnSingleTap;
    }

    public ImageEngine getImageEngine() {
        return imageEngine;
    }

    public void setImageEngine(ImageEngine imageEngine) {
        this.imageEngine = imageEngine;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public boolean isShowSingleMediaType() {
        return showSingleMediaType;
    }

    public void setOriginalEnable(Boolean originalEnable) {
        this.originalEnable = originalEnable;
    }

    public Boolean getOriginalEnable() {
        return originalEnable;
    }

    public void setShowSingleMediaType(boolean showSingleMediaType) {
        this.showSingleMediaType = showSingleMediaType;
    }


    public void saveAndCompress(final Context mContext, @NonNull List<String> filePaths) {
        saveAndCompress(mContext, new WaterMark(), filePaths);
    }


    public void saveAndCompress(final Context mContext, WaterMark waterMark, @NonNull List<String> filePaths) {
        saveAndCompress(mContext, waterMark, filePaths, 640, 640, 90);
    }

    /**
     * 线程保存到本地，并返回filePath 和Uri
     */
    public void saveAndCompress(final Context mContext, final WaterMark waterMark, @NonNull final List<String> filePaths, final int compressWidth, final int compressHeight, final int quality) {
        if (filePaths.size() <= 0) return;
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<List<String>>() {
            @Override
            public List<String> doInBackground() throws Throwable {
                List<String> pathList = new ArrayList<>();
                for (String path : filePaths) {
                    File file = compress(mContext, waterMark, path, compressWidth, compressHeight, quality);
                    if (file != null) {
                        pathList.add(file.getPath());
                    }
                }
                return pathList;
            }

            @Override
            public void onSuccess(List<String> list) {
                if (saveCallBack != null) {
                    saveCallBack.onFinish(list);
                }
            }
        });
    }

    public interface SaveCallBack {
        void onFinish(List<String> file);
    }


    private File compress(final Context mContext, final WaterMark waterMark, String filepath, final int compressWidth, final int compressHeight, final int quality) {
        // 首先保存图片
        File appDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, System.currentTimeMillis() + ".jpg");

        //Bitmap bitmap = ImageUtils.compressBySampleSize(BitmapFactory.decodeFile(filepath), compressWidth, compressHeight);
        Log.d("filePath", "doInBackground: ----filePath:" + filepath);
        Bitmap qualityBitmap = ImageUtils.getBitmap(ImageUtils.compressByQuality(BitmapFactory.decodeFile(filepath), quality),0);
//        ImageUtils.getBitmap()
        if (waterMark != null) {
            float x = waterMark.getX();
            float y = waterMark.getY();
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
        if (ImageUtils.save(qualityBitmap, file, Bitmap.CompressFormat.JPEG)){
            return file;
        }else {
            return null;
        }
    }


}
