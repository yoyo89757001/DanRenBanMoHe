package com.xiaojun.danrenbanmohe.ui;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.xiaojun.danrenbanmohe.MyApplication;
import com.xiaojun.danrenbanmohe.R;
import com.xiaojun.danrenbanmohe.bean.BaoCunBean;
import com.xiaojun.danrenbanmohe.dialog.BangDingDialog;
import com.xiaojun.danrenbanmohe.dialog.XiuGaiDiZhiDialog;
import com.xiaojun.danrenbanmohe.dialog.XiuGaiHuoTiFZDialog;
import com.xiaojun.danrenbanmohe.dialog.XiuGaiRuKuFZDialog;
import com.xiaojun.danrenbanmohe.dialog.XiuGaiSBFZDialog;
import com.xiaojun.danrenbanmohe.dialog.YuYingDialog;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import io.objectbox.Box;
import megvii.facepass.ruitong.FaceInit;




public class SheZhiActivity extends Activity {
    @BindView(R.id.rl3)
    RelativeLayout rl3;
    @BindView(R.id.rl1)
    RelativeLayout rl1;
    @BindView(R.id.rl2)
    RelativeLayout rl2;
    @BindView(R.id.rl4)
    RelativeLayout rl4;
    @BindView(R.id.rl6)
    RelativeLayout rl6;
    @BindView(R.id.rl7)
    RelativeLayout rl7;
    @BindView(R.id.switchs)
    Switch switchs;
    @BindView(R.id.rl5)
    RelativeLayout rl5;
    @BindView(R.id.rl8)
    RelativeLayout rl8;
    @BindView(R.id.chengshi)
    TextView chengshi;
    @BindView(R.id.rl9)
    RelativeLayout rl9;

