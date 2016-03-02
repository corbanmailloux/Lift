package co.corb.dylt;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DidNotLift.DidNotLiftFragmentListener} interface
 * to handle interaction events.
 * Use the {@link DidNotLift#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DidNotLift extends Fragment implements View.OnClickListener{

    private DidNotLiftFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DidNotLift.
     */
    public static DidNotLift newInstance() {
        DidNotLift fragment = new DidNotLift();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DidNotLift() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View tempView = inflater.inflate(R.layout.fragment_did_not_lift, container, false);

        Button findGymButton = (Button) tempView.findViewById(R.id.btnFindGym);
        findGymButton.setOnClickListener(this);

        return tempView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DidNotLiftFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DidNotLiftFragmentListener");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFindGym:
                mListener.findNearbyGyms();
                break;
            default:
                throw new RuntimeException("Invalid ID for this onClick handler.");
        }
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
    public interface DidNotLiftFragmentListener {
        public void findNearbyGyms();
    }

}
