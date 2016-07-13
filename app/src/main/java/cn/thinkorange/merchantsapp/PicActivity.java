package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import CoreService.BasicNameValuePartner;
import CoreService.CallBacks;
import CoreService.CallServiceAsyncTask;
import CoreService.MD5;
import CoreService.UploadImgTask;
import Model.GlobalModel;

public class PicActivity extends Activity implements OnClickListener {
    private GlobalModel globalModel = GlobalModel.get_Instanst();
    private Context context;

    private ImageView btn_Upload_pictures;
    private ImageView btn_Upload_pictures_next;
    private LinearLayout imageLoad;
    private LinearLayout nextInfo;

    private TextView tv_picture_cut_back;

    private ListView listview_up_picture;
    private List<Bitmap> listDownPhotos;//图片的容器
    private MyPhotoAdapter adapter;

    private Bitmap photo;

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    // 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

    Dialog dialog;
    private long exitTime = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_pic);
        initViews();
    }

    //初始化控件
    private void initViews() {
        context = PicActivity.this;

        btn_Upload_pictures = (ImageView) findViewById(R.id.btn_Upload_pictures);
        btn_Upload_pictures_next = (ImageView) findViewById(R.id.btn_Upload_pictures_next);

        this.imageLoad = (LinearLayout) findViewById(R.id.imageload_ll);
        this.nextInfo = (LinearLayout) findViewById(R.id.nextinfo_ll);
        //为ImageButton和Button添加监听事件
        imageLoad.setOnClickListener(this);
        nextInfo.setOnClickListener(this);

        // listphotos = new ArrayList<Drawable>();
        listDownPhotos = new ArrayList<Bitmap>();
        listview_up_picture = (ListView) findViewById(R.id.listview_up_picture);

        tv_picture_cut_back = (TextView) findViewById(R.id.tv_picture_cut_back);//标题栏返回按钮
        tv_picture_cut_back.setOnClickListener(this);

        initRoundimageHttp();//调用轮播图接口
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageload_ll://上传照片
                if (listDownPhotos.size() >= 5) {
                    Tool.showSimPromptdia(context, "最多上传5张图片");
                } else {
                    showPhotoDialog();
                }
                break;

            case R.id.nextinfo_ll://下一步
                finish();//结束当前activity
                startActivity(new Intent(context, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

            case R.id.tv_picture_cut_back://标题栏返回按钮
                finish();//结束当前activity

                break;

            default:
                break;
        }

    }

    /**
     * 提示选择照片对话框方法
     */
    private void showPhotoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(getResources().getStringArray(R.array.image_from_arr), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dialog.dismiss();
                    // 调用系统的拍照功能
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 指定调用相机拍照后照片的储存路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                } else if (which == 1) {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                }
            }
        });
        builder.show();
    }

    /**
     * onActivityResult方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                startPhotoZoom(Uri.fromFile(tempFile), 150);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 150);
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null)
                    setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size * 2);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            photo = bundle.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            //	img_btn.setBackgroundDrawable(drawable);

            initRoundimageUpHttp();//调用轮播图上传接口

        }
    }

    /**
     * 选择店铺信息照片后，显示在客户端的adapter
     */

    class MyPhotoAdapter extends BaseAdapter {
        public void refresh() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listDownPhotos.size();
        }

        @Override
        public Object getItem(int position) {
            return listDownPhotos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderPhoto holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
                holder = new ViewHolderPhoto();
                holder.img_up_picture = (ImageView) convertView.findViewById(R.id.img_up_picture);
                holder.btn_up_picture_delete = (Button) convertView.findViewById(R.id.btn_up_picture_delete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolderPhoto) convertView.getTag();
            }
            int winwidth = Tool.getWinWidth(context);//获取屏幕宽度
            int dewidth = Tool.dip2px(context, (10 + 10)); //获取要减去的屏幕宽度，并转化为px

            int width = (int) (winwidth - dewidth); //获取单个gridview宽度
            int height = (int) (0.5 * width); //获取单个gridview高度

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height); //新建一个params
            holder.img_up_picture.setLayoutParams(params); //设置ImageView的长宽
            // holder.img_up_picture.setImageDrawable(listDownPhotos.get(position));
            holder.img_up_picture.setImageBitmap(listDownPhotos.get(position));//设置图片

            holder.btn_up_picture_delete.setTag(position);
            holder.btn_up_picture_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//删除按钮
                    listDownPhotos.remove(position); //移除button所在的item的图片数据
                    adapter.refresh();  //刷新Adapter数据

                    initRoundimageDeHttp(position);//删除图片接口

                }
            });

            return convertView;
        }
    }

    /*
    * 自定义一个接受listview_pro_info_manage一项的容器
    * */
    public static final class ViewHolderPhoto {//里边就一个图片框和删除按钮
        public ImageView img_up_picture;
        public Button btn_up_picture_delete;
    }

    /**
     * 调用轮播图接口
     */
    private void initRoundimageHttp() {
        ArrayList<BasicNameValuePartner> data = new ArrayList<BasicNameValuePartner>();
//        data.add(new BasicNameValuePartner("merchant_id", globalModel.merchant_id));
//        data.add(new BasicNameValuePartner("shop_id", globalModel.shop_id));
        CallServiceAsyncTask roundImageTask = new CallServiceAsyncTask(context, data, "merchant/roundimage", new CallBacks() {
            @Override
            public void OnResult(Object o) {
                if (o != null) {
                    System.out.println(o.toString());
                    adapter = new MyPhotoAdapter();
                    listview_up_picture.setAdapter(adapter);
                    if (o.toString().trim().equals("")) {
                        Tool.showMessage(context, "网络连接错误");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());//将返回的object取值
                            String errcode = jsonObject.getString("errcode");//取出返回操作成功失败的标志值
                            if (errcode.equals("0")) {//0代表成功

                                dialog.dismiss();

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                                    String[] UriData = new String[jsonArray.length()];
                                List<String> listUriData = new ArrayList<String>();//设一个数组存图片地址

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (jsonArray.getString(i).trim().length() != 0) {//舍弃空地址
//                                    UriData[i] = jsonArray.getString(i);//把解析出来的图片地址放入数组
                                        listUriData.add(jsonArray.getString(i));//把解析出来的图片地址放入数组
                                    }
                                }
                                if (listUriData.size() == 0) {
                                    Tool.showMessage(context, "轮播图里暂时没有图片，请上传");
                                } else {
                                    Tool.showMessage(context, "图片正在加载中，请稍候");

                                    for (int i = 0; i < listUriData.size(); i++) {
                                        LoadImgTask loadImgTask = new LoadImgTask();//调用读取图片的AsyncTask
                                        loadImgTask.execute(listUriData.get(i));//请求读取图片
                                    }
                                }
                            } else {
                                Tool.showMessage(context, "图片加载失败，请重试");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Tool.showMessage(context, "网络连接错误");
                }

            }

            @Override
            public void OnFault(Object o) {
            }
        });

//        dialog = new Dialog(PicActivity.this,R.style.new_circle_progress);
//        dialog.setContentView(R.layout.layout_progressbar);
//        dialog.setCancelable(true);
//        dialog.show();

        roundImageTask.doAsyncTask();
    }

    /**
     * 加载图片AsyncTask
     */
    private class LoadImgTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String imgPath = params[0];
            Log.e("TGA", imgPath);
            Bitmap bitmap = downloadImage(imgPath);
            return bitmap;
        }

        /**
         * 处理返回的bitmap结果
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // listDownPhotos.clear();//先清除之前的数据

            listDownPhotos.add(bitmap);//listview 接收bitmap

            adapter = new MyPhotoAdapter();//创建adapterzx
            listview_up_picture.setAdapter(adapter);//listview设置适配器


            super.onPostExecute(bitmap);
        }

        /**
         * 从网络上获取图片
         */
        private Bitmap downloadImage(String imageUrl) {
            HttpURLConnection con = null;
            Bitmap bitmap = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            File imageFile = null;
            try {
                GlobalModel globalData = GlobalModel.get_Instanst();
                URL url = new URL(globalData.BaseUri + imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setRequestMethod("GET");

                bis = new BufferedInputStream(con.getInputStream());
                imageFile = new File(getImagepath(imageUrl));
                fos = new FileOutputStream(imageFile);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int length;
                while ((length = bis.read(b)) != -1) {
                    bos.write(b, 0, length);
                    bos.flush();
                }

            } catch (Exception e) {
                if (e instanceof SocketException) {
                    Toast.makeText(context, "连接超时，请重试。", Toast.LENGTH_SHORT).show();
                }
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (con != null) {
                        con.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                bitmap = ImageLoader.getInstance().getBitmapFromSDCard(imageUrl, 400, 800);
                if (bitmap != null) {
                    ImageLoader.getInstance().addBitmapToMemoryCache(imageUrl, bitmap);
                    return bitmap;
                }
            }

            return null;
        }

        /**
         * 获取img地址
         */
        private String getImagepath(String imageUrl) {
            String imageDir = Environment.getExternalStorageDirectory().getPath() + "/smartsign/catch/";
            File file = new File(imageDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            String imagePath = imageDir + MD5.getMD5Str(imageUrl);
            return imagePath;
        }
    }

    /**
     * 调用轮播图上传接口
     */
    private void initRoundimageUpHttp() {
        ArrayList<BasicNameValuePartner> dataArr = new ArrayList<BasicNameValuePartner>();
//        dataArr.add(new BasicNameValuePartner("merchant_id", globalModel.merchant_id));
//        dataArr.add(new BasicNameValuePartner("shop_id", globalModel.shop_id));
        UploadImgTask uploadImgTask = new UploadImgTask(context, dataArr, photo, "merchant/roundimageup", new CallBacks() {
            @Override
            public void OnResult(Object o) {
                if (o != null) {
                    if (o.toString().trim().equals("")) {
                        Tool.showMessage(context, "网络连接错误");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());//将返回的object取值
                            String errcode = jsonObject.getString("errcode");//取出返回操作成功失败的标志值
                            if (errcode.equals("0")) {//0代表成功

                                dialog.dismiss();

                                Tool.showMessage(context, "上传成功");
                                listDownPhotos.add(photo); //添加到list里边
                                //adapter = new MyPhotoAdapter();
                                adapter.refresh(); //刷新adapter
                            } else {
                                Tool.showMessage(context, "上传失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Tool.showMessage(context, "网络连接错误");
                }
            }

            @Override
            public void OnFault(Object o) {

            }
        });

//        dialog = new Dialog(PicActivity.this,R.style.new_circle_progress);
//        dialog.setContentView(R.layout.layout_progressbar);
//        dialog.setCancelable(true);
//        dialog.show();

        uploadImgTask.doAsyncTask();
    }

    /**
     * 调用轮播图删除接口
     */
    private void initRoundimageDeHttp(int position) {
        ArrayList<BasicNameValuePartner> data = new ArrayList<BasicNameValuePartner>();
//        data.add(new BasicNameValuePartner("merchant_id", globalModel.merchant_id));
//        data.add(new BasicNameValuePartner("shop_id", globalModel.shop_id));
        data.add(new BasicNameValuePartner("index", Integer.toString(position)));
        CallServiceAsyncTask moveImageTask = new CallServiceAsyncTask(context, data, "merchant/roundimageremove", new CallBacks() {
            @Override
            public void OnResult(Object o) {
                if (o != null) {
                    if (o.toString().trim().equals("")) {
                        Tool.showMessage(context, "网络连接错误");
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(o.toString());//将返回的object取值
                            String errcode = jsonObject.getString("errcode");//取出返回操作成功失败的标志值
                            if (errcode.equals("0")) {//0代表成功

                                dialog.dismiss();

                                Tool.showShortMessage(context, "删除成功");
                            } else {
                                Tool.showShortMessage(context, "删除失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Tool.showMessage(context, "网络连接错误");
                }
            }

            @Override
            public void OnFault(Object o) {
            }
        });

//        dialog = new Dialog(PicActivity.this,R.style.new_circle_progress);
//        dialog.setContentView(R.layout.layout_progressbar);
//        dialog.setCancelable(true);
//        dialog.show();

        moveImageTask.doAsyncTask();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Tool.showShortMessage(PicActivity.this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();//记录按下返回键时的时间戳
        } else {
            finish();
            System.exit(0);
        }
    }
}