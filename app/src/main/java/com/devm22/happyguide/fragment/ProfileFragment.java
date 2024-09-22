package com.devm22.happyguide.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devm22.happyguide.Config;
import com.devm22.happyguide.R;
import com.devm22.happyguide.activity.MainActivity;
import com.devm22.happyguide.activity.PrivacyActivity;
import com.devm22.happyguide.utils.Shared;
import com.devm22.happyguide.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.onesignal.OneSignal;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView textPageTitle;
    private RelativeLayout btnSettings, btnPrivacyPolicy, btnTerms, btnContactUs;
    private ImageView btnRate, btnShare;

    private Shared shared;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        textPageTitle = view.findViewById(R.id.text_title);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnPrivacyPolicy = view.findViewById(R.id.btnPrivacyPolicy);
        btnTerms = view.findViewById(R.id.btnTerms);
        btnContactUs = view.findViewById(R.id.btnContactUs);
        btnShare = view.findViewById(R.id.btnShare);
        btnRate = view.findViewById(R.id.btnRate);


        shared = new Shared(getContext());

        onClick();
        buildToolBar();

        return view;
    }

    private void onClick() {
        btnSettings.setOnClickListener(view -> {
            showDialogSettings();
        });

        btnPrivacyPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), PrivacyActivity.class);
            intent.putExtra("KEY_NAME", getResources().getString(R.string.text_privacy_policy));
            intent.putExtra("KEY_CONTENT", "file:///android_asset/PrivacyPolicy.html");
            startActivity(intent);
        });

        btnTerms.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), PrivacyActivity.class);
            intent.putExtra("KEY_NAME", getResources().getString(R.string.text_terms_conditions));
            intent.putExtra("KEY_CONTENT", "file:///android_asset/TermsConditions.html");
            startActivity(intent);
        });

        btnContactUs.setOnClickListener(view -> {
            showDialogContactUs();
        });

        btnRate.setOnClickListener(view -> {
            Utils.rateApp(getContext());
        });

        btnShare.setOnClickListener(view -> {
            Utils.shareApp(getContext());
        });
    }


    private void buildToolBar() {
        textPageTitle.setText(getContext().getResources().getString(R.string.text_nav_profile));
    }

    private void showDialogContactUs() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_contact_us);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText editTextName = dialog.findViewById(R.id.edit_name);
        EditText editTextEmail = dialog.findViewById(R.id.edit_email);
        EditText editTextMsg = dialog.findViewById(R.id.edit_msg);
        Button btnSend = dialog.findViewById(R.id.btn_send);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnSend.setOnClickListener(view -> {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String msg = editTextMsg.getText().toString();

            if (name.isEmpty()){
                Toast.makeText(getContext(), "Your name is empty", Toast.LENGTH_LONG).show();

            }else if (email.isEmpty()){
                Toast.makeText(getContext(), "Your email is empty", Toast.LENGTH_LONG).show();

            }else if (!Utils.isValidEmail(email)){
                Toast.makeText(getContext(), "Please! full valid email", Toast.LENGTH_LONG).show();

            }else if (msg.isEmpty()){
                Toast.makeText(getContext(), "Your message is empty", Toast.LENGTH_LONG).show();

            }else {
                dialog.dismiss();
                Utils.sendEmail((Activity) getContext(), email, "I am " + name, msg);

            }

        });

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });


        dialog.show();
    }


    private void showDialogSettings() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.dialog_settings);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        SwitchCompat switchTheme = dialog.findViewById(R.id.sw_theme);
        SwitchCompat switchNotifications = dialog.findViewById(R.id.sw_notifications);
        SeekBar seekBarTextSize = dialog.findViewById(R.id.seekbar_text_size);

        // Theme
        switchTheme.setChecked(shared.getBoolean(Config.MODE_NIGHT_YES, true));

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dialog.dismiss();
                if (b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    shared.putBoolean(Config.MODE_NIGHT_YES, true);

                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    shared.putBoolean(Config.MODE_NIGHT_YES, false);
                }
                requireActivity().finish();

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        //Notifications
        switchNotifications.setChecked(OneSignal.getDeviceState().isPushDisabled());

        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                OneSignal.disablePush(b);

            }
        });


        //SeekBar Text Size
        seekBarTextSize.setMax(2);

        float aFloatTextSize = shared.getFloat(Config.SharedTextContentSize, getResources().getDimension(R.dimen.content_text_size_moy));
        if (aFloatTextSize == getResources().getDimension(R.dimen.content_text_size_min)){
            seekBarTextSize.setProgress(0);

        }else if (aFloatTextSize == getResources().getDimension(R.dimen.content_text_size_max)){
            seekBarTextSize.setProgress(2);

        }else {
            seekBarTextSize.setProgress(1);

        }

        LayerDrawable drawable = (LayerDrawable) seekBarTextSize.getThumb();
        ScaleDrawable innerDrawable = (ScaleDrawable) drawable.findDrawableByLayerId(R.id.seek_bar_thumb_inner_ring);
        innerDrawable.setTint(getResources().getColor(R.color.white));
        innerDrawable.setLevel((seekBarTextSize.getProgress() + 1) * 600);


        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                LayerDrawable drawable = (LayerDrawable) seekBarTextSize.getThumb();
                ScaleDrawable innerDrawable = (ScaleDrawable)drawable.findDrawableByLayerId(R.id.seek_bar_thumb_inner_ring);
                innerDrawable.setLevel((seekBar.getProgress() + 1) * 600);

                if (seekBar.getProgress() == 0){
                    shared.putFloat(Config.SharedTextContentSize, getResources().getDimension(R.dimen.content_text_size_min));
                }else if (seekBar.getProgress() == 2){
                    shared.putFloat(Config.SharedTextContentSize, getResources().getDimension(R.dimen.content_text_size_max));
                }else {
                    shared.putFloat(Config.SharedTextContentSize, getResources().getDimension(R.dimen.content_text_size_moy));

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        dialog.show();
    }


}