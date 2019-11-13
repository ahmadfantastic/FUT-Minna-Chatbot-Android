package com.fut.chatbot.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fut.chatbot.R;
import com.fut.chatbot.util.PatternChecker;
import com.fut.chatbot.viewmodel.VerificationViewModel;

public class CodeVerificationFragment extends Fragment {

    private Context context;
    private VerificationViewModel viewModel;
    private int counterDuration = 90;

    private OnCodeVerificationFragmentInteractionListener mListener;

    public CodeVerificationFragment(){}

    public void init(Context context, VerificationViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void setCounterDuration(int seconds){
        this.counterDuration = seconds;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);

        EditText edtCode = view.findViewById(R.id.edt_code);
        TextView txtPhone = view.findViewById(R.id.txt_phone);
        TextView txtTimer = view.findViewById(R.id.txt_timer);
        Button btnVerify = view.findViewById(R.id.btn_verify);
        Button btnChange = view.findViewById(R.id.btn_change);
        Button btnResend = view.findViewById(R.id.btn_resend);

        txtPhone.setText(viewModel.getUser().getPhone());

        viewModel.setTimer(new CountDownTimer(counterDuration * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished /1000;
                long totalMinutes = totalSeconds / 60;
                long remainingSeconds = totalSeconds % 60;
                txtTimer.setText(context.getString(R.string.format_timer, totalMinutes, remainingSeconds));
            }
            public void onFinish() {
                btnResend.setEnabled(true);
                btnResend.setTextColor(Color.parseColor("#03A9F4"));
            }
        });
        viewModel.getTimer().start();

        btnResend.setTextColor(Color.parseColor("#999999"));
        btnResend.setEnabled(false);
        btnResend.setOnClickListener(view1 -> mListener.onResendClicked());

        btnChange.setOnClickListener(view1 -> mListener.onChangePhoneClicked());

        btnVerify.setOnClickListener(view1 -> {
            String code = edtCode.getText().toString();
            if(PatternChecker.checkCode(code)){
                if(code.equals(viewModel.getUser().getCode())) {
                    mListener.onVerifyCodeClicked(code);
                }else{
                    edtCode.setError(context.getString(R.string.error_wrong_code));
                    edtCode.requestFocus();
                }
            }else{
                edtCode.setError(context.getString(R.string.error_invalid_code));
                edtCode.requestFocus();
            }
        });

        return  view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCodeVerificationFragmentInteractionListener) {
            mListener = (OnCodeVerificationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCodeVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCodeVerificationFragmentInteractionListener {
        void onVerifyCodeClicked(String code);

        void onChangePhoneClicked();

        void onResendClicked();
    }
}
