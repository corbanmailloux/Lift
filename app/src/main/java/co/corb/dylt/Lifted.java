package co.corb.dylt;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Lifted.LiftedFragmentListener} interface
 * to handle interaction events.
 * Use the {@link Lifted#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Lifted extends Fragment implements View.OnClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "currentStreak";
    private static final String ARG_PARAM2 = "bestStreak";
    private static final String ARG_PARAM3 = "alreadyLiftedToday";

    private int currentStreak, bestStreak;
    private boolean alreadyLiftedToday;

    private LiftedFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentStreak The current streak.
     * @param bestStreak The best recorded streak.
     * @param alreadyLiftedToday Indicates if the user already lifted today.
     * @return A new instance of fragment Lifted.
     */
    public static Lifted newInstance(int currentStreak, int bestStreak, boolean alreadyLiftedToday) {
        Lifted fragment = new Lifted();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, currentStreak);
        args.putInt(ARG_PARAM2, bestStreak);
        args.putBoolean(ARG_PARAM3, alreadyLiftedToday);
        fragment.setArguments(args);
        return fragment;
    }
    public Lifted() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentStreak = getArguments().getInt(ARG_PARAM1, 0);
            bestStreak = getArguments().getInt(ARG_PARAM2, 0);
            alreadyLiftedToday = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View tempView = inflater.inflate(R.layout.fragment_lifted, container, false);

        TextView messageText = (TextView) tempView.findViewById(R.id.messageText);
        if (alreadyLiftedToday)
        {
            messageText.setText(R.string.lifted_already_lifted);
        }
        else
        {
            messageText.setText(R.string.lifted_congrats);
        }

        TextView bestStreakText = (TextView) tempView.findViewById(R.id.bestStreakText);
        bestStreakText.setText(Integer.toString(bestStreak));

        TextView currentStreakText = (TextView) tempView.findViewById(R.id.currentStreakText);
        currentStreakText.setText(Integer.toString(currentStreak));

        Button shareButton = (Button) tempView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);

        return tempView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LiftedFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LiftedFragmentListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mListener.setShareVisible(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareButton:
                mListener.sendShareIntent();
                break;
            default:
                throw new RuntimeException("Invalid ID for this onClick handler.");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mListener.setShareVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LiftedFragmentListener {
        public void setShareVisible(boolean visible);
        public void sendShareIntent();
    }

}
