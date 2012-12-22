package com.annimon.minizipandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Dialog for input and check passwords.
 * @author aNNiMON
 */
public class PasswordDialog extends Dialog {
    
    private Button okButton;
    private EditText passwordText1, passwordText2;
    private TextView infoText;
    private TextWatcher passwordWatcher;
    
    private String password;

    public PasswordDialog(Context context) {
        super(context);
        
        password = null;
        
        configureTextWatcher();
        
        setTitle(R.string.enter_password);
        setContentView(R.layout.password_dialog);
        setCancelable(true);
        
        setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialog) {
                password = null;
                dialog.cancel();
            }
            
        });
        
        okButton = (Button) findViewById(R.id.okButton);
        okButton.setEnabled(false);
        okButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                password = passwordText1.getText().toString();
                dismiss();
            }
        });
        
        passwordText1 = (EditText) findViewById(R.id.passwordText);
        passwordText1.addTextChangedListener(passwordWatcher);
        
        passwordText2 = (EditText) findViewById(R.id.passwordConfirmText);
        passwordText2.addTextChangedListener(passwordWatcher);
        
        infoText = (TextView) findViewById(R.id.infoTextView);
    }
    
    public String getPassword() {
        return password;
    }
    
    private void configureTextWatcher() {
        passwordWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            public void afterTextChanged(Editable editable) {
                String password1 = passwordText1.getText().toString();
                String password2 = passwordText2.getText().toString();
                
                boolean dataCorrect = checkCorrectData(password1, password2);
                okButton.setEnabled(dataCorrect);
            }
        };
    }
    
    private boolean checkCorrectData(String password1, String password2) {
        if ( (password1.length() == 0) || (password2.length() == 0) ) {
            infoText.setText(R.string.passwords_are_empty);
            return false;
        }
        
        if (!password1.equals(password2)) {
            infoText.setText(R.string.passwords_do_not_match);
            return false;
        }
        
        infoText.setText("");
        return true;
    }
 }
