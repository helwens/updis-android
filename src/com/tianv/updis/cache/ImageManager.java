
package com.tianv.updis.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tianv.updis.Constant;
import com.uucun.android.sharedstore.SharedStore;
import com.uucun.android.utils.io.IOUtils;
import com.uucun.android.uunetwork.httptools.CheckRequestState;

/**
 * Image Utility class used to manage the image resources, such as download,
 * cache, deletion.
 *
 * @author
 */
public class ImageManager {

    private Context mContext;

    private String mCachePath;

    private static ImageManager mInstance;

    private static final String LOG_TAG = "ImageManager";

    private static final String IMAGE_CACHE_DIRECTORY = "image";

    private static final int HARD_CACHE_CAPACITY = 40;

    private static final FastBitmapDrawable NULL_DRAWABLE = new FastBitmapDrawable(null);

    private static final HashMap<String, SoftReference<FastBitmapDrawable>> sArtCache = new LinkedHashMap<String, SoftReference<FastBitmapDrawable>>(
            HARD_CACHE_CAPACITY / 2, 0.75f, true) {
        /**
         *
         */
        private static final long serialVersionUID = 1441081382891204069L;

        protected boolean removeEldestEntry(
                Map.Entry<String, SoftReference<FastBitmapDrawable>> eldest) {
            if (size() > HARD_CACHE_CAPACITY / 2) {
                return true;
            } else
                return false;
        }
    };

    private ImageManager(Context context) {
        mContext = context;
        mCachePath = context.getPackageName();
    }

