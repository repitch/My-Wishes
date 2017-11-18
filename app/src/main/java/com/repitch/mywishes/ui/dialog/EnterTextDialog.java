package com.repitch.mywishes.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.repitch.mywishes.R;

/**
 * @author Dmitriy Tarasov
 */
public class EnterTextDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_HINT = "hint";
    private static final String ARG_DEFAULT = "default";
    private static final String ARG_MAX_LENGTH = "maxLength";

    private OnEnterTextListener listener;

    private EditText editText;

    public static EnterTextDialog newInstance(String title, String hint, String defaultValue, int maxLength) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_HINT, hint);
        args.putString(ARG_DEFAULT, defaultValue);
        args.putInt(ARG_MAX_LENGTH, maxLength);

        EnterTextDialog dialog = new EnterTextDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String hint = args.getString(ARG_HINT);

        String positive = getString(R.string.ok);
        String negative = getString(R.string.cancel);
        String defaultValue = args.getString(ARG_DEFAULT);

        View content = View.inflate(getActivity(), R.layout.dialog_enter_text, null);
        editText = content.findViewById(R.id.text);
        editText.setHint(hint);
        if (!TextUtils.isEmpty(defaultValue)) {
            editText.setText(defaultValue);
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(args.getInt(ARG_MAX_LENGTH))});

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(content);
        builder.setPositiveButton(positive, null);
        builder.setNegativeButton(negative, null);
        editText.requestFocus();
        editText.addTextChangedListener(getTextWatcher());
        //TODO cause NullPointerException
        if (getDialog() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(v -> {
                if (listener != null) {
                    if (listener.onTextEntered(editText.getText().toString().trim())) {
                        dialog.dismiss();
                    }
                }
            });
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelled();
                }
                dialog.dismiss();
            });
        });
        return dialog;
    }

    public void setListener(OnEnterTextListener listener) {
        this.listener = listener;
    }

    public TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                editText.markError(false);
            }
        };
    }

    public static abstract class OnEnterTextListener {

        public abstract boolean onTextEntered(String text);

        public void onCancelled() {
        }
    }
}
