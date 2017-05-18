package anthonynahas.com.autocallrecorder.activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.adapters.StatisticRecordsAdapter;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.providers.RecordDbHelper;
import anthonynahas.com.autocallrecorder.providers.RecordsContentProvider;
import anthonynahas.com.autocallrecorder.utilities.decoraters.DemoRecordSupport;

/**
 * Class that deals with the content provider (DB) in order to analyse the db and
 * push a statistic as GUI.
 * e.g: most called contacts... most outgoing calls...
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 16.05.2017
 */
public class StatisticActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = StatisticActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Context mContext;
    private final int mLoaderManagerID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        setupActionBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String[] list = {"asfa", "asfasf"};
        mAdapter = new StatisticRecordsAdapter(DemoRecordSupport.newInstance().generateRecordsList(2));
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(mLoaderManagerID, null, this);
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.material_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Statistic");
            //actionBar.setHomeAsUpIndicator();
        }
    }

    /**
     * E.G: < button: finishes the current activity
     *
     * @param item - item in the toolbar
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        String[] projection = new String[]{RecordDbContract.RecordItem.COLUMN_NUMBER
                + ", COUNT ("
                + RecordDbContract.RecordItem.COLUMN_NUMBER
                + ")"};
        String selection = null;

        String[] selectionArgs = null;
        String sort = "COUNT ( " + RecordDbContract.RecordItem.COLUMN_NUMBER + ") DESC";

        switch (id) {
            case 0:
                // TODO: 17.05.2017 offset and limit control
                Uri uri = RecordDbContract.CONTENT_URL
                        .buildUpon()
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_LIMIT,
                                String.valueOf(10))
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_OFFSET,
                                String.valueOf(0))
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_GROUP_BY,
                                RecordDbContract.RecordItem.COLUMN_NUMBER)
                        //.encodedQuery("mLimit=" + mLimit + "," + mOffset)
                        .build();
                return new CursorLoader(this, uri, projection, selection, selectionArgs, sort);

            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished with cursor size --> " + data.getCount());
        mAdapter = new StatisticRecordsAdapter(RecordDbHelper.convertCursorToConractRecordsList(data));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        //empty for now
    }
}
