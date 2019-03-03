package shsustudent.andrewkirby.vault;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class PaymentMethodFragment extends Fragment {
    private static final String TAG = "PaymentMethodFragment";

    private Button btnTest;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.paymentmethodfragment, container, false);
        btnTest = (Button) view.findViewById(R.id.btnTest);

        btnTest.setOnClickListener((View btnView) -> {
            Toast.makeText(getActivity(), "Testing Button Click 1", Toast.LENGTH_SHORT).show();
        } );
        return view;
    }
}
