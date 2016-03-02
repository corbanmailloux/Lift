package co.corb.dylt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;


public class LiftMain extends Activity implements View.OnClickListener, Lifted.LiftedFragmentListener, DidNotLift.DidNotLiftFragmentListener{

    private int currentStreak, bestStreak;
    private long lastLift;
    private ShareActionProvider mShareActionProvider;
    private MenuItem shareMenuItem;

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

        findViewById(R.id.btnYes).setOnClickListener(this);
        findViewById(R.id.btnNo).setOnClickListener(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // Save the streak data
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentStreak", currentStreak);
        editor.putInt("bestStreak", bestStreak);
        editor.putLong("lastLift", lastLift);

        // Commit the edits
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lift_main, menu);

        // Locate MenuItem with ShareActionProvider
        shareMenuItem = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();

        // Start hidden on the main screen
        shareMenuItem.setVisible(false);

        updateShareIntent();

        // Return true to display menu
        return true;
    }

    public void setShareVisible(boolean visible)
    {
        shareMenuItem.setVisible(visible);
    }

    /* // Re-enable if I re-add the Settings menu item.
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
    }*/

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnYes:
                yesAction();
                break;
            case R.id.btnNo:
                noAction();
                break;
            default:
                throw new RuntimeException("Invalid ID for this onClick handler.");
        }
    }

    public void yesAction()
    {
        boolean alreadyLiftedToday = false;

        // First run.
        if (lastLift == 0L)
        {
            currentStreak = 1;
        }
        else {
            int daysBetween =
                    Days.daysBetween(new LocalDate(lastLift).toDateTimeAtStartOfDay(),
                            new LocalDate().toDateTimeAtStartOfDay()).getDays();

            if (daysBetween == 0)
            {
                alreadyLiftedToday = true;
            }
            else if (daysBetween == 1)
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

        updateShareIntent();

        Lifted lifted = Lifted.newInstance(currentStreak, bestStreak, alreadyLiftedToday);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, lifted);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void noAction()
    {
        DidNotLift didNotLift = DidNotLift.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, didNotLift);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void sendShareIntent()
    {
        startActivity(Intent.createChooser(getShareIntent(), getResources().getText(R.string.share_your_streak)));
    }

    private void updateShareIntent() {
        setShareIntent(getShareIntent());
    }

    private Intent getShareIntent()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getShareString());
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    private String getShareString()
    {
        switch (currentStreak) {
            case 0:
                return "I didn't lift yet today. Did You Lift Today? http://dylt.corb.co";
            case 1:
                return "I lifted today. Did You Lift Today? http://dylt.corb.co";
            default:
                return String.format("I've lifted %d days in a row. Did You Lift Today? http://dylt.corb.co", currentStreak);
        }
    }

    public void findNearbyGyms()
    {
        Uri geoLocation = Uri.parse("geo:0,0?q=gym");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
            return inflater.inflate(R.layout.fragment_lift_main, container, false);
        }
    }
}
