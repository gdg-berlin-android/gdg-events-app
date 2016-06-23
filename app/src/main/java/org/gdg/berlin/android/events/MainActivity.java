package org.gdg.berlin.android.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.interceptor.AuthorizationHeaderInterceptor;
import com.contentful.java.cda.interceptor.ErrorInterceptor;
import com.contentful.java.cda.interceptor.UserAgentHeaderInterceptor;
import com.contentful.vault.Vault;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.gdg.berlin.android.events.cms.EventSpace;
import org.gdg.berlin.android.events.ui.layout.EventLayout;
import org.gdg.berlin.android.events.ui.layout.EventMenuLayout;
import org.gdg.berlin.android.events.ui.model.EventMenuModel;
import org.gdg.berlin.android.events.ui.model.EventModel;
import org.gdg.berlin.android.events.ui.presenter.EventListPresenter;
import org.gdg.berlin.android.events.ui.view.EventMenuView;
import okhttp3.Call;
import okhttp3.OkHttpClient;

@SuppressWarnings("WeakerAccess") // for bind views
public class MainActivity extends AppCompatActivity {

  private static final String LOG_TAG = MainActivity.class.getCanonicalName();

  private EventMenuView eventMenuView;

  private EventListPresenter eventListPresenter;

  @BindView(R.id.toolbar)
  protected Toolbar toolbar;

  @BindView(R.id.drawer_layout)
  protected DrawerLayout drawer;

  @BindView(R.id.event_view)
  protected EventLayout eventLayout;

  @BindView(R.id.about_layout)
  protected View aboutLayout;

  @BindView(R.id.menu_layout)
  protected EventMenuLayout menuLayout;

  private Vault vault;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupViews();

    setupMVP();

    JodaTimeAndroid.init(this);
  }

  private void setupMVP() {
    createVault();

    eventMenuView.bind(menuLayout, new EventMenuView.Listener() {
      @Override public void onEventClicked(int id) {
        final EventModel eventModel = eventListPresenter.getEvent(id);
        if (eventModel != null) {
          hideAbout(eventModel.getName());

          eventLayout.update(eventModel);
          drawer.closeDrawers();
        }
      }

      @Override public void onError(Throwable throwable) {
        showError(throwable);
      }
    });

    eventListPresenter = new EventListPresenter(vault);
    eventListPresenter.bind(new EventListPresenter.Listener() {
      @Override public void onEventListAdded(List<EventModel> eventList) {
        for (int i = 0; i < eventList.size(); i++) {
          EventModel model = eventList.get(i);
          final EventMenuModel event = new EventMenuModel(i, android.R.drawable.ic_dialog_map, model.getName());
          eventMenuView.addEvent(event);
        }
      }

      @Override public void onError(Throwable throwable) {
        showError(throwable);
      }
    });
  }

  private void setupViews() {
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);

    eventMenuView = new EventMenuView();
    showAbout();

    setSupportActionBar(toolbar);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
  }

  private void showAbout() {
    eventLayout.setVisibility(View.GONE);
    aboutLayout.setVisibility(View.VISIBLE);

    toolbar.setTitle(R.string.about_title);
  }

  private void hideAbout(CharSequence title) {
    eventLayout.setVisibility(View.VISIBLE);
    aboutLayout.setVisibility(View.GONE);

    toolbar.setTitle(title);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    if (eventListPresenter != null) {
      eventListPresenter.unbind();
    }

    if (eventMenuView != null) {
      eventMenuView.unbind();
    }

    vault = null;
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_about) {
      showAbout();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void createVault() {
    if (vault == null) {
      vault = Vault.with(this, EventSpace.class);
      vault.requestSync(CDAClient
          .builder()
          .setToken(EventSpace.DELIVERY_API_KEY)
          .setSpace(EventSpace.SPACE_ID)
          .setCallFactory(createCustomCallFactory())
          .build());
    }
  }

  private Call.Factory createCustomCallFactory() {
    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
        .addInterceptor(new AuthorizationHeaderInterceptor(EventSpace.DELIVERY_API_KEY))
        .addInterceptor(new UserAgentHeaderInterceptor(createUserAgent()))
        .addInterceptor(new ErrorInterceptor());

    return okBuilder.build();
  }

  @NonNull private String createUserAgent() {
    return "GDG Events " + BuildConfig.VERSION_NAME + " v" + BuildConfig.VERSION_CODE;
  }

  private void showError(Throwable t) {
    final String msg = "Error occurred!";
    Log.e(LOG_TAG, msg, t);

    Toast.makeText(MainActivity.this, msg + "\n" + t.toString(), Toast.LENGTH_SHORT).show();
  }
}
