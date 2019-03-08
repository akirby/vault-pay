package com.vaultpayment.vault;

import android.nfc.cardemulation.HostApduService;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.Arrays;

public class MyHostApduService extends HostApduService {


        private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");

        private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");

        @Override
        public byte[] processCommandApdu(byte[] apdu, Bundle extras) {

                AlertUser auTask = new AlertUser();
                auTask.execute();
                return null;
        }

        private class AlertUser extends AsyncTask<Void, Void, Void>{


            @Override
            protected Void doInBackground(Void... voids) {
                //TODO: Notify User of transaction and await confirmation
                boolean isApproved = false;
                if(isApproved){
                    //TODO get account from Bank API and send to the requestor.
                    String account = "some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string random data some string data some string random data some string";
                    byte[] accountBytes = account.getBytes();
                    sendResponseApdu(ConcatArrays(accountBytes, SELECT_OK_SW));
                }
                else{
                    sendResponseApdu(UNKNOWN_CMD_SW);
                }
                return null;
            }
        }

        @Override
        public void onDeactivated(int reason) {
                int i = 1;

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
