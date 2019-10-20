package com.example.ultraman.bluetoothprinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.printservice.PrintService;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.PDF417;

import com.github.anastaciocintra.output.PrinterOutputStream;
import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.GeneratedBarcodeInfo;
import com.onbarcode.barcode.android.IBarcode;
//import com.onbarcode.barcode.android.PDF417;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity extends Activity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;


    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;


    TextView stat;
    int printstat;

    LinearLayout layout;


    private ProgressDialog loading;


    AlertDialog.Builder builder;

    EditText customer_dtl,order_detail,total_price, agent_detail;


    TextView tvid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"fonts/abel-regular.ttf", true );
        stat = (TextView)findViewById(R.id.bpstatus);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        layout = (LinearLayout)findViewById(R.id.layout);

        customer_dtl = (EditText)findViewById(R.id.et_customer_details);
        order_detail = (EditText)findViewById(R.id.et_order_summary);
        total_price = (EditText)findViewById(R.id.et_total_price);
        agent_detail = (EditText)findViewById(R.id.et_agent_details);

        mScan = (Button)findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {

                if(mScan.getText().equals("Connect"))
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(MainActivity.this, "Message1", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,
                                    REQUEST_ENABLE_BT);
                        } else {
                            ListPairedDevices();
                            Intent connectIntent = new Intent(MainActivity.this,
                                    DeviceListActivity.class);
                            startActivityForResult(connectIntent,
                                    REQUEST_CONNECT_DEVICE);

                        }
                    }

                }
                else if(mScan.getText().equals("Disconnect"))
                {
                    if (mBluetoothAdapter != null)
                        mBluetoothAdapter.disable();
                    stat.setText("");
                    stat.setText("Disconnected");
                    stat.setTextColor(Color.rgb(199, 59, 59));
                    mPrint.setEnabled(false);
                    mScan.setEnabled(true);
                    mScan.setText("Connect");
                }
            }
        });






        mPrint = (Button) findViewById(R.id.mPrint);
        //mPrint.setTypeface(custom);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {


                p1();

                int TIME = 10000; //5000 ms (5 Seconds)

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //p2(); //call function!

                        printstat = 1;
                    }
                }, TIME);


            }
        });

        /*mDisc = (Button) findViewById(R.id.dis);
        mDisc.setTypeface(custom);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                //connectDisconnect(mView);
                *//*if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
                stat.setText("");
                stat.setText("Disconnected");
                stat.setTextColor(Color.rgb(199, 59, 59));
                mPrint.setEnabled(false);
                mDisc.setEnabled(false);
                mDisc.setBackgroundColor(Color.rgb(161, 161, 161));
                mPrint.setBackgroundColor(Color.rgb(161, 161, 161));
                mScan.setBackgroundColor(Color.rgb(0,0,0));
                mScan.setEnabled(true);*//*


            }
        });*/

       /* final ScrollView sv = (ScrollView)findViewById(R.id.sv);
        final LinearLayout l1 = (LinearLayout)findViewById(R.id.l1);
        final LinearLayout l2 = (LinearLayout)findViewById(R.id.l2);
        final LinearLayout l3 = (LinearLayout)findViewById(R.id.l3);

        sv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (sv != null) {
                    if (sv.getChildAt(0).getBottom() <= (sv.getHeight() + sv.getScrollY())) {

                        l1.setVisibility(View.VISIBLE);
                        l2.setVisibility(View.VISIBLE);
                        l3.setVisibility(View.VISIBLE);

                    }
                    else {
                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        l3.setVisibility(View.GONE);


                    }
                }
            }
        });*/
    }//oncreate


    public void connectDisconnect(View view)
    {
        if(mScan.getText().toString() == "Connect")
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(MainActivity.this, "Message1", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);
                }
            }


        }else{
            if (mBluetoothAdapter != null)
            {
                mBluetoothAdapter.disable();
            }
            else{
                stat.setText("");
                stat.setText("Disconnected");
                stat.setTextColor(Color.rgb(199, 59, 59));
                mPrint.setEnabled(false);
                /*mDisc.setEnabled(false);
                mDisc.setBackgroundColor(Color.rgb(161, 161, 161));*/
                //mPrint.setBackgroundColor(Color.rgb(161, 161, 161));
                mScan.setBackgroundColor(Color.rgb(0,0,0));
                mScan.setEnabled(true);
                mScan.setText("Disconnect");
            }
        }

    }

    public void p1(){

        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();

                    EscPos escpos;
                    escpos = new EscPos(os);
                    escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.ISO8859_15_Latin9);
                    Style title = new Style()
                            .setFontSize(Style.FontSize._3, Style.FontSize._3)
                            .setColorMode(Style.ColorMode.WhiteOnBlack)
                            .setJustification(EscPosConst.Justification.Center);
                    escpos.write(title, "PDF 417 Tamaño: "+order_detail.getText().toString());
                    escpos.feed(1);
                    PDF417 pdf417 = new PDF417();
                    int altura = Integer.parseInt(order_detail.getText().toString());
                    pdf417.setHeight(altura);
                    escpos.write(pdf417, customer_dtl.getText().toString());
                    escpos.feed(2);
                    escpos.cut(EscPos.CutMode.FULL);
                    escpos.close();



                } catch (Exception e) {
                    Log.e("PrintActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    public void p2(){

        Thread tt = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    String header = "";
                    String he = "";
                    String header2 = "";
                    String BILL = "";
                    String vio = "";
                    String header3 = "";
                    String mvdtail = "";
                    String header4 = "" ;
                    String offname = "";
                    String copy = "";
                    String checktop_status = "";

                    he = "      SAMPLE PRINT\n";
                    he = he +"********************************\n\n";

                    header =  "CUSTOMER DETAILS:\n";
                    BILL = customer_dtl.getText().toString()+"\n";
                    BILL = BILL
                            + "================================\n";
                    header2= "ORDER DETAILS:\n";
                    vio = order_detail.getText().toString()+"\n";
                    vio = vio
                            + "================================\n";
                    header3 = "TOTAL PRICE:\n";
                    mvdtail = total_price.getText().toString()+"\n";
                    mvdtail = mvdtail
                            + "================================\n";

                    header4 = "AGENT DETAILS:\n";
                    offname = agent_detail.getText().toString()+"\n";
                    offname = offname
                            + "--------------------------------\n";
                    copy = "-Agents's Copy\n\n\n\n\n";




                    os.write(he.getBytes());
                    os.write(header.getBytes());
                    os.write(BILL.getBytes());
                    os.write(header2.getBytes());
                    os.write(vio.getBytes());
                    os.write(header3.getBytes());
                    os.write(mvdtail.getBytes());
                    os.write(header4.getBytes());
                    os.write(offname.getBytes());
                    os.write(checktop_status.getBytes());
                    os.write(copy.getBytes());



                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("PrintActivity", "Exe ", e);
                }
            }
        };
        tt.start();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }


    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(MainActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();

            // Snackbar snackbar = Snackbar.make(layout, "Bluetooth Printer is Connected!", Snackbar.LENGTH_LONG);
            // snackbar.show();
            stat.setText("");
            stat.setText("Connected");
            stat.setTextColor(Color.rgb(97, 170, 74));
            mPrint.setEnabled(true);
            mScan.setText("Disconnect");
            //mDisc.setEnabled(true);
            //mDisc.setBackgroundColor(Color.rgb(0, 0, 0));
            //mScan.setEnabled(false);
            //mScan.setBackgroundColor(Color.rgb(161, 161, 161));

        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }




   
}
