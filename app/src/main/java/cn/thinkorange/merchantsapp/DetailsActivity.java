package cn.thinkorange.merchantsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import VO.SearchVO;

public class DetailsActivity extends Activity {

    private EditText shopType, shopNo, shopDetails, buyers, phone, Email;
    private ImageView getBack;
    private TextView titleName;
    SearchVO searchVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent intent = getIntent();
        searchVO = (SearchVO) intent.getSerializableExtra("Item");
        System.out.println(searchVO.toString());
        initViews();
    }

    private void initViews() {
        this.shopType = (EditText) findViewById(R.id.shop_et);
        this.titleName = (TextView) findViewById(R.id.title_name_tv);
        this.titleName.setText("求购信息");
        this.getBack = (ImageView) findViewById(R.id.getback);
        this.shopNo = (EditText) findViewById(R.id.shop_no_et);
        this.shopDetails = (EditText) findViewById(R.id.shop_details_et);
        this.buyers = (EditText) findViewById(R.id.buyers_et);
        this.phone = (EditText) findViewById(R.id.phone_no);
        this.Email = (EditText) findViewById(R.id.email_et);

        this.getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });

        shopType.setText(searchVO.getType());
        shopNo.setText(searchVO.getCount());
        shopDetails.setText(searchVO.getDetails());
        buyers.setText(searchVO.getBuyer());
        phone.setText(searchVO.getPhone());
        Email.setText(searchVO.getEmail());
    }


}