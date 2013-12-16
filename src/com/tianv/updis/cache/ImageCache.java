/**
 * @Title: ImageCache.java
 * @Package com.uucun.android.provider
 * @author Wang Baoxi 
 * @date 2011-11-25 下午09:32:04
 * @version V1.0
 */

package com.tianv.updis.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.melvin.android.base.task.AsyncMockTask;
import com.tianv.updis.Constant;
import com.tianv.updis.network.UUHttpClient;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.io.IOUtils;
import com.uucun.android.utils.newstring.StringUtils;

/**
 * 用于缓存图片的类
 *
 * @author Wang Baoxi
 * @ClassName: ImageCache
 * @date 2011-11-25 下午09:32:04
 */
public class ImageCache {
    /**
     * 连接方式标志位
     */
    private static final boolean USE_HTTP_CLIENT = false;

    /**
     * icon的标志位
     */
    public static final int APP_ICON = 1;

    /**
     * 屏幕截图的标志位
     */
    public static final int SCREENSHOT = 2;

    /**
     * BANNER的标志位
     */
    public static final int BANNER = 3;

    /**
     * 专题标志位
     */
    public static final int TOPIC = 4;

    public static final int UUBAO = 7;

    /**
     * 屏幕截图全屏的标志位
     */
    public static final int SCREENSHOT_FULL = 5;

    /**
     * GOOGLE play 首页专题图片
     */
    public static final int TOPIC_BANNER = 6;

    public static ImageCache imageCache = null;

    /**
     * 缓存目录
     */
    private static final String IMAGE_CACHE_DIRECTORY = "image";

    /**
     * 缓存的图像数据
     */
    private ConcurrentHashMap<String, SoftReference<Bitmap>> cacheMaps = null;

    /**
     * 缓存的Banner图像数据
     */
    private ConcurrentHashMap<String, Bitmap> cacheBanners = null;

    private Context context = null;

    ImageManager mImageManager = null;

    /**
     * 最大的任务数
     */
    private static final int MAX_TASK = 2;

    /**
     * 所有图片下载任务
     */
    private ConcurrentHashMap<String, ImageLoader> allTask = null;

    /**
     * 正在进行的任务
     */
    private ConcurrentHashMap<String, ImageLoader> downloadingTask = null;

    /**
     * 默认缓存目录名称
     */
    private String cacheDirName = "uucun";

    /**
     * url connection访问模式
     */
    public static int MODE_URL_CONNECTION = 1;

    /**
     * HttpClient访问模式
     */
    public static int MODE_HTTP_CLIENT = 2;

    /**
     * 访问服务器模式
     */
    // private static int mVisitMode = MODE_URL_CONNECTION;

    /**
     * Avoid OutOfMemoryError
     */
    private Options opts;

    /**
     * 缓存清理
     */
    public void clearCache() {
        imageCache = null;
        if (cacheMaps != null) {
            cacheMaps.clear();
        }
        cacheMaps = null;

        if (cacheBanners != null) {
            cacheBanners.clear();
        }
        cacheBanners = null;

        if (allTask != null) {
            allTask.clear();
        }
        allTask = null;

        if (downloadingTask != null) {
            downloadingTask.clear();
        }
        downloadingTask = null;
    }

    /**
     * 下载线程暂停
     */
    public void pauseDownLoadTask() {
        if (downloadingTask != null && downloadingTask.size() > 0) {
            for (int i = 0; i < downloadingTask.size(); i++) {

            }
        }
    }

    private ImageCache(Context context) {
        this.context = context;
        cacheMaps = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
        cacheBanners = new ConcurrentHashMap<String, Bitmap>();
        downloadingTask = new ConcurrentHashMap<String, ImageLoader>();
        allTask = new ConcurrentHashMap<String, ImageLoader>();

        opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inSampleSize = 2;
        mImageManager = ImageManager.getInstance(context);
    }

    public synchronized static ImageCache getInstance(Context context) {
        if (imageCache == null) {
            imageCache = new ImageCache(context);
        }
        return imageCache;
    }

