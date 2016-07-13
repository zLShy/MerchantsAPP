package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;

import Tools.SaveImageToSD;


public class MyQrCodeActivity extends Activity {

    private ImageView mCode;
    Bitmap bitmap;// 二维码中间图片
    private int iv_halfWidth = 20;// 显示中间图片的宽度的一半
    Bitmap mBitmap;// 二维码图片;

    private ImageView goBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);
        TextView tv = (TextView)findViewById(R.id.title_name_tv);
        tv.setText("我的名片");
        mCode = (ImageView) findViewById(R.id.myqrcode);

        goBack = (ImageView) findViewById(R.id.getback);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.applogo);

        // 缩放图片，用到矩阵去做
        Matrix matrix = new Matrix();
        float sx = (float) 2 * iv_halfWidth / bitmap.getWidth();
        float sy = (float) 2 * iv_halfWidth / bitmap.getHeight();
        matrix.setScale(sx, sy);
        // 生成缩放后的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);

        String s ="http://f.thinkorange.cn/3641";
        try {
            mBitmap = createBitmap(new String(s.getBytes(), "ISO-8859-1"));
            mCode.setImageBitmap(mBitmap);
            SaveImageToSD.saveBitmap(mBitmap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        mCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");// 格式固定
                // 保存图片
                File file;
                try {
                    file = new File("mnt/sdcard/mycode.png");
                    // 没有个文件都有一个指定的Uri
                    Uri u = Uri.fromFile(file);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "share");
                    intent.putExtra(Intent.EXTRA_STREAM, u);
                    intent.putExtra(Intent.EXTRA_TEXT, "没错，这是一个二维码图片");
                    startActivity(Intent.createChooser(intent, "分享图片到"));// 加一个提示框
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    /**
     * 根据字符串生成二维码
     *
     * @param str
     * @return
     * @throws WriterException
     */
    private Bitmap createBitmap(String str) throws WriterException {
        // 生成而为矩阵，编码是指定大小，不要生成了图片在进行缩放，这样会导致模糊识别失败，就是扫描失败了。
        BitMatrix mBitMatrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, 300, 300);// BarcodeFormat.QR_CODE-编码格式
        // 二维矩阵的宽高
        int w = mBitMatrix.getWidth();
        int h = mBitMatrix.getHeight();

        // 头像的宽度
        int halfw = w / 2;
        int halfh = h / 2;
        // 准备画二维码，把二维矩阵转换为一维数组，一直横着画
        int[] pixels = new int[w * h];// 数组长度就是矩阵的面积值
        for (int y = 0; y < h; y++) {
            int outputOffset = y * w;
            for (int x = 0; x < w; x++) {
                // 画一个普通的二维码
                // if (mBitMatrix.get(x, y)) {// 表示二维矩阵有值，对应画一个黑点
                // pixels[outputOffset + x] = 0xff000000;
                // } else {
                // pixels[outputOffset + x] = 0xffffffff;
                // }

                // 画一个有图片的二维码图片
                if (x > (halfw - iv_halfWidth) && x < (halfw + iv_halfWidth)
                        && y > (halfh - iv_halfWidth)
                        && y < (halfh + iv_halfWidth)) {// 中间图片的区域
                    pixels[outputOffset + x] = bitmap.getPixel(x - halfw
                            + iv_halfWidth, y - halfh + iv_halfWidth);// 这里画图之后会很明显的显示出来
                } else {
                    if (mBitMatrix.get(x, y)) {// 表示二维矩阵有值，对应画一个黑点
                        pixels[outputOffset + x] = 0xff000000;
                    } else {
                        pixels[outputOffset + x] = 0xffffffff;
                    }
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个300*300bitmap
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);// 像素点、起始点、宽度、其起始像素、宽、高
        return bitmap;

    }

}
