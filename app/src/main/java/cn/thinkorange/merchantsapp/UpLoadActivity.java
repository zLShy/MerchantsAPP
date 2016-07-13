package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import CoreService.AddOrUpdateSpecialOfferTask;
import CoreService.BasicNameValuePartner;
import CoreService.CallBacks;
import CoreService.SuperImageLoader;
import Model.GlobalModel;


public class UpLoadActivity extends Activity {

    private Button sunmit;
    private EditText companyName, companyUrl, companyFax, phone, Email, Address, introduce;
    private Spinner sp;
    private SmartImageView UpIv1, UpIv2, UpIv3;
    private TextView stall;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private Cursor cursor;
    File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
    private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private Bitmap[] mBitmaps = new Bitmap[3];
    private String types[] = {"1", "2", "3", "4"};
    private ImageView getBack;
    private Dialog pd;

    private int finalI = 0;

    Handler mHandler;

    private int ImageViewNo = 0;
    private ArrayList<Bitmap> Loadbitmaps = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);


        initViews();

        initEvents();
        if (GlobalModel.get_Instanst().imageUrls.size() > 0) {
            LoadContentImags();
        } else if (HomeActivity.bitmaps.size() > 0) {
            for (int i = 0; i < HomeActivity.bitmaps.size(); i++) {
                mBitmaps[i] = HomeActivity.bitmaps.get(i);
            }
            SetBitmaps();
        }

    }

    private void initEvents() {

        this.UpIv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewNo = 1;
                showPhotoDialog();

            }
        });

        UpIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewNo = 2;
                showPhotoDialog();
            }
        });
        UpIv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewNo = 3;
                showPhotoDialog();
            }
        });
        this.sunmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), bitmaps.size() + "", Toast.LENGTH_LONG).show();
                bitmaps.clear();
                for (int i = 0; i <= 2; i++) {
                    if (mBitmaps[i] != null) {
                        bitmaps.add(mBitmaps[i]);
                    }
                }

                UpMessage();
                System.out.println(bitmaps.size() + "");
            }
        });
    }

    private void initViews() {
        this.sunmit = (Button) findViewById(R.id.up_submit);
        this.companyName = (EditText) findViewById(R.id.companyname);
        this.companyUrl = (EditText) findViewById(R.id.companyurl);
        this.companyFax = (EditText) findViewById(R.id.companyfax);
        this.phone = (EditText) findViewById(R.id.phone);
        this.Email = (EditText) findViewById(R.id.email);
        this.Address = (EditText) findViewById(R.id.addresset);
        this.introduce = (EditText) findViewById(R.id.company_et);
        this.sp = (Spinner) findViewById(R.id.sp_type);
        this.UpIv1 = (SmartImageView) findViewById(R.id.upiv1);
        this.UpIv2 = (SmartImageView) findViewById(R.id.upiv2);
        this.UpIv3 = (SmartImageView) findViewById(R.id.upiv3);
        this.getBack = (ImageView) findViewById(R.id.get_back);
        this.stall = (TextView) findViewById(R.id.stall);

        this.stall.setText("    " + GlobalModel.get_Instanst().userName);

        companyName.setText("    " + GlobalModel.get_Instanst().company_name.trim() + "");
        companyUrl.setText("    " + GlobalModel.get_Instanst().company_web.trim() + "");
        companyFax.setText("    " + GlobalModel.get_Instanst().company_fax.trim() + "");
        phone.setText("    " + GlobalModel.get_Instanst().company_tel.trim() + "");
        Email.setText("    " + GlobalModel.get_Instanst().company_email.trim() + "");
        Address.setText("    " + GlobalModel.get_Instanst().company_add.trim() + "");
        introduce.setText("    " + GlobalModel.get_Instanst().company_intro.trim() + "");
        if (GlobalModel.get_Instanst().hasMessage) {
            for (int i = 0; i < GlobalModel.get_Instanst().typesId.length; i++) {
                if (GlobalModel.get_Instanst().typesId[i].equals(GlobalModel.get_Instanst().map_polygon_category_id)) {
                    String type = GlobalModel.get_Instanst().types[0];
                    GlobalModel.get_Instanst().types[0] = GlobalModel.get_Instanst().types[i];
                    GlobalModel.get_Instanst().types[i] = type;

                    String typesId = GlobalModel.get_Instanst().typesId[0];
                    GlobalModel.get_Instanst().typesId[0] = GlobalModel.get_Instanst().typesId[i];
                    GlobalModel.get_Instanst().typesId[i] = typesId;
                }
            }
        }
        this.getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpLoadActivity.this, R.layout.spinner_item, GlobalModel.get_Instanst().types);
        this.sp.setAdapter(adapter);
    }
