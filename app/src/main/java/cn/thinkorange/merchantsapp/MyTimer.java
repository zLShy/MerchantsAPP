package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;

public class MyTimer extends CountDownTimer {

	private Button btn;
	private Activity mActivity;
	public MyTimer( Activity mActivity, long millisInFuture, long countDownInterval,Button btn) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.btn = btn;
		this.mActivity = mActivity;
		
//		btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//					Intent it = new Intent();
//					it.setClass(mActivity, TwoActivity.class);
//					mActivity.startActivity(it);
//			}
//		});
	}

	
	
	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
//		btn.setClickable(false);//���ò��ܵ��
		btn.setText("跳 转\n"+millisUntilFinished / 1000+" S");//倒计时

		//���ð�ťΪ��ɫ����ʱ�ǲ��ܵ����
		Spannable span = new SpannableString(btn.getText().toString());//��ȡ��ť������
		btn.setText(span);

	}

	@Override
	public void onFinish() {
		btn.setVisibility(View.INVISIBLE);
		// TODO Auto-generated method stub
//		btn.setText("发送验证码");
//		btn.setClickable(true);//���»�õ��
	}

	
}
