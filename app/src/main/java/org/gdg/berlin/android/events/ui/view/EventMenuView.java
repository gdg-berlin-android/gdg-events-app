package org.gdg.berlin.android.events.ui.view;

import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import org.gdg.berlin.android.events.ui.layout.EventMenuLayout;
import org.gdg.berlin.android.events.ui.model.EventMenuModel;

public class EventMenuView {

  public interface Listener {
    void onEventClicked(int id);

    void onError(Throwable throwable);
  }

  private EventMenuLayout source;

  private Listener listener;

  public void bind(EventMenuLayout source, final Listener listener) {
    if (source == null) {
      throw new IllegalArgumentException("Source view cannot be null!");
    }

    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null!");
    }

    this.source = source;
    this.listener = listener;

    source.reset();
    source.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override public boolean onNavigationItemSelected(MenuItem item) {
        listener.onEventClicked(item.getItemId());
        return false;
      }
    });
  }

  public void unbind() {
    source.setNavigationItemSelectedListener(null);
    listener = null;
  }

  public void addEvent(EventMenuModel item) {
    if (item == null && listener != null) {
      listener.onError(new IllegalArgumentException());
    } else {
      source.addMenuItem(item);
    }
  }
}
