package shsustudent.andrewkirby.vault;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

public class MyHostApduService extends HostApduService {

        @Override
        public byte[] processCommandApdu(byte[] apdu, Bundle extras) {


                return null;
        }
        @Override
        public void onDeactivated(int reason) {

        }

}
