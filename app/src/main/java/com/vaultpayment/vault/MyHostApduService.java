package com.vaultpayment.vault;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.cardemulation.HostApduService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Arrays;

public class MyHostApduService extends HostApduService {

    private String CHANNEL_ID = "NFCPaymentChannel";

    private int notificationId = 1;
    private NotificationManagerCompat notificationManager;

        private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");

        private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");

        @Override
        public byte[] processCommandApdu(byte[] apdu, Bundle extras) {

            notificationManager = NotificationManagerCompat.from(this);
            createNotificationChannel();
                AlertUser auTask = new AlertUser();
                auTask.execute();
                return null;
        }



//    private final BroadcastReceiver approveReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            notificationManager.cancel(notificationId);
////            String data = intent.getDataString();
////            if(data != null && data.equals("Approve")){
//                //TODO:Get tokenized account number
//                String account = "some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string data some string random data some string";
//                byte[] accountBytes = account.getBytes();
//                sendResponseApdu(ConcatArrays(accountBytes, SELECT_OK_SW));
////            }
////            else{
////                sendResponseApdu(UNKNOWN_CMD_SW);
////            }
//        }
//    };



//    private final BroadcastReceiver denyReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            notificationManager.cancel(notificationId);
////            String data = intent.getDataString();
////            if(data != null && data.equals("Approve")){
////                //TODO:Get tokenized account number
////                String account = "some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string data some string random data some string";
////                byte[] accountBytes = account.getBytes();
////                sendResponseApdu(ConcatArrays(accountBytes, SELECT_OK_SW));
////            }
////            else{
//                sendResponseApdu(UNKNOWN_CMD_SW);
////            }
//        }
//    };

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
//        }
    }
    private ApprovalReceiver approveReceiver;
    private class ApprovalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){

            notificationManager.cancel(notificationId);
            String data = intent.getAction();
            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG);
            if(data != null && data.equals("com.vaultpayment.vault.Approve")){
                //TODO:Get tokenized account number
                String account = "some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string data some string random data some string";
                byte[] accountBytes = account.getBytes();
                sendResponseApdu(ConcatArrays(accountBytes, SELECT_OK_SW));
            }
            else{
                sendResponseApdu(UNKNOWN_CMD_SW);
            }
        }
    }

        private class AlertUser extends AsyncTask<Void, Void, Void>{


            protected void onNewIntent(Intent intent){

                notificationManager.cancel(notificationId);
                String data = intent.getDataString();
                if(data != null && data.equals("Approve")){
                    String account = "some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string data some string random data some string";
                    byte[] accountBytes = account.getBytes();
                    sendResponseApdu(ConcatArrays(accountBytes, SELECT_OK_SW));
                }
                else{
                    sendResponseApdu(UNKNOWN_CMD_SW);
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                approveReceiver = new ApprovalReceiver();
                registerReceiver(approveReceiver, new IntentFilter("com.vaultpayment.vault.Approve"));
                registerReceiver(approveReceiver, new IntentFilter("com.vaultpayment.vault.Deny"));

                showNotification();
                return null;
            }

            public void showNotification(){
                Context appContext = getApplicationContext();
//                Intent approveIntent = new Intent(appContext, AlertUser.class);
                Intent approveIntent = new Intent(appContext, ApprovalReceiver.class);
                approveIntent.setData(Uri.parse("Approve"));
                approveIntent.setAction("com.vaultpayment.vault.Approve");
//                PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 0, approveIntent, Intent.FILL_IN_ACTION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, 4, approveIntent, PendingIntent.FLAG_ONE_SHOT);

//                Intent denyIntent = new Intent(appContext, AlertUser.class);
                Intent denyIntent = new Intent(appContext, ApprovalReceiver.class);
                denyIntent.setData(Uri.parse("deny"));
                denyIntent.setAction("com.vaultpayment.vault.Deny");
//                PendingIntent denyPendingIntent = PendingIntent.getActivity(appContext, 0, denyIntent, Intent.FILL_IN_ACTION);
                PendingIntent denyPendingIntent = PendingIntent.getBroadcast(appContext, 3, denyIntent, PendingIntent.FLAG_ONE_SHOT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_vault_logo)
                        .setContentTitle("Test Notification")
                        .setContentText("Test notification details")
                        .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.ic_vault_logo, getString(R.string.Approved),
                                pendingIntent)
                        .addAction(R.drawable.ic_vault_logo, getString(R.string.Deny),
                                denyPendingIntent);
//        Notification myNotification = builder.build();
//        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
// notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, builder.build());
            }
        }

        @Override
        public void onDeactivated(int reason) {
                unregisterReceiver(approveReceiver);
            notificationManager.cancel(notificationId);
        }

        public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
                int len = s.length();
                if (len % 2 == 1) {
                        throw new IllegalArgumentException("Hex string must have even number of characters");
                }
                byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
                for (int i = 0; i < len; i += 2) {
                        // Convert each character into a integer (base-16), then bit-shift into place
                        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i+1), 16));
                }
                return data;
        }

        public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
                int totalLength = first.length;
                for (byte[] array : rest) {
                        totalLength += array.length;
                }
                byte[] result = Arrays.copyOf(first, totalLength);
                int offset = first.length;
                for (byte[] array : rest) {
                        System.arraycopy(array, 0, result, offset, array.length);
                        offset += array.length;
                }
                return result;
        }


}
