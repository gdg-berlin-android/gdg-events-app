package org.gdg.berlin.android.events.cms;

import com.contentful.vault.Asset;
import com.contentful.vault.ContentType;
import com.contentful.vault.Field;
import com.contentful.vault.Resource;

@SuppressWarnings("WeakerAccess") // for @Field
@ContentType("gdg")
public class GoogleDeveloperGroup extends Resource {
  @Field
  String name;

  @Field
  String description;

  @Field
  Asset logo;

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public Asset logo() {
    return logo;
  }
}