    /**
     * @param url
     * @return 返回类型
     * @throws
     * @Title: getBannerFromMomery
     * @Description: 从缓存获取banner广告
     */
    public Bitmap getBannerFromMomery(String url) {
        String name = StringUtils.generateFileName(url);
        if (cacheBanners != null) {
            return cacheBanners.get(name);
        } else {
            return null;
        }
    }

    /**
     * 异步加载图
     *
     * @param url  URL
     * @param view 视图
     * @param flag APP_ICON,BANNER,TOPIC,SCREENSHOT,SCREENSHOT_FULL
     * @Title: asyncLoadImage
     */
    public void asyncLoadImage(String url, View view, int flag) {
        if (url == null) {
            return;
        }

        if (allTask != null || downloadingTask != null) {
            if (allTask.containsKey(url) || downloadingTask.containsKey(url)) {
                // Gallery循环展示，一屏显示三张缩略图，
                // 只有两张时，有一张会加载两次，所以不能直接返回。
                if (flag != SCREENSHOT) {
                    return;
                }
            }
        } else {
            return;
        }
        // load image or not， 设置界面，设置是加载图片显示还是不加载
        SharedStore st = new SharedStore(context, null);

        boolean isNoImage = st.getBoolean(Constant.KEY_NO_IMAGE, false);
        if (isNoImage && flag != BANNER) {
            cacheMaps.clear();
            return;
        }
        ImageLoader imageLoader = new ImageLoader(url, view, flag);
        addTask(url, imageLoader);
    }

    /**
     * 增加任务
     *
     * @param url
     * @param imageLoader
     * @Title: addTask
     */
    private void addTask(String url, ImageLoader imageLoader) {
        allTask.put(url, imageLoader);
        deteckTask();
    }

    /**
     * 删除任务
     *
     * @param url
     * @Title: removeTask
     */
    private void removeTask(String url) {
        if (downloadingTask != null) {
            downloadingTask.remove(url);
        }
        if (allTask != null) {
            allTask.remove(url);
        }
        deteckTask();
    }

    /**
     * 检测并执行任务
     *
     * @Title: deteckTask
     */
    private void deteckTask() {
        if (downloadingTask != null) {
            if (downloadingTask.size() >= MAX_TASK) {
                return;
            }
            Enumeration<String> en = allTask.keys();
            while (en.hasMoreElements()) {
                String key = en.nextElement();
                ImageLoader imageLoader = allTask.get(key);
                allTask.remove(key);
                if (imageLoader != null) {
                    downloadingTask.put(key, imageLoader);
                    imageLoader.execute();
                    break;
                }
            }
        } else {
            return;
        }
    }

    /**
     * 取得內存中的圖像
     *
     * @param url
     * @return
     * @Title: getBitmapFromMemory
     */
    public Bitmap getBitmapFromMemory(String url) {
        String name = StringUtils.generateFileName(url);
        Bitmap bb = null;
        /*** 从内存中取 **/
        if (cacheMaps != null) {
            SoftReference<Bitmap> reference = cacheMaps.get(name);
            if (reference != null) {
                bb = reference.get();
            } else {
                bb = cacheBanners.get(name);
                return bb;
            }

            if (bb != null) {
                cacheMaps.put(name, new SoftReference<Bitmap>(bb));
                return bb;
            }
        }
        return null;
    }

    /**
     * 从本地、或者网络上取得图片
     *
     * @param url
     * @param flag
     * @return
     * @Title: getBitmapFromCacheOrUrl
     */
    private Bitmap getBitmapFromCacheOrUrl(String url, int flag) {
        String name = StringUtils.generateFileName(url);
        Bitmap bb = null;
        if (cacheBanners != null) {
            /** 从缓存文件中取 **/
            bb = loadImageFromLocal(getCacheDirectory(), name);
            if (bb != null) {
                if (flag == BANNER) {
                    if (cacheBanners != null) {
                        cacheBanners.put(name, bb);
                    }
                } else {
                    if (cacheMaps != null) {
                        cacheMaps.put(name, new SoftReference<Bitmap>(bb));
                    }
                }
                return bb;
            }
            /** 从网络中加载 ***/
            bb = forceDownload(url, flag);
            if (bb != null) {
                if (flag == BANNER) {
                    if (cacheBanners != null) {
                        cacheBanners.put(name, bb);
                    }
                } else {
                    if (cacheMaps != null) {
                        cacheMaps.put(name, new SoftReference<Bitmap>(bb));
                    }
                }
                return bb;
            }
        }
        return null;

    }

