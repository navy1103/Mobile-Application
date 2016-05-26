package tacoma.uw.edu.tcss450.User;


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

import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassFragment extends Fragment {
    private static final String PASSWORD_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/login.php?";

    EditText oldPass, newPass, confirmPass;
    private ChangePassListener rListener;

    public interface ChangePassListener{
        void changePassword(String url);
    }

    public ChangePassFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);

        oldPass = (EditText) view.findViewById(R.id.old_password);
        oldPass.requestFocus();
        newPass = (EditText) view.findViewById(R.id.new_password);
        confirmPass = (EditText) view.findViewById(R.id.confirm_password);

        Button changePass = (Button) view.findViewById(R.id.change_pass_btn);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(oldPass.getText().toString())){
                    Toast.makeText(v.getContext(), "Enter your old password", Toast.LENGTH_SHORT).show();
                    oldPass.requestFocus();
                    return;
                } else if(TextUtils.isEmpty(newPass.getText().toString())){
                    Toast.makeText(v.getContext(), "Enter your new password", Toast.LENGTH_SHORT).show();
                    newPass.requestFocus();
                    return;
                } else if(TextUtils.isEmpty(confirmPass.getText().toString())){
                    Toast.makeText(v.getContext(), "Reenter your new password", Toast.LENGTH_SHORT).show();
                    confirmPass.requestFocus();
                    return;
                } else if(!TextUtils.equals(newPass.getText().toString(), confirmPass.getText().toString())){
                    Toast.makeText(v.getContext(), "Your new password and confirm password should be the same password.", Toast.LENGTH_SHORT).show();
                    confirmPass.requestFocus();
                    return;
                }

                String url = buildUrl(v);
                rListener.changePassword(url);

                Log.i("RegisterAddFragment", url.toString());
            }
        });


        return view;
    }

    //the onAttach uses this listener in the code.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChangePassListener) {
            rListener = (ChangePassListener) context;
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
    private String buildUrl(View v) {
        StringBuilder sb = new StringBuilder(PASSWORD_URL);

        try {
            sb.append("tag=changePass");
            sb.append("&username=");
            sb.append(URLEncoder.encode(UserProfile.profileUser, "UTF-8"));

            String old = oldPass.getText().toString();
            sb.append("&oldPass=");
            sb.append(URLEncoder.encode(old, "UTF-8"));

            String change = newPass.getText().toString();
            sb.append("&newPass=");
            sb.append(URLEncoder.encode(change, "UTF-8"));

            Log.i("RegisterAddFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }
}