    /**
     * Get a singleton instance of ImageManager.
     *
     * @param context
     * @return
     */
    public static synchronized ImageManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ImageManager(context);
        }
        return mInstance;
    }

    /**
     * Retrieves a drawable from the resources cache, identified by the
     * specified id. If the drawable does not exist in the cache, it is loaded
     * and added to the cache. If the drawable cannot be added to the cache, the
     * specified default drwaable is returned.
     *
     * @param name         The name of the drawable to retrieve
     * @param defaultCover The default drawable returned if no drawable can be
     *                     found that matches the id
     * @return The drawable identified by id or defaultCover
     */
    public FastBitmapDrawable getCachedImage(String name, FastBitmapDrawable defaultImage) {
        if (TextUtils.isEmpty(name)) {
            return defaultImage;
        }
        FastBitmapDrawable drawable = getCachedImage(name, getCacheDirectory());
        return drawable == NULL_DRAWABLE ? defaultImage : drawable;
    }

    /**
     * Retrieve the boot image from local storage and make sure the boot image
     * can satisfy the landscape and portrait.
     *
     * @param isOL Determine whether the device's orientation is landscape or
     *             portrait.
     * @return drawable
     */
    public FastBitmapDrawable getCachedBootImage() {
        FastBitmapDrawable drawable = null;
        Bitmap bitmap = loadBootImageFromLocal();
        if (bitmap != null) {
            drawable = new FastBitmapDrawable(bitmap);
        }
        return drawable;
    }

    private FastBitmapDrawable getCachedImage(String name, File cacheDirectory) {
        FastBitmapDrawable drawable = null;
        SoftReference<FastBitmapDrawable> reference = sArtCache.get(name);
        if (reference != null) {
            drawable = reference.get();
        }
        if (drawable == null) {
            final Bitmap bitmap = loadImageFromLocal(cacheDirectory, name);
            if (bitmap != null) {
                drawable = new FastBitmapDrawable(bitmap);
            } else {
                drawable = NULL_DRAWABLE;
            }
            sArtCache.put(name, new SoftReference<FastBitmapDrawable>(drawable));
        }
        return drawable;
    }

    public FastBitmapDrawable getImageFromCache(File cacheDirectory, String name) {

        if (TextUtils.isEmpty(name)) {
            return null;
        }
        FastBitmapDrawable drawable = null;
        SoftReference<FastBitmapDrawable> reference = sArtCache.get(name);
        if (reference != null) {
            drawable = reference.get();
        }
        if (drawable == null) {
            final Bitmap bitmap = loadImageFromLocal(cacheDirectory, name);
            if (bitmap != null) {
                drawable = new FastBitmapDrawable(bitmap);
                sArtCache.put(name, new SoftReference<FastBitmapDrawable>(drawable));
            }
        } else {
            sArtCache.remove(name);
            sArtCache.put(name, reference);
        }

        return drawable;
    }

    /**
     * Load LOGO image from network if it does not exist in the cache, and then
     * store it.
     *
     * @param logoUrl The URL to load the LOGO image.
     */
    public void loadAndStoreLogoImage(String logoUrl) {
        if (TextUtils.isEmpty(logoUrl)) {
            return;
        }

        SharedStore sharedStore = new SharedStore(mContext, null);

        String logoUrlSaved = sharedStore.getString("logo_url", null);

        File logoCacheDir = mContext.getCacheDir();
        String imageName = "logo";
        File imageFile = new File(logoCacheDir, imageName);
        FastBitmapDrawable drawable = getImageFromCache(logoCacheDir, imageName);
        if (drawable != null && logoUrl.equals(logoUrlSaved)) {
            // same url and downloaded
            return;
        }

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (172 * metrics.density);
        int height = (int) (44 * metrics.density);

        Bitmap logoImage = loadImage(logoUrl, true);
        if (logoImage != null) {
            imageFile.delete();
            putImageToCache(logoImage, width, height, imageName, logoCacheDir, true);
            sharedStore.putString("logo_url", logoUrl);
        }
    }

    /**
     * Load Boot image from network if it does not exist in the cache, and then
     * store it.
     *
     * @param bootUrl The URL to load the image.
     */
    public void loadAndStoreBootImage(String bootUrl) {
        if (TextUtils.isEmpty(bootUrl)) {
            return;
        }

        SharedStore sharedStore = new SharedStore(mContext, null);
        String bootUrlSaved = sharedStore.getString("boot_url", null);

        File bootCacheDirectory = mContext.getCacheDir();
        String imageName = "boot";
        File imageFile = new File(bootCacheDirectory, imageName);
        FastBitmapDrawable drawable = getImageFromCache(bootCacheDirectory, imageName);
        if (bootUrl.equals(bootUrlSaved) && drawable != null) {
            // same url and downloaded
            return;
        }

        int orientation = mContext.getResources().getConfiguration().orientation;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = metrics.heightPixels;
            height = metrics.widthPixels;
        }

        Bitmap bootImage = loadImage(bootUrl, true);
        if (bootImage != null) {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            putBootImageToCache(bootImage, width, height, imageName, bootCacheDirectory);
            sharedStore.putString("boot_url", bootUrl);
        }
    }

    /**
     * Scale the bitmap if necessary, and then store the bitmap to memory and
     * local storage.
     *
     * @param bitmap         The bitmap need to be stored.
     * @param width          The width of the bitmap.
     * @param height         The height of the bitmap.
     * @param imageName      The image name.
     * @param cacheDirectory The cache directory used to cache the image.
     * @param scaled         Determine whether the bitmap needs to be scaled or not.
     */
    private void putImageToCache(Bitmap bitmap, int width, int height, String imageName,
                                 File cacheDirectory, boolean scaled) {
        if (bitmap != null) {
            if (scaled) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            addImageToLocalCache(cacheDirectory, imageName, bitmap);
            sArtCache.put(imageName, new SoftReference<FastBitmapDrawable>(new FastBitmapDrawable(
                    bitmap)));
        }
    }

    /**
     * Scales the default logo image in the drawable package.
     *
     * @return the scaled bitmap
     */
    public Bitmap scaleLogoBitmap(int drawableId) {
        final float scaledFactor = mContext.getResources().getDisplayMetrics().density;
        Bitmap logoBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(mContext.getResources(), drawableId),
                (int) (172 * scaledFactor), (int) (44 * scaledFactor), true);
        return logoBitmap;
    }

    FastBitmapDrawable restoreImageToCache(Bitmap bitmap, int width, int height, String imageName) {
        FastBitmapDrawable drawable = null;
        if (bitmap != null) {
            synchronized (sArtCache) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                addImageToLocalCache(getCacheDirectory(), imageName, bitmap);
                drawable = new FastBitmapDrawable(bitmap);
                sArtCache.put(imageName, new SoftReference<FastBitmapDrawable>(drawable));
            }
        }
        return drawable;
    }

    /**
     * Scale the boot image by the screen size, two images will be scaled
     * whenever the device's orientation is landscape or portrait, then cache
     * these scaled images to memory and local storage.
     *
     * @param bitmap         The bitmap need to be scaled.
     * @param width          The width of the bitmap.
     * @param height         The height of the bitmap.
     * @param imageName      The image name.
     * @param cacheDirectory The cache directory used to cache the image.
     * @param isOL           Determine whether the device's orientation is landscape or
     *                       portrait.
     */
    private void putBootImageToCache(Bitmap bitmap, int width, int height, String imageName,
                                     File cacheDirectory) {
        if (bitmap != null) {
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, width, height, true);
            addImageToLocalCache(cacheDirectory, imageName, bitmap1);
            sArtCache.put(imageName, new SoftReference<FastBitmapDrawable>(new FastBitmapDrawable(
                    bitmap)));
        }
    }

    /**
     * Get the cache directory of common image, such as icon, screenshot and
     * banner images.
     *
     * @return cacheDirectory
     */
    public File getCacheDirectory() {
        StringBuilder imageCache = new StringBuilder(mCachePath);
        imageCache.append(File.separator);
        imageCache.append(IMAGE_CACHE_DIRECTORY);
        File cacheDirectory = IOUtils.getExternalFile(imageCache.toString());
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        return cacheDirectory;
    }

    /**
     * Store and compress the bitmap.
     *
     * @param cacheDirectory The cache directory used to cache the image.
     * @param fileName       The name of image
     * @param bitmap         The bitmap need to be stored.
     * @return true if stores successfully.
     */
    private boolean addImageToLocalCache(File cacheDirectory, String fileName, Bitmap bitmap) {
        File coverFile = new File(cacheDirectory, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(coverFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            IOUtils.closeStream(out);
        }

        return true;
    }

    /**
     * Restore image from local storage by specified name.
     *
     * @param cacheDirectory The cache directory used to cache the image.
     * @param fileName       The name of image
     * @return local bitmap
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
                // Ignore
            } finally {
                IOUtils.closeStream(stream);
            }
        }
        return null;
    }

    /**
     * Restore portrait image from local storage.
     *
     * @param cacheDirectory The cache directory used to cache the image.
     * @return boot bitmap
     */

    private Bitmap loadBootImageFromLocal() {
        File bootCacheDirectory = mContext.getCacheDir();
        String imageName = "boot";
        File bootFile = new File(bootCacheDirectory, imageName);
        InputStream stream = null;
        try {
            stream = new FileInputStream(bootFile);
            return BitmapFactory.decodeStream(stream, null, null);
        } catch (FileNotFoundException e) {
            // Ignore
        } finally {
            IOUtils.closeStream(stream);
        }
        return null;
    }

    /**
     * Loads an image from the specified URL with the specified cookie.
     *
     * @param url The URL of the image to load.
     * @return The image at the specified URL or null if an error occurred.
     */
    Bitmap loadImage(String url, boolean isSkia) {
        try {
            URL fetchUrl = new URL(url);
            Proxy proxy = CheckRequestState.checkUrlConnectionProxy(mContext);
            HttpURLConnection urlConnection = null;
            if (proxy != null) {
                urlConnection = (HttpURLConnection) fetchUrl.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) fetchUrl.openConnection();
            }
            urlConnection.setConnectTimeout(20 * 1000);
            urlConnection.setRequestProperty("User-Agent", "Android_CMS Client");
            urlConnection.connect();

            final int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.w("ImageDownloader", "Error code " + statusCode
                        + " while retrieving bitmap from " + fetchUrl.toString());
                return null;
            }
            Bitmap bitmap = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
                if (isSkia) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    inputStream = new BufferedInputStream(inputStream, 6 * 1024);
                    final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                    outputStream = new BufferedOutputStream(dataStream, 6 * 1024);
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.flush();
                    final byte[] data = dataStream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                return bitmap;
            } finally {
                IOUtils.closeStream(inputStream);
                IOUtils.closeStream(outputStream);
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        }
        return null;
    }

    /**
     * Clear all cached images in memory.
     */
    public void clearImageCache() {
        sArtCache.clear();
    }

    /**
     * Check the storage space of cache directory.
     */
    public void checkSpaceForCacheDirectory() {

        Log.d(LOG_TAG, "Check the storage space of cache directory.");
        File[] tempFiles = getCacheDirectory().listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String fileName = pathname.getName();
                if (!fileName.startsWith(".")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (tempFiles != null) {
            int length = tempFiles.length;
            if (length > HARD_CACHE_CAPACITY) {
                HashMap<Long, File> lastMF = new HashMap<Long, File>();
                long[] lastModifieds = new long[HARD_CACHE_CAPACITY];
                for (int i = 0; i < HARD_CACHE_CAPACITY; i++) {
                    lastModifieds[i] = tempFiles[i].lastModified();
                    lastMF.put(lastModifieds[i], tempFiles[i]);
                }
                Arrays.sort(lastModifieds);
                File tempFile = null;
                String imageName = null;
                for (int i = 0; i < HARD_CACHE_CAPACITY; i++) {
                    tempFile = lastMF.get(lastModifieds[i]);
                    if (tempFile != null) {
                        imageName = tempFile.getName();
                        if (tempFile.delete()) {
                            sArtCache.remove(imageName);
                            Log.i(LOG_TAG, "Delete cache file: " + imageName);
                        } else {
                            Log.i(LOG_TAG, "The cached file is in use: " + imageName);
                        }
                    }
                }
            }
        }
    }
}