    private Box<BaoCunBean> baoCunBeanDao = null;
    private BaoCunBean baoCunBean = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_she_zhi);
        ButterKnife.bind(this);
        //ScreenAdapterTools.getInstance().reset(this);//如果希望android7.0分屏也适配的话,加上这句
        //在setContentView();后面加上适配语句
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        baoCunBeanDao = MyApplication.myApplication.getBoxStore().boxFor(BaoCunBean.class);
        baoCunBean=baoCunBeanDao.get(123456L);
        if (baoCunBean == null) {
            baoCunBean = new BaoCunBean();
            baoCunBean.setId(123456L);
            baoCunBean.setHoutaiDiZhi("http://192.168.2.187:8980/js/f");
            baoCunBean.setShibieFaceSize(100);
            baoCunBean.setShibieFaZhi(70);
            baoCunBean.setRuKuFaceSize(80);
            baoCunBean.setRuKuMoHuDu(0.4f);
            baoCunBean.setHuoTiFZ(70);
            baoCunBean.setHuoTi(true);
            baoCunBean.setDangqianShiJian("d");
            baoCunBean.setTianQi(false);

            baoCunBeanDao.put(baoCunBean);
        } else {
            if (baoCunBean.getDangqianChengShi2() == null) {
                chengshi.setText("");
            } else {
                chengshi.setText(baoCunBean.getDangqianChengShi2());
            }

        }

        EventBus.getDefault().register(this);//订阅
        switchs.setChecked(true);
        switchs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    baoCunBean.setHuoTi(true);
                    baoCunBeanDao.put(baoCunBean);
                    Toast tastyToast = TastyToast.makeText(SheZhiActivity.this, "活体验证已开启", TastyToast.LENGTH_LONG, TastyToast.INFO);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                } else {
                    baoCunBean.setHuoTi(false);
                    baoCunBeanDao.put(baoCunBean);
                    Toast tastyToast = TastyToast.makeText(SheZhiActivity.this, "活体验证已关闭", TastyToast.LENGTH_LONG, TastyToast.INFO);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                }

            }
        });



    }


    @OnClick({R.id.rl1, R.id.rl2, R.id.rl3, R.id.rl4, R.id.rl5, R.id.rl6, R.id.rl7, R.id.rl8, R.id.rl9})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl1:
                final XiuGaiDiZhiDialog diZhiDialog = new XiuGaiDiZhiDialog(SheZhiActivity.this);
                diZhiDialog.setCanceledOnTouchOutside(false);
                diZhiDialog.setContents(baoCunBean.getHoutaiDiZhi(), null);
                diZhiDialog.setOnQueRenListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baoCunBean.setHoutaiDiZhi(diZhiDialog.getUrl());
                        baoCunBeanDao.put(baoCunBean);
                        diZhiDialog.dismiss();
                    }
                });
                diZhiDialog.setQuXiaoListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        diZhiDialog.dismiss();
                    }
                });
                diZhiDialog.show();

                break;
            case R.id.rl2:
                final BangDingDialog bangDingDialog = new BangDingDialog(SheZhiActivity.this);
                bangDingDialog.setCanceledOnTouchOutside(false);
                bangDingDialog.setOnQueRenListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        link_uplodexiazai(bangDingDialog.getZhuCeMa());
                        FaceInit init = new FaceInit(getApplicationContext());
                        init.init(bangDingDialog.getZhuCeMa(), baoCunBean.getHoutaiDiZhi());
                        bangDingDialog.dismiss();
                    }
                });
                bangDingDialog.setQuXiaoListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bangDingDialog.dismiss();
                    }
                });
                bangDingDialog.show();
                break;
            case R.id.rl3:
                YuYingDialog yuYingDialog = new YuYingDialog(SheZhiActivity.this);
                yuYingDialog.show();
                break;
            case R.id.rl4:
                //识别阀值
                final XiuGaiSBFZDialog sbfzDialog = new XiuGaiSBFZDialog(SheZhiActivity.this);
                sbfzDialog.setContents(baoCunBean.getShibieFaZhi() + "", baoCunBean.getShibieFaceSize() + "");
                sbfzDialog.setOnQueRenListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            baoCunBean.setShibieFaZhi(Integer.valueOf(sbfzDialog.getFZ()));
                            baoCunBean.setShibieFaceSize(Integer.valueOf(sbfzDialog.getFaceSize()));
                            Log.d("SheZhiActivity", "baoCunBeanDao.put(baoCunBean):" + baoCunBeanDao.put(baoCunBean));

                            Log.d("SheZhiActivity", "baoCunBean.getShibieFaZhi():" + baoCunBean.getShibieFaZhi());
                            Log.d("SheZhiActivity", "baoCunBeanDao.get(123456).getShibieFaZhi():" + baoCunBeanDao.get(123456).getShibieFaZhi());
                            sbfzDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                sbfzDialog.setQuXiaoListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sbfzDialog.dismiss();
                    }
                });
                sbfzDialog.show();
                break;
            case R.id.rl5:
                final XiuGaiHuoTiFZDialog huoTiFZDialog = new XiuGaiHuoTiFZDialog(SheZhiActivity.this);
                huoTiFZDialog.setContents(baoCunBean.getHuoTiFZ() + "", null);
                huoTiFZDialog.setOnQueRenListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            baoCunBean.setHuoTiFZ(Integer.valueOf(huoTiFZDialog.getFaZhi()));
                            baoCunBeanDao.put(baoCunBean);
                            huoTiFZDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                huoTiFZDialog.setQuXiaoListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        huoTiFZDialog.dismiss();
                    }
                });
                huoTiFZDialog.show();

                break;
            case R.id.rl6:

                startActivity(new Intent(SheZhiActivity.this, SettingActivity.class));

                break;
            case R.id.rl7:
                final XiuGaiRuKuFZDialog dialog = new XiuGaiRuKuFZDialog(SheZhiActivity.this);
                dialog.setContents(baoCunBean.getRuKuFaceSize() + "", baoCunBean.getRuKuMoHuDu() + "");
                dialog.setOnQueRenListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            baoCunBean.setRuKuFaceSize(Integer.valueOf(dialog.getFZ()));
                            baoCunBean.setRuKuMoHuDu(Float.valueOf(dialog.getMoHuDu()));
                            baoCunBeanDao.put(baoCunBean);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.setQuXiaoListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.rl8:
                //选择城市


                break;

            case R.id.rl9:

                startActivity(new Intent(SheZhiActivity.this, YuLanActivity.class));

                break;



        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//解除订阅
        startActivity(new Intent(SheZhiActivity.this, MainActivity.class));
    }



    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        Toast tastyToast = TastyToast.makeText(SheZhiActivity.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();
    }








}