//    http://ss.thinkorange.cn/upload/img_icon/6315c32399df3bb4644e3d5644194a0f.jpg
//    http://ss.thinkorange.cn/upload/img_icon/5dbb348f02134e79942f2b952a306dc7.jpg

    /**
     * 提示选择照片对话框方法
     */
    private void showPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpLoadActivity.this);
        builder.setItems(R.array.image_from_arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                } else if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                }
            }
        });
        builder.show();
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String fileName = getPhotoFileName();
        Bitmap bitmap = null;
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                try {
                    bitmap = compassImage(String.valueOf(tempFile.getAbsoluteFile()), fileName, 100);
                    bitmaps.add(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                System.out.println("123");
                if (data != null) {
                    try {

                        Uri uri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        String imagePath = "";
                        cursor = managedQuery(uri, proj, null, null, null);
                        if (cursor != null) {
                            int cloum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                                imagePath = cursor.getString(cloum_index);
                            }
                        }
                        System.out.println("124" + fileName);
                        System.out.println("1====>" + imagePath);
                        bitmap = compassImage(imagePath, fileName, 100);
//                        bitmaps.add(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ;
                    }
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        bitmap = bundle.getParcelable("data");
                    }
                }
                break;


        }

        if (bitmap != null && ImageViewNo == 1) {
            Drawable drawable = new BitmapDrawable(bitmap);
//            bitmaps.remove(ImageViewNo - 1);
            bitmaps.add(bitmap);
            mBitmaps[ImageViewNo - 1] = bitmap;
            UpIv1.setImageDrawable(drawable);
        }

        if (bitmap != null && ImageViewNo == 2) {
            Drawable drawable = new BitmapDrawable(bitmap);
//            bitmaps.remove(ImageViewNo - 1);
            mBitmaps[ImageViewNo - 1] = bitmap;
            bitmaps.add(bitmap);

            UpIv2.setImageDrawable(drawable);
        }

        if (bitmap != null && ImageViewNo == 3) {
            Drawable drawable = new BitmapDrawable(bitmap);

//            bitmaps.remove(ImageViewNo - 1);
            bitmaps.add(bitmap);
            mBitmaps[ImageViewNo - 1] = bitmap;
            UpIv3.setImageDrawable(drawable);
        }
    }


    private Bitmap compassImage(String filepath, String fileName, int i) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filepath);
        if (bm == null) {
            System.out.println("null====>bm");
            return null;
        }
        int degree = readPictureDegree(filepath);
        if (degree != 0) {
            bm = rotateBitmap(bm, degree);
        }
        return bm;
    }

    private Bitmap getSmallBitmap(String filepath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, options);
    }

    //计算图片的缩放值
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = (heightRatio + widthRatio) / 2;
        }

        return inSampleSize;

    }

    private int readPictureDegree(String filepath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filepath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private Bitmap rotateBitmap(Bitmap bm, int degree) {
        if (bm != null) {
            Matrix m = new Matrix();
            m.postRotate(degree);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
        }
        return bm;
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
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    private void UpMessage() {

        ArrayList<BasicNameValuePartner> data = new ArrayList<BasicNameValuePartner>();
        data.add(new BasicNameValuePartner("users_user_name", GlobalModel.get_Instanst().userName));
        data.add(new BasicNameValuePartner("company_name", companyName.getText().toString().trim()));
        data.add(new BasicNameValuePartner("company_intro", introduce.getText().toString()));
        data.add(new BasicNameValuePartner("company_tel", phone.getText().toString().trim()));
        data.add(new BasicNameValuePartner("company_fax", companyFax.getText().toString() + ""));
        data.add(new BasicNameValuePartner("company_add", Address.getText().toString()));
        System.out.println(Address.getText().toString());
        data.add(new BasicNameValuePartner("company_email", Email.getText().toString()));
        data.add(new BasicNameValuePartner("map_polygon_category_id", GlobalModel.get_Instanst().typesId[Integer.valueOf(Long.toString(sp.getSelectedItemId()))]));
        data.add(new BasicNameValuePartner("company_web", companyFax.getText().toString() + ""));
        AddOrUpdateSpecialOfferTask upTask = new AddOrUpdateSpecialOfferTask(UpLoadActivity.this, data, bitmaps, "uesrs/sumbitmessage", new CallBacks() {
            @Override
            public void OnResult(Object o) {
                if (o != null) {
                    pd.cancel();
                    if (o.toString().equals("")) {
                        Toast.makeText(getApplication(), "网络不给力~", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println(o.toString());
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            if ("0".equals(object.getString("errcode"))) {
                                Toast.makeText(getApplication(), "提交成功!", Toast.LENGTH_SHORT).show();
                                GlobalModel.get_Instanst().company_name = companyName.getText().toString().trim();
                                GlobalModel.get_Instanst().company_intro = introduce.getText().toString();
                                GlobalModel.get_Instanst().company_tel = phone.getText().toString().trim();
                                GlobalModel.get_Instanst().company_fax = companyFax.getText().toString() + "";
                                GlobalModel.get_Instanst().company_add = Address.getText().toString();
                                GlobalModel.get_Instanst().company_web = companyFax.getText().toString() + "";
                                HomeActivity.bitmaps.clear();
                                for (int i = 0; i < bitmaps.size(); i++) {
                                    HomeActivity.bitmaps.add(bitmaps.get(i));
                                }
                                if (HomeActivity.bitmaps.size() > 0) {
                                    GlobalModel.get_Instanst().imageUrls.clear();
                                }

                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void OnFault(Object o) {

            }
        });

        pd = new Dialog(UpLoadActivity.this, R.style.new_circle_progress);
        pd.setContentView(R.layout.layout_progressbar);
        pd.setCancelable(true);
        pd.show();
        upTask.doAsyncTask();
    }


    private void LoadContentImags() {
        System.out.println(GlobalModel.get_Instanst().imageUrls.size());
        if (GlobalModel.get_Instanst().imageUrls.size() > 0) {
            for (int i = 0; i < GlobalModel.get_Instanst().imageUrls.size(); i++) {
                System.out.println(GlobalModel.get_Instanst().imageUrls.get(i).toString());
                SuperImageLoader loadImgTask = new SuperImageLoader(GlobalModel.get_Instanst().BaseUri, GlobalModel.get_Instanst().imageUrls.get(i).toString(), new CallBacks() {

                    @Override
                    public void OnResult(Object o) {
                        Bitmap bitmap = (Bitmap) o;

                        if (bitmap != null) {
                            Loadbitmaps.add(bitmap);
//                            ImageViewNo++;
                            System.out.println(Loadbitmaps.size());
                            if (Loadbitmaps.size() == 1) {
                                mBitmaps[0] = bitmap;
                                Drawable drawable = new BitmapDrawable(bitmap);
                                UpIv1.setImageDrawable(drawable);
                            } else if (Loadbitmaps.size() == 2) {
                                mBitmaps[1] = bitmap;
                                Drawable drawable = new BitmapDrawable(bitmap);
                                UpIv2.setImageDrawable(drawable);
                            } else if (Loadbitmaps.size() == 3) {
                                mBitmaps[2] = bitmap;
                                Drawable drawable = new BitmapDrawable(bitmap);
                                UpIv3.setImageDrawable(drawable);
                            }
                        }
                    }

                    @Override
                    public void OnFault(Object o) {

                    }
                });

                loadImgTask.doIt();
            }
        }

    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 用数据装
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        // 关闭流一定要记得。
        return outstream.toByteArray();
    }

    private void SetBitmaps() {
        if (HomeActivity.bitmaps.size() == 1) {
            this.UpIv1.setImageBitmap(HomeActivity.bitmaps.get(0));
        } else if (HomeActivity.bitmaps.size() == 2) {
            this.UpIv1.setImageBitmap(HomeActivity.bitmaps.get(0));
            this.UpIv2.setImageBitmap(HomeActivity.bitmaps.get(1));
        } else if (HomeActivity.bitmaps.size() == 3) {
            this.UpIv1.setImageBitmap(HomeActivity.bitmaps.get(0));
            this.UpIv2.setImageBitmap(HomeActivity.bitmaps.get(1));
            this.UpIv3.setImageBitmap(HomeActivity.bitmaps.get(2));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (GlobalModel.get_Instanst().hasMessage) {
//            System.out.println("11111111");
//            LoadContentImags();
//            if(GlobalModel.get_Instanst().imageUrls.size()==1) {
//                UpIv1.setTag(GlobalModel.get_Instanst().imageUrls.get(0));
//                UpIv1.setImageUrl(GlobalModel.get_Instanst().imageUrls.get(0));
//            }else if(GlobalModel.get_Instanst().imageUrls.size()==2) {
//                UpIv1.setTag(GlobalModel.get_Instanst().imageUrls.get(0));
//                UpIv1.setImageUrl(GlobalModel.get_Instanst().imageUrls.get(0));
//                UpIv2.setTag(GlobalModel.get_Instanst().imageUrls.get(1));
//                UpIv2.setImageUrl(GlobalModel.get_Instanst().imageUrls.get(1));
//            }else if(GlobalModel.get_Instanst().imageUrls.size()==3) {
//                UpIv1.setTag(GlobalModel.get_Instanst().imageUrls.get(0));
//                UpIv1.setImageUrl(GlobalModel.get_Instanst().imageUrls.get(0));
//                UpIv2.setTag(GlobalModel.get_Instanst().imageUrls.get(1));
//                UpIv2.setImageUrl(GlobalModel.get_Instanst().imageUrls.get(1));
//                UpIv3.setTag(GlobalModel.get_Instanst().imageUrls.get(2));
//                UpIv3.setImageUrl(GlobalModel.get_Instanst().imageUrls.get(2));
//            }
//        }
    }
}
