package org.gdg.berlin.android.events.ui.interactor;

import com.contentful.vault.Vault;

import java.util.ArrayList;
import java.util.List;

import org.gdg.berlin.android.events.cms.Event;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class EventListInteractor {

  public interface Listener {
    void onEventListLoaded(List<Event> event);

    void onError(Throwable throwable);
  }

  private final Vault vault;

  private Subscription subscription;

  public EventListInteractor(Vault vault) {
    this.vault = vault;
  }

  public void bind(final Listener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null!");
    }

    subscription = vault.observe(Event.class)
        .all()
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Event>() {
          final List<Event> eventList = new ArrayList<>();

          @Override public void onCompleted() {
            listener.onEventListLoaded(eventList);
            subscription = null;
          }

          @Override public void onError(Throwable throwable) {
            listener.onError(throwable);
          }

          @Override public void onNext(Event event) {
            eventList.add(event);
          }
        });
  }

  public void unbind() {
    if (subscription != null) {
      subscription.unsubscribe();
      subscription = null;
    }
  }
}
