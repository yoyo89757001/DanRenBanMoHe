package com.xiaojun.danrenbanmohe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.xiaojun.danrenbanmohe.R;


/**
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 * @author Tom.Cai
 */
public class BangDingDialog extends Dialog {
   // private TextView title2;
    private Button l1,l2;
    private EditText jiudianname,idid;
    public BangDingDialog(Context context) {
        super(context, R.style.dialog_style2);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.xiugaidialog_bangding, null);

        jiudianname= (EditText) mView.findViewById(R.id.xiangce);
        idid= (EditText)mView.findViewById(R.id.idid);
        idid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
              int len=s.toString().length();
                StringBuilder builder=new StringBuilder();
                if (len>0){
                    if (len==4){
                        builder.append(idid.getText().toString().trim());
                        builder.append("-");
                        idid.setText(builder.toString());
                        idid.setSelection(idid.getText().toString().trim().length());
                    }else if (len==9){
                        builder.append(idid.getText().toString().trim());
                        builder.append("-");
                        idid.setText(builder.toString());
                        idid.setSelection(idid.getText().toString().trim().length());
                    }else if (len==14){
                        builder.append(idid.getText().toString().trim());
                        builder.append("-");
                        idid.setText(builder.toString());
                        idid.setSelection(idid.getText().toString().trim().length());
                    }else if (len==19){
                        builder.append(idid.getText().toString().trim());
                        builder.append("-");
                        idid.setText(builder.toString());
                        idid.setSelection(idid.getText().toString().trim().length());
                    }


                }
            }
        });
       // title2= (TextView) mView.findViewById(R.id.title2);
        l1= (Button)mView. findViewById(R.id.queren);
        l2= (Button) mView.findViewById(R.id.quxiao);

        super.setContentView(mView);
    }

    public void setContents(String ss, String s3){
       if (ss!=null)
           idid.setText(ss);
        if (s3!=null)
            jiudianname.setText(s3);

    }

    public String getZhuCeMa(){
        return idid.getText().toString().trim();
    }



    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnQueRenListener(View.OnClickListener listener){
        l1.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setQuXiaoListener(View.OnClickListener listener){
        l2.setOnClickListener(listener);
    }


}