    /**
     * 从本地缓存中加载图片
     *
     * @param cacheDirectory
     * @param fileName
     * @return
     * @Title: loadImageFromLocal
     */
    private Bitmap loadImageFromLocal(File cacheDirectory, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        final File file = new File(cacheDirectory, fileName);
        if (file.exists()) {
            InputStream stream = null;
            try {
                file.setLastModified(System.currentTimeMillis());
                stream = new FileInputStream(file);
                return BitmapFactory.decodeStream(stream, null, null);
            } catch (FileNotFoundException e) {
            } finally {
                IOUtils.closeStream(stream);
            }
        }
        return null;
    }

    /**
     * 强制下载
     *
     * @param url
     * @return
     * @Title: forceDownload2
     */
    private Bitmap forceDownload(String url, int flag) {
        if (url == null || TextUtils.isEmpty(url)) {
            return null;
        }
        return downloadImageFromUri(url, flag);
    }

    /**
     * 从URL中下载图片
     *
     * @param mUrl
     * @return
     * @Title: downloadImageFromUri
     */
    private Bitmap downloadImageFromUri(String mUrl, int flag) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float mScaledFactor = dm.density;
        Bitmap bitmap = null;
        String fileName = StringUtils.generateFileName(mUrl);

        try {
            byte[] bb = getImageFromServer(mUrl, context);
            if (bb == null)
            // if (bb == null || bb.length > 102400)
            {
                // Logger.w("NULLORBIG", mUrl);
                return null;
            }

            if (bb.length >= 61440) {
                bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length, opts);
            } else {
                bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
            }
        } catch (OutOfMemoryError e) {
            System.gc();
            return null;
        }

        switch (flag) {
            case APP_ICON:
                bitmap = restoreImageToCache(bitmap, (int) (48 * mScaledFactor),
                        (int) (48 * mScaledFactor), fileName);
                break;
            case SCREENSHOT:
                bitmap = restoreImageToCache(bitmap, (int) (133 * mScaledFactor),
                        (int) (222 * mScaledFactor), fileName);
                break;
            case BANNER:
                bitmap = restoreImageToCache(bitmap, (int) (150 * mScaledFactor),
                        (int) (93 * mScaledFactor), fileName);
                break;
            case TOPIC:
                bitmap = restoreImageToCache(bitmap, (int) (130 * mScaledFactor),
                        (int) (70 * mScaledFactor), fileName);
                break;
            case SCREENSHOT_FULL:
                bitmap = restoreImageToCache(bitmap, dm.widthPixels, dm.heightPixels, fileName);
                break;
            case TOPIC_BANNER:
                // bitmap = restoreImageToCache(bitmap, bitmap.getWidth(),
                // bitmap.getHeight(), fileName);
                bitmap = reDrawBitMap(bitmap, context, fileName);
                break;
        }
        return bitmap;
    }

    /**
     * @param 图片url
     * @return byte[]
     * @Description 获取图片流
     */
    public static byte[] getImageFromServer(String urlpath, Context context) {
        if (urlpath == null || urlpath.trim().equals(""))
            return null;

        if (!USE_HTTP_CLIENT) {
            URL url = null;
            HttpURLConnection con = null;
            InputStream is = null;
            ByteArrayOutputStream outStream = null;
            try {
                url = new URL(urlpath);
                con = (HttpURLConnection) url.openConnection();
                is = con.getInputStream();
                byte[] bs = new byte[1024];
                int len;
                outStream = new ByteArrayOutputStream();
                while ((len = is.read(bs)) != -1) {
                    outStream.write(bs, 0, len);
                }
                return outStream.toByteArray();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeStream(is);
                IOUtils.closeStream(outStream);
            }
        } else {
            HttpClient client = UUHttpClient.getInstance(context);
            HttpGet get = new HttpGet(urlpath);
            InputStream inputStream = null;
            ByteArrayOutputStream outStream = null;
            HttpEntity entity = null;
            try {
                HttpResponse httpResponse = client.execute(get);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    entity = httpResponse.getEntity();
                    inputStream = entity.getContent();
                    outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4 * 1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    return outStream.toByteArray();
                }
            } catch (Exception e) {
                Logger.w("Download Image Error", e.getMessage() + " " + urlpath + " ");
            } finally {
                IOUtils.closeStream(inputStream);
                IOUtils.closeStream(outStream);
                if (entity != null)
                    try {
                        entity.consumeContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                UUHttpClient.closeHttpClient(client);
            }
        }
        return null;
    }

    /**
     * 存储图片
     *
     * @param bitmap
     * @param width
     * @param height
     * @param imageName
     * @return
     * @Title: restoreImageToCache
     */
    private Bitmap restoreImageToCache(Bitmap bitmap, int width, int height, String imageName) {
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            addImageToLocalCache(getCacheDirectory(), imageName, bitmap);
        }
        return bitmap;
    }

    /**
     * @param @param  bitmap
     * @param @param  context
     * @param @return 设定文件
     * @return Bitmap 返回类型
     * @throws
     * @Title: reDrawBitMap
     * @Description: 等比缩放图片
     */
    public Bitmap reDrawBitMap(Bitmap bitmap, Context context, String imageName) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int rHeight = dm.heightPixels;
        int rWidth = dm.widthPixels;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float zoomScale;
        if (rWidth / rHeight > width / height) {// 以高为准
            zoomScale = ((float) rHeight) / height;
        } else { // if(rWidth/rHeight<width/height)//以宽为准
            zoomScale = ((float) rWidth) / width;
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(zoomScale, zoomScale);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
        addImageToLocalCache(getCacheDirectory(), imageName, resizedBitmap);
        return resizedBitmap;
    }

    /**
     * 取得缓存目录
     *
     * @return
     * @Title: getCacheDirectory
     */
    public File getCacheDirectory() {
        SharedStore st = new SharedStore(context, null);
        cacheDirName = st.getString(Constant.CACHE_DIR_KEY, "tianv");
        StringBuilder imageCache = new StringBuilder(cacheDirName);
        imageCache.append(File.separator);
        imageCache.append(IMAGE_CACHE_DIRECTORY);
        File cacheDirectory = IOUtils.getExternalFile(imageCache.toString());
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        return cacheDirectory;
    }

    /**
     * 将文件写入到缓存中
     *
     * @param cacheDirectory
     * @param fileName
     * @param bitmap
     * @return
     * @Title: addImageToLocalCache
     */
    private boolean addImageToLocalCache(File cacheDirectory, String fileName, Bitmap bitmap) {

        File coverFile = new File(cacheDirectory, fileName);

        FileOutputStream out = null;
        try {
            if (!coverFile.exists())
                coverFile.createNewFile();
            out = new FileOutputStream(coverFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            String s = IOUtils.exception2String(e);
            Logger.w("ImageCache", "" + s);
            return false;
        } finally {
            IOUtils.closeStream(out);
        }
        return true;
    }

    /**
     * 异步加载图片从本地或者网络
     *
     * @author Wang Baoxi
     * @ClassName: ImageLoader
     * @date 2011-11-25 下午09:38:08
     */
    public class ImageLoader extends AsyncMockTask<Void, String, Bitmap> {

        /**
         * 视图
         */
        private View view = null;

        // private int resId = -1;

        // private Context mContext;
        /**
         * 下载地址
         */
        private String url = null;

        /**
         * 缩放标志，ICON ,BANNER,SCREENSHOT
         */
        private int flag;

        // private String mType=null;

        public ImageLoader(String url, View view, int flag) {
            this.view = view;
            this.url = url;
            this.flag = flag;
        }

        protected Bitmap doInBackground(Void... params) {
            return getBitmapFromCacheOrUrl(url, flag);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && view != null) {
                ImageView iv = (ImageView) view.findViewWithTag(url);
                if (iv != null) {
                    if (flag == BANNER) {
                        BitmapDrawable drawable = new BitmapDrawable(result);
                        iv.setBackgroundDrawable(drawable);
                    } else {
                        iv.setImageBitmap(result);
                    }
                }
            }
            removeTask(url);
        }

    }

    ;
}
