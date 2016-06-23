package org.gdg.berlin.android.events.ui.layout;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import org.gdg.berlin.android.events.ui.model.EventMenuModel;

public class EventMenuLayout extends NavigationView {

  private ProgressBar progressBar;

  public EventMenuLayout(Context context) {
    this(context, null);
  }

  public EventMenuLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EventMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();

    progressBar = new ProgressBar(getContext());
    progressBar.setIndeterminate(true);
    addHeaderView(progressBar);
  }

  public void reset() {
    final Menu menu = getMenu();
    if (menu != null) {
      removeHeaderView(progressBar);
      menu.clear();
    }
  }

  public void addMenuItem(EventMenuModel eventMenuModel) {
    final Menu menu = getMenu();
    final String title = eventMenuModel.getTitle();

    final MenuItem item = menu.add(Menu.NONE, eventMenuModel.getItemId(), Menu.NONE, title);
    item.setIcon(eventMenuModel.getIcon());
  }
}
