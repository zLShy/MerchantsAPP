package CoreService;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abc on 2015/11/05.
 */
public class SuperImageLoader extends AsyncTask<String, Void, Bitmap> {
    private String imageRootPath;
    private String imageName;
    private CallBacks callBacks;
    private SuperImageManager superImageManager = SuperImageManager.getInstance();

    public SuperImageLoader(String imageRootPath, String imageName, CallBacks callBacks) {
        this.imageRootPath = imageRootPath;
        this.imageName = imageName;
        this.callBacks = callBacks;
    }

    public void doIt(){
        this.execute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String fileName = imageName;
        String fileDirInServer = imageRootPath;
        Bitmap bitmap;
        if (superImageManager.getBitmapFromCache(fileName) != null){
            System.out.println("1");
            bitmap = superImageManager.getBitmapFromCache(fileName);
        }else if (superImageManager.checkFileExist(fileName)){
            bitmap = superImageManager.getBitmapFromSDCard(fileName, 400, 800);
            System.out.println("2");
            superImageManager.addBitmapToMemoryCache(fileName, bitmap);
        }else {
            bitmap = downloadImage(fileName, fileDirInServer);
            System.out.println("2");
        }
        return bitmap;
    }

    private Bitmap downloadImage(String fileName, String fileDir){
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try{
            URL url = new URL(fileName);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(15 * 1000);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            superImageManager.addBitmapToSDCard(bitmap, fileName);
            superImageManager.addBitmapToMemoryCache(fileName, bitmap);
        }catch (Exception e){
            if (e != null){
                e.printStackTrace();
            }
        }finally {
            try{
                inputStream.close();
                connection.disconnect();
            }catch (Exception e){
                if (e != null){
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        callBacks.OnResult(bitmap);
        super.onPostExecute(bitmap);
    }
}
