package com.piotrg.postypeapplicationforrestaurants.Network;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Client {
    private final static String TAG = "NET_Client";

    private Socket mSocket;
    private InetAddress mAddress;
    private int mPort;
    private Thread mSendThread;
    private Thread mRecThread;

    BlockingQueue<String> mSendingMessageQueue;
    private BlockingQueue<String> mReceivedMessagesQueue;
    private final int QUEUE_CAPACITY = 10;

    public Client(InetAddress address, int port) {
        Log.d(TAG, "Constructor.");

        this.mReceivedMessagesQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.mSendingMessageQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.mSendThread = new Thread(new SendingThread());
        this.mRecThread = new Thread(new ReceivingThread());

        this.mAddress = address;
        this.mPort = port;

        mSendThread.start();
    }

    private synchronized void setSocket(Socket socket) {
        Log.d(TAG, "setSocket called.");
        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }



    public void tearDown() {
        // be nice, tell server we shutting down
        sendMessage(NetworkConnection.CODES.CLIENT_CLOSING_CONNECTION);
        try {
            Log.d(TAG, "Closing socket!");
            mSocket.close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing socket.");
            ioe.printStackTrace();
        }
    }


    public void sendMessage(String text){
        // sending thread will take it from here
        mSendingMessageQueue.add(text);
    }
    public String getReceivedMessage(){
        String msg = "";
        try {
            msg = mReceivedMessagesQueue.poll(10, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(msg == null){
                msg = "";
            }
        }
        Log.d(TAG, "Returning received message " + msg);
        return msg;
    }
    private void handleReceivedMessage(String messageStr) {
        Log.d(TAG, "Handling message.");
        if(messageStr.length() > 0){
            Log.d(TAG, "Adding to ReceivedMessageQueue" + messageStr);
            mReceivedMessagesQueue.add(messageStr);
        }
    }






    class SendingThread implements Runnable {
        @Override
        public void run() {
            //try and initialize socket
            try {
                if(mSocket != null){

                }else{
                    mSocket = new Socket(mAddress, mPort);
                }
                mRecThread.start();
            } catch (UnknownHostException e) {
                Log.e(TAG, "Initializing socket failed, UHE", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Initializing socket failed, IOE.", e);
                e.printStackTrace();
            }
            //main loop
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String msg = mSendingMessageQueue.take();
                    sendMessage(msg);
                } catch (InterruptedException ie) {
                    Log.e(TAG, "Message sending loop interrupted, exiting");
                    ie.printStackTrace();
                }
            }
        }

        private void sendMessage(String msg) {
            try {
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(mSocket.getOutputStream())), true);
                out.println(msg);
                out.flush();
                Log.d(TAG, "Client sent message: " + msg);
            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown Host", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "I/O Exception", e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "Error3", e);
                e.printStackTrace();
            }
        }
    }


    class ReceivingThread implements Runnable {

        @Override
        public void run() {
            BufferedReader input;
            try {
                input = new BufferedReader(new InputStreamReader(
                        mSocket.getInputStream()));

                //main loop
                while (!Thread.currentThread().isInterrupted()) {
                    String messageStr;
                    //wait for input
                    messageStr = input.readLine();

                    //we got something!
                   // Log.d(TAG, "Client received something!");
                    if (messageStr != null) {
                        Log.d(TAG, "Read from the stream: " + messageStr);
                        //handle received message
                        handleReceivedMessage(messageStr);
                    }
                }

                input.close();
            } catch (IOException e) {
                Log.e(TAG, "Client loop error: ", e);
                e.printStackTrace();
            }
        }
    }
}
