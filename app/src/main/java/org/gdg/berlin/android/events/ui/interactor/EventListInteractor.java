package org.gdg.berlin.android.events.ui.interactor;

import com.contentful.vault.SyncResult;
import com.contentful.vault.Vault;

import java.util.ArrayList;
import java.util.List;

import org.gdg.berlin.android.events.cms.Event;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EventListInteractor {

  public interface Listener {
    void onEventListLoaded(List<Event> event);

    void onError(Throwable throwable);
  }

  private final Vault vault;

  private Subscription vaultSyncSubscription;
  private Subscription eventListSubscription;

  public EventListInteractor(Vault vault) {
    this.vault = vault;
  }

  public void bind(final Listener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null!");
    }

    vaultSyncSubscription = Vault.observeSyncResults()
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<SyncResult>() {
          @Override public void call(SyncResult syncResult) {
            if (syncResult.isSuccessful()) {
              requestEvents(listener);
            } else {
              listener.onError(syncResult.error());
            }
          }
        });
  }

  private void requestEvents(final Listener listener) {
    eventListSubscription = vault.observe(Event.class)
        .all()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Event>() {
          final List<Event> eventList = new ArrayList<>();

          @Override public void onCompleted() {
            listener.onEventListLoaded(eventList);
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
    unbindSubscription(eventListSubscription);
    eventListSubscription = null;

    unbindSubscription(vaultSyncSubscription);
    vaultSyncSubscription = null;
  }

  private void unbindSubscription(Subscription subscription) {
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }
}
