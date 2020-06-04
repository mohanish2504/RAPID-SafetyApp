package com.example.safetyapp.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.safetyapp.R;

import java.net.ConnectException;
import java.util.ArrayList;

public class report_dialog extends AppCompatDialogFragment {

    private static String TAG = report_dialog.class.getSimpleName();


    public FragmentManager newInstance() {
        return getChildFragmentManager();
    }

    public interface onMultiChoiceListener{
        void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemList);
        void onNegativeButtonClicked();
    }

    onMultiChoiceListener mListener;
    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        setlistener(context);
    }
    private void setlistener(Context context){
        if(mListener==null) {
            Log.d(TAG,String.valueOf(context));
            try {
                mListener = (onMultiChoiceListener) context;
            } catch (Exception e) {
                Log.d(TAG,e.getMessage());
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        setlistener(context);
        final ArrayList<String> selectedItemList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        final String[] list = getActivity().getResources().getStringArray(R.array.report_reason);


        builder.setTitle("Report User")
                .setMultiChoiceItems(list, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if(isChecked){
                            selectedItemList.add(list[which]);
                        }
                        else{
                            selectedItemList.remove(list[which]);
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeButtonClicked();
                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveButtonClicked(list, selectedItemList);
                    }
                });
        return  builder.create();
    }

    @Nullable
    @Override
    public Dialog getDialog() {
        return super.getDialog();
    }
}
