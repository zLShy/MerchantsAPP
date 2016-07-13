package Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/3/23.
 */
public class ImgCompresser {

    private static ImgCompresser imgCompresser;
    private static LruCache<String, Bitmap> memoryCache;

    private ImgCompresser(){
        int maxMemory = (int) Runtime.getRuntime().freeMemory();
        int cacheSize = maxMemory / 5;
        memoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return super.sizeOf(key, value);
            }
        };
    }

    public static ImgCompresser getInstance(){
        if (imgCompresser == null){
            imgCompresser = new ImgCompresser();
        }
        return imgCompresser;
    }

    public Bitmap getBitmap(String path){
        if (memoryCache.get(getMD5String(path)) != null){
            return memoryCache.get(getMD5String(path));
        }else {
            return getImage(path);
        }
    }

    public static String getMD5String(String str){
        return MD5.getMD5Str(str);
    }

    public static Bitmap getImage(String path){
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 640f;
        float ww = 960f;

        int be = 1;
        if (w > h && w > ww){
            be = (int) (newOpts.outWidth / ww);
        }else if ((w < h && h > hh)){
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0){
            be = 1;
        }
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        bitmap = compressImage(bitmap);
        addBitmapToMemoryCache(path, bitmap);
        return bitmap;
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>300) {  //循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static void addBitmapToMemoryCache(String fileName, Bitmap bitmap){
        if (memoryCache.size() >= memoryCache.maxSize()){
            memoryCache.evictAll();
        }
        if (getBitmapFromCache(fileName) == null){
            memoryCache.put(getMD5String(fileName), bitmap);
        }
    }

    public static void cleanCache(){
        if (memoryCache != null){

            memoryCache.evictAll();
        }

        memoryCache = null;
    }

    public static Bitmap getBitmapFromCache(String filePath){
        return memoryCache.get(getMD5String(filePath));
    }

    public static synchronized void removeImageCache(String key) {
        if (key != null) {
            if (memoryCache != null) {
                Bitmap bm = memoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
}
