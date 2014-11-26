package co.corb.dylt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;


public class LiftMain extends Activity implements Lifted.OnFragmentInteractionListener, DidNotLift.OnFragmentInteractionListener{

    public int currentStreak, bestStreak;
    public long lastLift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        currentStreak = preferences.getInt("currentStreak", 0);
        bestStreak = preferences.getInt("bestStreak", 0);
        lastLift = preferences.getLong("lastLift", 0L);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // We need an Editor object to make preference changes.
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentStreak", currentStreak);
        editor.putInt("bestStreak", bestStreak);
        editor.putLong("lastLift", lastLift);

        // Commit the edits!
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lift_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void btnYesClick(View v)
    {
        // First run.
        if (lastLift == 0L)
        {
            currentStreak = 1;
        }
        else {
            int daysBetween =
                    Days.daysBetween(new LocalDate(lastLift).toDateTimeAtStartOfDay(),
                            new LocalDate().toDateTimeAtStartOfDay()).getDays();

            if (daysBetween == 1)
            {
                // It's been a day. Add to the streak.
                currentStreak++;
            }
            else if (daysBetween > 1)
            {
                // Too long. Reset the streak.
                currentStreak = 1;
            }
        }

        // Update lastLift
        lastLift = new DateTime().getMillis();

        if (currentStreak > bestStreak)
        {
            bestStreak = currentStreak;
        }

        Lifted lifted = Lifted.newInstance(currentStreak, bestStreak);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, lifted);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void btnNoClick(View v)
    {
        DidNotLift didNotLift = DidNotLift.newInstance("","");

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, didNotLift);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lift_main, container, false);
            return rootView;
        }
    }
}
