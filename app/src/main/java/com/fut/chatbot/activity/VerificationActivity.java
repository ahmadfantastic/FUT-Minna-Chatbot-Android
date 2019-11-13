package com.fut.chatbot.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.fut.chatbot.database.AppDatabase;
import com.fut.chatbot.fragment.CodeVerificationFragment;
import com.fut.chatbot.fragment.PhoneVerificationFragment;
import com.fut.chatbot.model.User;
import com.fut.chatbot.retrofit.Result;
import com.fut.chatbot.util.Constants;
import com.fut.chatbot.viewmodel.VerificationViewModel;
import com.fut.chatbot.viewmodel.VerificationViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fut.chatbot.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity implements
        PhoneVerificationFragment.OnPhoneVerificationFragmentInteractionListener,
        CodeVerificationFragment.OnCodeVerificationFragmentInteractionListener {

    private SharedPreferences sharedPref;
    private VerificationViewModel viewModel;

    private PhoneVerificationFragment phoneVerificationFragment;
    private CodeVerificationFragment codeVerificationFragment;
    private FragmentManager fragmentManager;

    private View mProgressView;
    private View mFrameView;

    private Snackbar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        TextView titleView = toolbar.findViewById(R.id.title);
        titleView.setText(R.string.prompt_phone_verification);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        AppDatabase mDb = AppDatabase.getInstance(this);
        viewModel = ViewModelProviders.of(this, new VerificationViewModelFactory(mDb))
                .get(VerificationViewModel.class);

        phoneVerificationFragment = new PhoneVerificationFragment();
        phoneVerificationFragment.init(this);

        codeVerificationFragment = new CodeVerificationFragment();
        codeVerificationFragment.init(this, viewModel);

        mFrameView = findViewById(R.id.content_frame);
        mProgressView = findViewById(R.id.progress_bar);

        fragmentManager = getSupportFragmentManager();
        mSnackBar = Snackbar.make(mFrameView, "", Snackbar.LENGTH_SHORT);

        String phone = sharedPref.getString(getString(R.string.prompt_phone), null);
        String code = sharedPref.getString(getString(R.string.prompt_code), null);

        if(phone != null && code != null){
            User user = new User();
            user.setPhone(phone);
            user.setCode(code);
            viewModel.setUser(user);
            codeVerificationFragment.setCounterDuration(60);
            loadCodeVerification();
        }else {
            loadPhoneVerification();
        }
    }

    private void loadPhoneVerification(){
        showProgress(false);
        cancelTimer();
        fragmentManager.beginTransaction().replace(R.id.content_frame, phoneVerificationFragment).commitAllowingStateLoss();
    }

    private void loadCodeVerification(){
        showProgress(false);
        fragmentManager.beginTransaction().replace(R.id.content_frame, codeVerificationFragment).commitAllowingStateLoss();
    }

    private void gotoChatbot(){
        cancelTimer();
        Intent intent = new Intent(this, ChatbotActivity.class);
        startActivity(intent);
        finish();
    }

    //cancel timer
    private void cancelTimer() {
        if(viewModel.getTimer()!=null)
            viewModel.getTimer().cancel();
    }

    @Override
    public void onVerifyPhoneClicked(String phone) {
        showProgress(true);
        viewModel.setUser(null);
        Call<Result> resultCall = viewModel.getUserClient().activatePhone(Constants.KEY, phone);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if(response.isSuccessful() && response.body() != null){
                    Result result = response.body();
                    if(result.getStatus() == Result.Status.SUCCESS){
                        User user =  Constants.GSON.fromJson(Constants.GSON.toJson(result.getData()), User.class);
                        viewModel.setUser(user);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.prompt_phone), user.getPhone());
                        editor.putString(getString(R.string.prompt_code), user.getCode());
                        editor.apply();
                        codeVerificationFragment.setCounterDuration(90);
                        loadCodeVerification();
                    }else{
                        showProgress(false, result.getData().toString());
                    }
                }else{
                    showProgress(false, R.string.error_network_failure);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                showProgress(false, R.string.error_network_failure);
                Log.e("VERIFICATION", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onVerifyCodeClicked(String code) {
        showProgress(true);
        Call<Result> resultCall = viewModel.getUserClient().activateCode(Constants.KEY, viewModel.getUser().getPhone(), code);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if(response.isSuccessful() && response.body() != null){
                    Result result = response.body();
                    if(result.getStatus() == Result.Status.SUCCESS){
                        User user = Constants.GSON.fromJson(Constants.GSON.toJson(result.getData()), User.class);
                        viewModel.setUser(user);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.prompt_verify), true);
                        editor.apply();
                        showProgress(false);
                        gotoChatbot();
                    }else{
                        showProgress(false, result.getData().toString());
                    }
                }else{
                    showProgress(false, R.string.error_network_failure);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                showProgress(false, R.string.error_network_failure);
                Log.e("VERIFICATION", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onChangePhoneClicked() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getString(R.string.prompt_phone));
        editor.remove(getString(R.string.prompt_code));
        editor.apply();

        loadPhoneVerification();
        phoneVerificationFragment.setPhone(viewModel.getUser().getPhone());
        viewModel.setUser(null);
    }

    @Override
    public void onResendClicked() {
        cancelTimer();
        loadPhoneVerification();
        onVerifyPhoneClicked(viewModel.getUser().getPhone());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.verification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProgress(final boolean show) {
        showProgress(show, -1);
    }

    private void showProgress(final boolean show, int messageId) {
        showProgress(show, messageId < 0 ? null :getString(messageId));
    }

    private void showProgress(final boolean show, String message) {
        if(message != null && mSnackBar != null) {
            if (mSnackBar.isShown()) {
                mSnackBar.dismiss();
            }
            mSnackBar.setText(message);
            mSnackBar.show();
        }
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mFrameView.setVisibility(show ? View.GONE : View.VISIBLE);
        mFrameView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
