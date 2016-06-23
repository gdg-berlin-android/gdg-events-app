package org.gdg.berlin.android.events.ui.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class EventModel {
  @SuppressWarnings("UnusedReturnValue")
  public static class Builder {
    private String id;
    private String name;
    private String description;
    private String date;
    private String place;
    private String gdg;
    private String groupLogoUrl;
    private final List<String> sphereUrls;

    public Builder() {
      this.sphereUrls = new ArrayList<>();
    }

    public Builder setId(String value) {
      this.id = value;
      return this;
    }

    public Builder setName(String value) {
      this.name = value;
      return this;
    }

    public Builder setDescription(String value) {
      this.description = value;
      return this;
    }

    public Builder setDate(String value) {
      this.date = value;
      return this;
    }

    public Builder setPlace(String value) {
      this.place = value;
      return this;
    }

    public Builder setGdgName(String value) {
      this.gdg = value;
      return this;
    }

    public Builder setGroupLogoUrl(String value) {
      this.groupLogoUrl = value;
      return this;
    }

    public Builder addPhotoSphereUrl(String value) {
      this.sphereUrls.add(value);

      return this;
    }

    public EventModel build() {
      if (TextUtils.isEmpty(id)) {
        throw new IllegalArgumentException("id cannot be null.");
      }
      if (TextUtils.isEmpty(name)) {
        throw new IllegalArgumentException("name cannot be null.");
      }

      return new EventModel(id,
          name,
          description,
          date,
          place,
          gdg,
          groupLogoUrl,
          toArray(sphereUrls));
    }

    private String[] toArray(List<String> images) {
      final String[] array = new String[images.size()];
      for (int i = 0; i < images.size(); ++i) {
        array[i] = images.get(i);
      }
      return array;
    }
  }

  private final String id;
  private final String name;
  private final String description;
  private final String date;
  private final String place;
  private final String gdg;
  private final String groupLogoUrl;
  private final String[] photoSphereUrls;

  private EventModel(String id,
                     String name,
                     String description,
                     String date,
                     String place,
                     String gdg,
                     String groupLogoUrl,
                     String[] photoSphereUrls) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.date = date;
    this.place = place;
    this.gdg = gdg;
    this.groupLogoUrl = groupLogoUrl;
    this.photoSphereUrls = photoSphereUrls;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getDate() {
    return date;
  }

  public String getPlace() {
    return place;
  }

  public String getGdg() {
    return gdg;
  }

  public String getGroupLogoUrl() {
    return groupLogoUrl;
  }

  public String[] getPhotoSphereUrls() {
    return photoSphereUrls;
  }
}
