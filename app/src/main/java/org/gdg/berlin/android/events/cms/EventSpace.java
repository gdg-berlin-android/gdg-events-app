package org.gdg.berlin.android.events.cms;

import com.contentful.vault.Space;

@Space(
    value = EventSpace.SPACE_ID,
    models = {Event.class, GoogleDeveloperGroup.class},
    locales = {"en-US"},
    dbVersion = 7
)
public class EventSpace {
  public static final String DELIVERY_API_KEY = "c12384becc98e688db0d2fd1df9e8dc5a7fd48c9ea3abed3065c62507f464403";
  public static final String SPACE_ID = "iz87bfgoerp9";
}
