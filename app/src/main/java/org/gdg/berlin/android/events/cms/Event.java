package org.gdg.berlin.android.events.cms;

import com.contentful.vault.Asset;
import com.contentful.vault.ContentType;
import com.contentful.vault.Field;
import com.contentful.vault.Resource;
import java.util.List;

@SuppressWarnings("WeakerAccess") @ContentType("event")
public class Event extends Resource {
  @Field
  String name;

  @Field
  String description;

  @Field
  String date;

  @Field
  String place;

  @Field
  List<Asset> spheres;

  @Field
  List<GoogleDeveloperGroup> gdg;

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public String date() {
    return date;
  }

  public String place() {
    return place;
  }

  public List<Asset> spheres() {
    return spheres;
  }

  public List<GoogleDeveloperGroup> gdg() {
    return gdg;
  }
}
