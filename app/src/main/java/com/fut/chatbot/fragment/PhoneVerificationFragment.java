package com.fut.chatbot.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fut.chatbot.R;
import com.fut.chatbot.util.PatternChecker;

public class PhoneVerificationFragment extends Fragment {

    private Context context;

    private OnPhoneVerificationFragmentInteractionListener mListener;

    private EditText edtPhone;
    private String phone;

    public PhoneVerificationFragment(){}

    public void init(Context context) {
        this.context = context;
    }

    public void setPhone(String phone){
        this.phone = phone;
        if(edtPhone != null){
            edtPhone.setText(phone);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_verification, container, false);
        Button btnStart = view.findViewById(R.id.btn_start);
        edtPhone = view.findViewById(R.id.edt_phone);
        if(phone != null){
            edtPhone.setText(phone);
        }

        btnStart.setOnClickListener(view1 -> {
            String phone = edtPhone.getText().toString();
            if(PatternChecker.checkPhone(phone)) {
                mListener.onVerifyPhoneClicked(phone);
            }else{
                edtPhone.setError(context.getString(R.string.error_invalid_phone));
                edtPhone.requestFocus();
            }
        });
        return  view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPhoneVerificationFragmentInteractionListener) {
            mListener = (OnPhoneVerificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhoneVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPhoneVerificationFragmentInteractionListener {
        void onVerifyPhoneClicked(String phone);
    }
}
