package tacoma.uw.edu.tcss450.reminderproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;


/**
 * The RegisterFragment class is the fragment to use to register new user.
 */
public class RegisterFragment extends Fragment {
    private final static String REGISTER_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/register.php?";

    private LoginFragment.LoginAddListener rListener;

    private EditText rUsername;
    private EditText rEmail;
    private EditText rPassword;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        getActivity().setTitle("Register");     //set title of the fragment

        rUsername = (EditText) v.findViewById(R.id.register_username);
        rEmail = (EditText) v.findViewById(R.id.register_email);
        rPassword = (EditText) v.findViewById((R.id.register_password));

        Button reg = (Button) v.findViewById(R.id.register_btn);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = rUsername.getText().toString();
                String email = rEmail.getText().toString();
                String pwd = rPassword.getText().toString();

                if (TextUtils.isEmpty(user)) {
                    Toast.makeText(v.getContext(), "Username is empty.", Toast.LENGTH_SHORT).show();
                    rUsername.requestFocus();
                    return;
                } else if (user.length() < 5) {
                    Toast.makeText(v.getContext(), "Username must at least 5 characters.", Toast.LENGTH_SHORT).show();
                    rUsername.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(v.getContext(), "Email is empty.", Toast.LENGTH_SHORT).show();
                    rEmail.requestFocus();
                    return;
                }else if(!checkEmail(email)){
                    Toast.makeText(v.getContext(), "Enter valid email address.", Toast.LENGTH_SHORT).show();
                    rEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(v.getContext(), "Password cant empty.", Toast.LENGTH_SHORT).show();
                    rPassword.requestFocus();
                    return;
                } else if (pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Password must at least 6 characters.", Toast.LENGTH_SHORT).show();
                    rPassword.requestFocus();
                    return;
                }

                String url = buildRegisterUrl(v);
                rListener.register(url);
            }
        });
        return v;
    }

    /**
     * Check the input email is valid or not
     * @param email is input email.
     * @return true / false
     */
    private boolean checkEmail(String email) {
        Boolean check = false;
        for(int i = 0; i < email.length(); i++){
            if(email.charAt(i) == 64){
               check = true;
                break;
            }
        }
        return check;
    }


    //the onAttach uses this listener in the code.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.LoginAddListener) {
            rListener = (LoginFragment.LoginAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegisterAddListener");
        }
    }

    /**
     * Build the URL to register base on the user's input
     * @param v the fragment view
     * @return URL as a string
     */
    private String buildRegisterUrl(View v) {
        StringBuilder sb = new StringBuilder(REGISTER_URL);

        try {
            String username = rUsername.getText().toString();
            sb.append("username=");
            sb.append(username);

            String email = rEmail.getText().toString();
            sb.append("&email=");
            sb.append(URLEncoder.encode(email, "UTF-8"));

            String pass = rPassword.getText().toString();
            sb.append("&password=");
            sb.append(URLEncoder.encode(pass, "UTF-8"));

            Log.i("RegisterAddFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

}
