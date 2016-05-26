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

import tacoma.uw.edu.tcss450.Reminder.ReminderDetailFragment;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {
    private static final String PROFILE_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/login.php?";
    //protected static String profileUser;
    private Button changePass, updateProfile;
    private EditText first, last, email;
    private UpdateProfileListener rListener;

    public interface UpdateProfileListener
    {
        void updateProfile(String url);
    }

    public UserProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        Bundle b = getActivity().getIntent().getBundleExtra("bundle");

        first = (EditText) v.findViewById(R.id.profile_first_name);
        first.setText(b.getString("first"));

        last = (EditText) v.findViewById(R.id.profile_last_name);
        last.setText(b.getString("last"));
        email = (EditText) v.findViewById(R.id.profile_email);
        email.setText(b.getString("email"));

        changePass = (Button) v.findViewById(R.id.profile_pass_btn);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.user_profile_container, new ChangePassFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        updateProfile = (Button) v.findViewById(R.id.profile_update_btn);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(v.getContext(), "Email is empty.", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                //update user profile
                rListener.updateProfile(buildProfileUrl());
            }
        });

        return v;
    }

    //the onAttach uses this listener in the code.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateProfileListener) {
            rListener = (UpdateProfileListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegisterAddListener");
        }
    }


    /**
     * Build the URL to register base on the user's input
     * @return URL as a string
     */
    private String buildProfileUrl() {
        StringBuilder sb = new StringBuilder(PROFILE_URL);

        try {
            sb.append("tag=update");
            sb.append("&username=");
            sb.append(URLEncoder.encode(UserProfile.profileUser, "UTF-8"));

            sb.append("&first=");
            sb.append(URLEncoder.encode(first.getText().toString(), "UTF-8"));

            sb.append("&last=");
            sb.append(URLEncoder.encode(last.getText().toString(), "UTF-8"));

            sb.append("&email=");
            sb.append(URLEncoder.encode(email.getText().toString(), "UTF-8"));

            Log.i("RegisterAddFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }
}
