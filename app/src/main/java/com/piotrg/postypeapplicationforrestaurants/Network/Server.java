package com.piotrg.postypeapplicationforrestaurants.Network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.piotrg.postypeapplicationforrestaurants.Data.SingleOrder;
import com.piotrg.postypeapplicationforrestaurants.Helper.ActiveOrdersHelper;
import com.piotrg.postypeapplicationforrestaurants.Helper.IOOperations;
import com.piotrg.postypeapplicationforrestaurants.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
    private final static String TAG = "NET_Server";

    private ServerSocket mServerSocket = null;
    private int mPort;
    private Context mCtx;
    private Thread mServerThread;
    private ArrayList<Thread> clientsThreads;

    private Gson gson;

    public Server(Context ctx) {
        this.clientsThreads = new ArrayList<>();
        this.gson = new Gson();
        this.mServerThread = new Thread(new ServerThread());

        this.mPort = -1;
        this.mCtx = ctx;

        this.mServerThread.start();
    }

    public void tearDown() {
        mServerThread.interrupt();

        for(Thread t : clientsThreads){
            t.interrupt();
        }
        try {
            mServerSocket.close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing server socket.");
            ioe.printStackTrace();
        }finally {
            mServerSocket = null;
        }
    }

    public int getmPort() {
        return mPort;
    }
    public void setmPort(int mPort) {
        this.mPort = mPort;
    }

    private void cleanUpClientsThreads(){
        Iterator<Thread> i = clientsThreads.iterator();
        while(i.hasNext()){
            if(!i.next().isAlive()){
                Log.d(TAG, "Server removing non active connection " + i.next().getName());
                i.remove();
            }
        }
    }

    //server listener thread implementation
    private class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(0);
                setmPort(mServerSocket.getLocalPort());
                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "ServerSocket Created on port " + mPort + ", awaiting connection");
                    Socket thisSocket = mServerSocket.accept();
                    Log.d(TAG, "Someone connected.");

                    Thread t = new Thread(new ServerClientThread(thisSocket));
                    t.setName(thisSocket.getInetAddress().toString());
                    clientsThreads.add(t);
                    t.start();

                    cleanUpClientsThreads();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }

    //server client-handler thread
    private class ServerClientThread implements Runnable {
        private Socket clientSocket;

        public ServerClientThread(Socket socket){
            Log.d(TAG, "Server-side client socket initialization.");
            this.clientSocket = socket;
        }

        private void handleReceivedMessage(String code, String content){
            switch(code){
                case NetworkConnection.CODES.GET_MENU:
                    String reply = "";
                    String MenuFileName = mCtx.getResources().getString(R.string.configMenu_data_MenuFileName);
                    File directory = mCtx.getApplicationContext().getFilesDir();
                    File file = new File(directory, MenuFileName);
                    if (file.exists()) {
                        Log.d(TAG, "Server read menu!");
                        String ReadMenu = IOOperations.readFile(mCtx, MenuFileName);
                        if(ReadMenu != "fail"){
                            reply = ReadMenu;
                        }
                    }
                    sendMessage(reply);
                    break;

                case NetworkConnection.CODES.NEW_ACTIVE_ORDER:
                    if(content != ""){
                        Log.d(TAG, "Server read order! " + content);
                        SingleOrder SO = gson.fromJson(content, SingleOrder.class);
                        ActiveOrdersHelper.getInstance().addOrderToActiveOrdersList(SO);
                    }
                    break;

                case NetworkConnection.CODES.GET_ACTIVE_ORDERS:
                    reply = gson.toJson(ActiveOrdersHelper.getInstance().getMyActiveOrdersList(), new TypeToken<ArrayList<SingleOrder>>(){}.getType());
                    sendMessage(reply);
                    break;


                case NetworkConnection.CODES.CLIENT_CLOSING_CONNECTION:
                    tearDown();
                    break;
            }
        }

        public void sendMessage(String msg) {
            try {
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        clientSocket.getOutputStream())), true);

                out.println(msg);
                out.flush();

                Log.d(TAG, "Server sent msg to client (" +
                        clientSocket.getInetAddress() + ") " + msg);
            } catch (UnknownHostException e) {
                Log.d(TAG, "Unknown Host", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "I/O Exception", e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.d(TAG, "Error3", e);
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "Server-side client socket initialized.");
            BufferedReader input;
            try {
                input = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                while (!Thread.currentThread().isInterrupted()) {
                    String messageStr;
                    messageStr = input.readLine();
                    if (messageStr != null && !messageStr.equals("")) {
                        Log.d(TAG, "Read from " + clientSocket.getInetAddress() + " : " + messageStr);

                        Log.d(TAG, "Server handling received message (Len: " + messageStr.length() + ").");

                        int beginCodeIndex = 0;
                        int endCodeIndex = 3;

                        if(messageStr.length() >= endCodeIndex){
                            String code = messageStr.substring(beginCodeIndex, endCodeIndex);
                            String content = messageStr.substring(endCodeIndex);
                            Log.d(TAG, "Server handling code: " + code);
                            handleReceivedMessage(code, content);
                        }
                    }
                }
                input.close();
            } catch (IOException e) {
                Log.e(TAG, "ClientServerThread loop error: ", e);
                e.printStackTrace();
            }

        }

        public void tearDown() {
            try {
                Log.d(TAG, "Closing socket!");
                clientSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Error when closing socket.");
                ioe.printStackTrace();
            } finally {
                clientSocket = null;
                Thread.currentThread().interrupt();
            }
        }
    }
}