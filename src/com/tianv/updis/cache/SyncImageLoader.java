
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import com.tianv.updis.Constant;
import com.uucun.android.logger.Logger;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.io.IOUtils;
import com.uucun.android.utils.newstring.StringUtils;

public class SyncImageLoader {

    private Object lock = new Object();

    private boolean mAllowLoad = true;

    private boolean firstLoad = true;

    private int mStartLoadLimit = 0;

    private int mStopLoadLimit = 0;

    final Handler handler = new Handler();

    /**
     * 连接方式标志位
     */
    private static final boolean USE_HTTP_CLIENT = false;

    private Context mContext;

    /**
     * Avoid OutOfMemoryError
     */
    private Options opts;

    /**
     * 默认缓存目录名称
     */
    private String cacheDirName = "updis";

    // private Bitmap mBp;

    /**
     * 缓存目录
     */
    private static final String IMAGE_CACHE_DIRECTORY = "image";

    public ConcurrentHashMap<String, SoftReference<Bitmap>> imageCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

    public SyncImageLoader(Context context) {
        super();
        mContext = context;
        opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inSampleSize = 2;
    }

    public interface OnImageLoadListener {
        public void onImageLoad(Integer t, Bitmap bitmap, View parent);

        public void onError(Integer t, View parent);
    }

    public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
        if (startLoadLimit > stopLoadLimit) {
            return;
        }
        mStartLoadLimit = startLoadLimit;
        mStopLoadLimit = stopLoadLimit;
    }

    public void restore() {
        mAllowLoad = true;
        firstLoad = true;
    }

    public void lock() {
        mAllowLoad = false;
        firstLoad = false;
    }

    public void unlock() {
        mAllowLoad = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void loadImage(Integer t, String imageUrl, OnImageLoadListener listener,
                          final View parent, final Context context) {
        final OnImageLoadListener mListener = listener;
        final String mImageUrl = imageUrl;
        final Integer mt = t;
        SharedStore st = new SharedStore(context, null);
        boolean isNoImage = st.getBoolean(Constant.KEY_NO_IMAGE, false);
        if (isNoImage) {
            imageCache.clear();
            return;
        }
        final Bitmap d = getBitmapFromMemory(mImageUrl);
        if (d != null) {
            handler.post(new Runnable() {
                public void run() {
                    mListener.onImageLoad(mt, d, parent);
                }
            });
        }
        // }
        new Thread(new Runnable() {
            public void run() {
                if (!mAllowLoad) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                if (mAllowLoad && firstLoad) {
                    loadImage(mImageUrl, mt, mListener, parent, context);
                }
                if (mAllowLoad && mt <= mStopLoadLimit + 1 && mt >= mStartLoadLimit - 1) {
                    loadImage(mImageUrl, mt, mListener, parent, context);
                }
            }

        }).start();
    }

    private void loadImage(final String mImageUrl, final Integer mt,
                           final OnImageLoadListener mListener, final View parent, final Context context) {
        final Bitmap d = getBitmapFromMemory(mImageUrl);
        if (d != null) {
            handler.post(new Runnable() {
                public void run() {
                    if (mAllowLoad) {
                        mListener.onImageLoad(mt, d, parent);
                    }
                }
            });
            return;
        }
        try {
            final Bitmap mBp = getBitmapFromCacheOrUrl(mImageUrl);
            handler.post(new Runnable() {
                public void run() {
                    if (mAllowLoad) {
                        mListener.onImageLoad(mt, mBp, parent);
                    }
                }
            });
        } catch (Exception e) {
            handler.post(new Runnable() {
                public void run() {
                    mListener.onError(mt, parent);
                }
            });
            e.printStackTrace();
        }
    }

    /**
     * 从本地、或者网络上取得图片
     *
     * @param url
     * @return
     * @Title: getBitmapFromCacheOrUrl
     */
    private Bitmap getBitmapFromCacheOrUrl(String url) {
        String name = StringUtils.generateFileName(url);
        Bitmap bb = null;
        /** 从缓存文件中取 **/
        bb = loadImageFromLocal(getCacheDirectory(), name);
        if (bb != null) {
            imageCache.put(name, new SoftReference<Bitmap>(bb));
            return bb;
        }
        /** 从网络中加载 ***/
        bb = forceDownload(url);
        if (bb != null) {

            return bb;
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
    private Bitmap forceDownload(String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            return null;
        }
        return downloadImageFromUri(url);
    }

    /**
     * 从URL中下载图片
     *
     * @param mUrl
     * @return
     * @Title: downloadImageFromUri
     */
    private Bitmap downloadImageFromUri(String mUrl) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float mScaledFactor = dm.density;
        Bitmap bitmap = null;
        String fileName = StringUtils.generateFileName(mUrl);
        try {
            byte[] bb = getImageFromServer(mUrl, mContext);
            if (bb == null) {
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

        bitmap = restoreImageToCache(bitmap, (int) (bitmap.getWidth() * mScaledFactor),
                (int) (bitmap.getHeight() * mScaledFactor), fileName);


        return bitmap;
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
     * 取得缓存目录
     *
     * @return
     * @Title: getCacheDirectory
     */
    public File getCacheDirectory() {
        SharedStore st = new SharedStore(mContext, null);
        cacheDirName = st.getString(Constant.CACHE_DIR_KEY, "updis");
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
     * @param urlpath
     * @param context
     * @return byte[]
     * @Description 获取图片流
     */
    public static byte[] getImageFromServer(String urlpath, Context context) {
        if (urlpath == null)
            return null;

        URL url = null;
        HttpURLConnection con = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        try {
            url = new URL(urlpath);
            con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Cookie", Constant.COOKIES);
            Logger.i("Constant Cookie", Constant.COOKIES);
            is = con.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            outStream = new ByteArrayOutputStream();
            while ((len = is.read(bs)) != -1) {
                outStream.write(bs, 0, len);
            }
            return outStream.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(is);
            IOUtils.closeStream(outStream);
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
        SoftReference<Bitmap> reference = imageCache.get(name);
        if (reference != null) {
            bb = reference.get();
        }
        if (bb != null) {
            imageCache.put(name, new SoftReference<Bitmap>(bb));
            return bb;
        }
        return null;
    }
}
