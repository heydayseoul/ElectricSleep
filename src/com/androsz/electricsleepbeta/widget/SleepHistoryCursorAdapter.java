package com.androsz.electricsleepbeta.widget;

import java.io.IOException;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androsz.electricsleepbeta.R;
import com.androsz.electricsleepbeta.db.SleepSession;

public class SleepHistoryCursorAdapter extends ResourceCursorAdapter {

    private static int LAYOUT = R.layout.list_item_sleep_history;

    private ViewGroup parent;

    public SleepHistoryCursorAdapter(final Context context, final Cursor cursor) {
        super(context, LAYOUT, cursor, true);
    }

    @Override
    public void bindView(final View view, final Context context,
            final Cursor cursor) {

        final SleepSession session = new SleepSession(cursor);
        ((TextView) view.findViewById(R.id.date)).setText(session
                .getDayText(context));
        ((TextView) view.findViewById(R.id.duration)).setText(context
                .getString(R.string.recording_time, session
                        .getTotalRecordAbbrevTime(context.getResources())));
        ((TextView) view.findViewById(R.id.efficiency)).setText(session
                .getEfficiency() + "%");

        final SleepChart sleepChart = (SleepChart) view
                .findViewById(R.id.sleep_history_list_item_sleepchartview);
        sleepChart.setScroll(true);

        try {
            sleepChart.sync(cursor);
        } catch (final StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public View newView(final Context context, final Cursor cursor,
            final ViewGroup parent) {
        final LayoutInflater li = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.parent = parent;
        return li.inflate(LAYOUT, parent, false);
    }
}
