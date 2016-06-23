package org.gdg.berlin.android.events.ui.presenter;

import com.contentful.vault.Asset;
import com.contentful.vault.Vault;

import java.util.ArrayList;
import java.util.List;

import org.gdg.berlin.android.events.cms.Event;
import org.gdg.berlin.android.events.cms.GoogleDeveloperGroup;
import org.gdg.berlin.android.events.ui.interactor.EventListInteractor;
import org.gdg.berlin.android.events.ui.model.EventModel;

public class EventListPresenter {
  private List<EventModel> eventListWithImages;

  public interface Listener {
    void onEventListAdded(List<EventModel> eventList);

    void onError(Throwable throwable);
  }

  private final EventListInteractor interactor;

  EventListPresenter(EventListInteractor interactor) {
    this.interactor = interactor;
  }

  public EventListPresenter(Vault vault) {
    this(new EventListInteractor(vault));
  }

  public void bind(final Listener listener) {
    interactor.bind(new EventListInteractor.Listener() {
      @Override public void onEventListLoaded(List<Event> eventList) {
        eventListWithImages = createEventListWithImages(eventList);
        listener.onEventListAdded(eventListWithImages);
      }

      @Override public void onError(Throwable throwable) {
        listener.onError(throwable);
      }
    });
  }

  private List<EventModel> createEventListWithImages(List<Event> eventList) {
    List<EventModel> eventModels = new ArrayList<>();

    for (final Event event : eventList) {
      final GoogleDeveloperGroup gdg = event.gdg().get(0);
      final EventModel.Builder builder = new EventModel.Builder()
          .setId(event.remoteId())
          .setName(event.name())
          .setDescription(event.description())
          .setDate(event.date())
          .setPlace(event.place())
          .setGdgName(gdg.name());

      for (Asset sphere : event.spheres()) {
        builder.addPhotoSphereUrl(sphere.url());
      }

      if (gdg.logo() != null) {
        builder.setGroupLogoUrl(gdg.logo().url());
      }

      eventModels.add(builder.build());
    }

    return eventModels;
  }

  public EventModel getEvent(int index) {
    if (eventListWithImages != null && index < eventListWithImages.size() && index >= 0) {
      return eventListWithImages.get(index);
    } else {
      return null;
    }
  }

  public void unbind() {
    interactor.unbind();
    eventListWithImages = null;
  }
}
