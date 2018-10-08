package com.xiaojun.danrenbanmohe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xiaojun.danrenbanmohe.MyApplication;
import com.xiaojun.danrenbanmohe.R;
import com.xiaojun.danrenbanmohe.bean.BaoCunBean;

import io.objectbox.Box;

public class BaseActivity extends Activity {
    private Box<BaoCunBean> baoCunBeanDao = null;
    private BaoCunBean baoCunBean = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        baoCunBeanDao = MyApplication.myApplication.getBoxStore().boxFor(BaoCunBean.class);
        baoCunBean=baoCunBeanDao.get(123456L);
        if (baoCunBean == null) {
            baoCunBean = new BaoCunBean();
            baoCunBean.setId(123456L);
            baoCunBean.setHoutaiDiZhi("http://192.168.2.187:8980/js/f");
            baoCunBean.setShibieFaceSize(50);
            baoCunBean.setShibieFaZhi(20);
            baoCunBean.setRuKuFaceSize(50);
            baoCunBean.setRuKuMoHuDu(0.4f);
            baoCunBean.setHuoTiFZ(60);
            baoCunBean.setHuoTi(true);
            baoCunBean.setDangqianShiJian("d");
            baoCunBean.setTianQi(false);

            baoCunBeanDao.put(baoCunBean);
        }

        startActivity(new Intent(BaseActivity.this,MainActivity.class));
        finish();


    }
}
