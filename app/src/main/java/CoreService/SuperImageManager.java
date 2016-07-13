package CoreService;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by abc on 2015/11/05.
 */
public class SuperImageManager {
    private static SuperImageManager imageManager;
    private static LruCache<String, Bitmap> mMemoryCache;
    private static String fileDir = Environment.getExternalStorageDirectory().getPath() + "/smartsign/catch/";

    private SuperImageManager(){
        int maxMemory = (int) Runtime.getRuntime().freeMemory();
        int cacheSize = maxMemory / 6;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static SuperImageManager getInstance(){
        if (imageManager == null){
            imageManager = new SuperImageManager();
        }
        return imageManager;
    }

    public Bitmap getBitmapFromCache(String filePath){
        return mMemoryCache.get(getMD5String(filePath));
    }

    public Bitmap getBitmapFromSDCard(String fileName, int reqWidth, int reqHeight){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileDir + getMD5String(fileName), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inTempStorage = new byte[80 * 1024];
        if (checkFileExist(fileName)){
            File file = new File(fileDir + getMD5String(fileName));
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            }catch (Exception e){
                if (e != null){

                }
            }

            if (fileInputStream != null){
                try {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD(), null, options);
                }catch (Exception e){
                    if (e != null){

                    }
                }finally {
                    if (fileInputStream != null){
                        try {
                            fileInputStream.close();
                        }catch (Exception e){
                            if (e != null){

                            }
                        }
                    }
                }
            }
        }
        return bitmap;
    }

    public void addBitmapToMemoryCache(String fileName, Bitmap bitmap){
        if (mMemoryCache.size() >= mMemoryCache.maxSize()){
            mMemoryCache.evictAll();
        }
        if (getBitmapFromCache(fileName) == null){
            mMemoryCache.put(getMD5String(fileName), bitmap);
        }
    }

    public void addBitmapToSDCard(Bitmap bitmap, String fileName){
        File file = new File(fileDir + getMD5String(fileName));
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            file.createNewFile();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        }catch (Exception e){
            if (e != null){

            }
        }finally {
            try{
                fileOutputStream.close();
            }catch (Exception e){
                if (e != null){

                }
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth){
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

/*    public Bitmap decodeSampledBitmapFormResource(String filePath, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }*/

    public String getLocalFilePath(String fileName){
        File file = new File(fileDir);
        if (!file.exists()){
            file.mkdirs();
        }
        return fileDir + getMD5String(fileName);
    }

    public boolean checkFileExist(String fileName){
        File file = new File(fileDir + getMD5String(fileName));
        if (file.exists()){
            return true;
        }else {
            return false;
        }
    }

    public static String getMD5String(String str){
        return MD5.getMD5Str(str);
    }
}
