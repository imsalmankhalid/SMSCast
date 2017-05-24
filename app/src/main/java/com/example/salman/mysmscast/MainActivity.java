package com.example.salman.mysmscast;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txtnum, txttotal, txtstep,txtmsg;
    private TextView tv_Total, tv_Status;
    Button btn_Send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtnum = (EditText)findViewById(R.id.txtNum);
        txttotal = (EditText)findViewById(R.id.txtTotal);
        txtmsg = (EditText)findViewById(R.id.txtMsg);
        txtstep = (EditText)findViewById(R.id.txtStep);
        tv_Total = (TextView)findViewById(R.id.Total_num);
        tv_Status = (TextView)findViewById(R.id.txtStatus);
        btn_Send = (Button)findViewById(R.id.button);

        txttotal.addTextChangedListener(mTextEditorWatcher);
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup group = (ViewGroup)findViewById(R.id.layout);
                for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                    View view = group.getChildAt(i);
                    if (view instanceof EditText) {
                        EditText et = (EditText)view;
                        if(et.getText().toString().trim().equalsIgnoreCase("")){
                            et.setError("This field can not be blank");
                        }
                    }
                }
                if(txtnum.length() >10 && txttotal.length() > 0 && txtmsg.length() > 0 && txtstep.length() > 0)
                {
                    AsyncTaskRunner runner = new AsyncTaskRunner(txtnum.getText().toString(),txtmsg.getText().toString()
                                                                ,txttotal.getText().toString(), txtstep.getText().toString());
                    runner.execute();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Please enter valid details",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            if(count > 0)
            tv_Total.setText(String.valueOf(txttotal.getText()));
        }

        public void afterTextChanged(Editable s) {
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp, number, msg;
        private int num_step, max_nums, rem;
        private long  cur_num, m_number;
        AsyncTaskRunner( String m_num, String msg_, String max, String step)
        {
            num_step = Integer.parseInt(step);
            max_nums = Integer.parseInt(max);
            m_number = Long.parseLong(m_num);
            msg = msg_;
            rem = max_nums;
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                for(int i=0; i < max_nums; i++ )
                {
                    sendSMS(next_num(i),msg);
                    publishProgress(""); // Calls onProgressUpdate()
                    Thread.sleep(10000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        private String next_num(int i)
        {
            if(i > 0) {
                m_number += num_step;
            }
            cur_num = m_number;
            return( number = String.format("%011d",m_number));
        }

        private void sendSMS(String phoneNumber, String message) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            rem -=1;
        }


        @Override
        protected void onPostExecute(String result) {

            tv_Status.setText(result);
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {
            tv_Status.setText("Current Number: 0"+cur_num+"\nSent Messages: "+rem);

        }
    }
}

