package Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

import cn.thinkorange.merchantsapp.R;

/**
 * 广告图片自动轮播控件</br>
 */
public class ImageCycleView extends LinearLayout {
    /**
     * 上下文
     */
    private Context mContext;
    public static int i = 0;
    /**
     * 图片轮播视图
     */
    private ViewPager mAdvPager = null;
    /**
     * 滚动图片视图适配
     */
    private ImageCycleAdapter mAdvAdapter;
    /**
     * 图片轮播指示器控件
     */
    private ViewGroup mGroup;

    /**
     * 图片轮播指示个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */

    private boolean isStop;

    private int imageCount;
    ArrayList<String> imageNameList;
    Bitmap imageBitmap;

    /**
     * @param context
     */
    public ImageCycleView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.ad_cycle_view, this);
        mAdvPager = (ViewPager) findViewById(R.id.adv_pager);
        mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
        mAdvPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
    }

    /**
     * 装填图片数据
     *
     * @param imageBitmapList
     * @param imageCycleViewListener
     */
    public void setImageResources(ArrayList<Bitmap> imageBitmapList, ArrayList<String> imageUrlList, ImageCycleViewListener imageCycleViewListener) {
        // 清除
        mGroup.removeAllViews();
        // 图片广告数量
        if (imageUrlList.size() == 0) {
            imageCount = imageBitmapList.size();
            System.out.println(imageBitmapList.size()+"imageBitmapList.size()");
        } else {
            imageCount = imageUrlList.size();
            System.out.println(imageUrlList.size()+"imageUrlList.size()");
        }

        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 30;
            mImageView.setScaleType(ScaleType.FIT_XY);
            mImageView.setLayoutParams(params);

            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_focus);
            } else {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_blur);
            }
            mGroup.addView(mImageViews[i]);
        }

        mAdvAdapter = new ImageCycleAdapter(mContext, imageBitmapList, imageUrlList, imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);
        startImageTimerTask();
    }

    /**
     * 图片轮播(手动控制自动轮播与否，便于资源控件）
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播—用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片滚动
        mHandler.postDelayed(mImageTimerTask, 8000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        isStop = true;
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            if (mImageViews != null) {
                mAdvPager.setCurrentItem(mAdvPager.getCurrentItem() + 1);
                if (!isStop) {  //if  isStop=true   //当你退出后 要把这个给停下来 不然 这个一直存在 就一直在后台循环
                    mHandler.postDelayed(mImageTimerTask, 8000);
                }

            }
        }
    };

    /**
     * 轮播图片监听
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            index = index % mImageViews.length;
            // 设置当前显示的图片
            // 设置图片滚动指示器背
            i++;
            mImageViews[index].setBackgroundResource(R.drawable.banner_dian_focus);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setBackgroundResource(R.drawable.banner_dian_blur);
                }
            }
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<ImageView> mImageViewCacheList;

        /**
         * 图片资源列表
         */
        private ArrayList<Bitmap> mAdList = new ArrayList<Bitmap>();

        private ArrayList<String> mList = new ArrayList<String>();

        /**
         * 广告图片点击监听
         */
        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;
        private String imageUrl;

        public ImageCycleAdapter(Context context, ArrayList<Bitmap> adList, ArrayList<String> imageUrlList, ImageCycleViewListener imageCycleViewListener) {
            this.mContext = context;
            this.mAdList = adList;
            this.mList = imageUrlList;
            mImageCycleViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
//			return mAdList.size();
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            if (mAdList.size()>0) {
               imageBitmap = mAdList.get(position % mAdList.size());
            }
            if(mList.size() > 0) {
                imageUrl = mList.get(position%mList.size());
            }

            Log.i("imageUrl", "bitmap");
           SmartImageView imageView = null;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new SmartImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                //test
                imageView.setScaleType(ScaleType.FIT_XY);
                //imageView.setImageUrl("http://imgs.xiuna.com/xiezhen/2014-9-25/2/5562900520140919100645087.jpg");
                // 设置图片点击监听
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageCycleViewListener.onImageClick(position % mAdList.size(), v);
                    }
                });
            } else {
                imageView = (SmartImageView) mImageViewCacheList.remove(0);
            }

            if (mList.size() == 0) {
                //			imageView.setTag(imageUrl);
                container.addView(imageView);
//			imageView.setImageUrl(imageUrl);
               if(imageBitmap != null) {
                   Drawable drawable = new BitmapDrawable(imageBitmap);
                   imageView.setBackgroundDrawable(drawable);
               }
            } else {
                imageView.setTag(imageUrl);
                container.addView(imageView);
                imageView.setImageUrl(imageUrl);
//				Drawable drawable = new BitmapDrawable(imageUrl);
//				imageView.setBackgroundDrawable(drawable);
            }

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            mAdvPager.removeView(view);
            mImageViewCacheList.add(view);

        }

    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public static interface ImageCycleViewListener {

        /**
         * 单击图片事件
         *
         * @param position
         * @param imageView
         */
        public void onImageClick(int position, View imageView);
    }

}
