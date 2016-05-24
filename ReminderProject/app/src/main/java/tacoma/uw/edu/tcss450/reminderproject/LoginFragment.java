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
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;


/**
 * The RegisterFragment class is the fragment to use to login
 */
public class LoginFragment extends Fragment {
    /**
     * The interface object
     */
    private LoginAddListener logListener;

    /**
     * The URL for webservice
     */
    private final static String LOGIN_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/login.php?";

    private EditText logUsername;
    private EditText logPass;

    /**
     * Constructor
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle("MxN Reminder");

        logUsername = (EditText) view.findViewById(R.id.login_username);
        logUsername.requestFocus();

        logPass = (EditText) view.findViewById(R.id.login_password);

        Button signInButton = (Button) view.findViewById(R.id.login_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = logUsername.getText().toString();
                String pwd = logPass.getText().toString();
                if (TextUtils.isEmpty(userId))  {
                    Toast.makeText(v.getContext(), "Enter username"
                            , Toast.LENGTH_SHORT)
                            .show();
                    logUsername.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pwd))  {
                    Toast.makeText(v.getContext(), "Enter password"
                            , Toast.LENGTH_SHORT)
                            .show();
                    logPass.requestFocus();
                    return;
                }
                if (pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter password of at least 6 characters"
                            , Toast.LENGTH_SHORT)
                            .show();
                    logPass.requestFocus();
                    return;
                }

                String url = buildLoginUrl(v);
                ((LoginActivity) getActivity()).login(url);
            }
        });

        //Register action
        TextView reg = (TextView) view.findViewById(R.id.register_link);
        reg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).registerTextLink();
            }

        });

        //Forget password/id action
        TextView forget = (TextView) view.findViewById(R.id.forget_password_link);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).forgetTextLink();
            }
        });
        return view;
    }

    //the onAttach uses this listener in the code.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginAddListener) {
            logListener = (LoginAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginAddListener");
        }
    }

    /**
     * Build the URL to login base on the user's input
     * @param v the fragment view
     * @return URL as a string
     */
    private String buildLoginUrl(View v) {
        StringBuilder sb = new StringBuilder(LOGIN_URL);

        try {
            sb.append("tag=login");
            String username = logUsername.getText().toString();
            sb.append("&username=");
            sb.append(URLEncoder.encode(username, "UTF-8"));

            String pass = logPass.getText().toString();
            sb.append("&password=");
            sb.append(URLEncoder.encode(pass, "UTF-8"));

            Log.i("LoginAddFragment", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * The login, register, and forget pasword actions listener
     */
    public interface LoginAddListener {
        /**
         * Login action
         * @param url is the url for webservice
         */
        void login(String url);

        /**
         * Launch to Register fragment
         */
        void registerTextLink();

        /**
         * Launch to forget password / id fragment
         */
        void forgetTextLink();

        /**
         * Forget password action
         * @param url is the url for webservice
         */
        void forgetPassword(String url);

        /**
         * Register new user
         * @param url is the url for webservice
         */
        void register(String url);
    }
}
