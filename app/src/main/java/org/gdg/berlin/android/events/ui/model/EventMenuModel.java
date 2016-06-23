package org.gdg.berlin.android.events.ui.model;

import android.support.annotation.DrawableRes;

public class EventMenuModel {

  private final String title;

  private final @DrawableRes int icon;

  private final int itemId;

  public EventMenuModel(int itemId, int icon, String title) {
    this.itemId = itemId;
    this.title = title;
    this.icon = icon;
  }

  public String getTitle() {
    return title;
  }

  public int getIcon() {
    return icon;
  }

  public int getItemId() {
    return itemId;
  }
}
