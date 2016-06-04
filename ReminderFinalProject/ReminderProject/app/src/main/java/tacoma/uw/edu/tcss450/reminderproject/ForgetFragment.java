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
 * A simple {@link Fragment} subclass.
 */
public class ForgetFragment extends Fragment {
    /**
     * The URL for webservice when user want to reset password
     */
    private final static String FORGET_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/login.php?";
    private EditText fEmail;
    /**
     * The interface object
     */
    private LoginFragment.LoginAddListener rListener;


    public ForgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forget, container, false);
        getActivity().setTitle("Forget Password or Username");

        fEmail = (EditText) view.findViewById(R.id.forget_email);
        fEmail.requestFocus();

        Button confirm = (Button) view.findViewById(R.id.forget_confirm_btn);
        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String text = fEmail.getText().toString();

                if(TextUtils.isEmpty(text)){
                    Toast.makeText(v.getContext(), "Email is empty.", Toast.LENGTH_SHORT).show();
                    fEmail.requestFocus();
                    return;
                }else if(!checkEmail(text)){
                    Toast.makeText(v.getContext(), "Enter valid email address.", Toast.LENGTH_SHORT).show();
                    fEmail.requestFocus();
                    return;
                }
                String url = buildRegisterUrl(v);
                rListener.forgetPassword(url);
            }
        });
        return view;
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
        StringBuilder sb = new StringBuilder(FORGET_URL);

        try {
            sb.append("tag=forget");

            String text = fEmail.getText().toString();
            sb.append("&email=");
            sb.append(URLEncoder.encode(text, "UTF-8"));

            Log.i("RegisterAddFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }
}
