package org.gdg.berlin.android.events.ui.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.vrtoolkit.cardboard.widgets.common.VrWidgetView;
import com.google.vrtoolkit.cardboard.widgets.pano.VrPanoramaEventListener;
import com.google.vrtoolkit.cardboard.widgets.pano.VrPanoramaView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.gdg.berlin.android.events.R;
import org.gdg.berlin.android.events.ui.model.EventModel;

@SuppressWarnings("WeakerAccess") // for bind views
public class EventLayout extends CardView {
  private static Bitmap intermediatePhotoSphereBitmap;
  private static Bitmap errorPhotoSphereBitmap;

  private final ImageLoader imageLoader;

  @BindView(R.id.event_logo)
  protected ImageView groupLogo;
  @BindView(R.id.event_place)
  protected TextView eventPlaceText;
  @BindView(R.id.event_title)
  protected TextView eventTitle;
  @BindView(R.id.event_gdg)
  protected TextView eventGDGName;
  @BindView(R.id.event_date)
  protected TextView eventDateText;
  @BindView(R.id.event_description)
  protected TextView eventDescription;
  @BindView(R.id.event_photospheres)
  protected LinearLayout photospheres;

  private String[] photoSphereUrls;
  private String fullscreenSphereUrl;

  public EventLayout(Context context) {
    this(context, null);
  }

  public EventLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    if (!isInEditMode()) {
      if (!ImageLoader.getInstance().isInited()) {
        final ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);
      } else {
        imageLoader = ImageLoader.getInstance();
      }
    } else {
      imageLoader = ImageLoader.getInstance();
    }
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();

    ButterKnife.bind(this);

    if (intermediatePhotoSphereBitmap == null) {
      intermediatePhotoSphereBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ps_loading);
      errorPhotoSphereBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ps_error);
    }
  }

  public void update(EventModel eventModel) {
    eventPlaceText.setText(eventModel.getPlace());
    eventTitle.setText(eventModel.getName());
    eventGDGName.setText(eventModel.getGdg());
    eventDateText.setText(createHumanReadableDate(eventModel));
    eventDescription.setText(eventModel.getDescription());

    DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(android.R.drawable.ic_menu_gallery)
        .build();

    imageLoader.displayImage(eventModel.getGroupLogoUrl(), groupLogo, options);

    photospheres.removeAllViews();

    photoSphereUrls = eventModel.getPhotoSphereUrls();
    for (final String photoSphereUrl : photoSphereUrls) {
      loadPhotoSphereFromUrl(photoSphereUrl);
    }
  }

  private String createHumanReadableDate(EventModel eventModel) {
    final String date = eventModel.getDate();
    final DateTime dateTime = new DateTime(date);
    final DateTimeFormatter formatter = DateTimeFormat.shortDate();
    final Locale locale = Locale.getDefault();
    final DateTimeFormatter dateTimeFormatter = formatter.withLocale(locale);

    return dateTimeFormatter.print(dateTime);
  }

  private void loadPhotoSphereFromUrl(String photoSphereUrl) {
    final ViewGroup loadingView = (ViewGroup) LayoutInflater.from(getContext())
        .inflate(R.layout.progress_photosphere, photospheres, false);

    photospheres.addView(loadingView);

    imageLoader.loadImage(photoSphereUrl, new ImageLoadingListener() {
      @Override public void onLoadingStarted(String imageUri, View view) {
      }

      @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
      }

      @Override public void onLoadingComplete(final String imageUri, View view, final Bitmap originalBitmap) {
        final VrPanoramaView sphere = createPanoramaView();
        final VrPanoramaView.Options options = new VrPanoramaView.Options();

        sphere.setAlpha(0.0f);
        sphere.animate().alpha(1.0f).setDuration(200).start();

        final View fullscreenButton = sphere.findViewById(R.id.fullscreen_button);
        fullscreenButton.setBackgroundColor(0x80000000);
        sphere.findViewById(R.id.cardboard_button).setBackgroundColor(0x80000000);

        options.inputType = VrPanoramaView.Options.TYPE_MONO;
        sphere.loadImageFromBitmap(originalBitmap, options);
        sphere.setEventListener(new VrPanoramaEventListener() {
          @Override public void onClick() {
            if (fullscreenSphereUrl == null) {
              fullscreenButton.callOnClick();
            } else {
              loadNextSphere(sphere);
            }
          }

          @Override public void onDisplayModeChanged(int newDisplayMode) {
            if (newDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_MONO ||
                newDisplayMode == VrWidgetView.DisplayMode.FULLSCREEN_VR) {
              fullscreenSphereUrl = imageUri;
            } else {
              fullscreenSphereUrl = null;
              sphere.loadImageFromBitmap(originalBitmap, options);
            }

            super.onDisplayModeChanged(newDisplayMode);
          }
        });

        loadingView.addView(sphere);
      }

      @Override public void onLoadingCancelled(String imageUri, View view) {
      }
    });
  }

  private void loadNextSphere(final VrPanoramaView sphere) {
    final VrPanoramaView.Options options = new VrPanoramaView.Options();
    options.inputType = VrPanoramaView.Options.TYPE_MONO;

    final int indexOfUrl = urlIndexOf(fullscreenSphereUrl);
    if (indexOfUrl != -1) {
      final int nextIndex = (indexOfUrl + 1) % photoSphereUrls.length;
      final String nextUrl = photoSphereUrls[nextIndex];

      imageLoader.loadImage(nextUrl, new ImageLoadingListener() {
        @Override public void onLoadingStarted(String imageUri, View view) {
          sphere.loadImageFromBitmap(intermediatePhotoSphereBitmap, options);
        }

        @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
          sphere.loadImageFromBitmap(errorPhotoSphereBitmap, options);
        }

        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
          sphere.loadImageFromBitmap(loadedImage, options);
          fullscreenSphereUrl = imageUri;
        }

        @Override public void onLoadingCancelled(String imageUri, View view) {
          sphere.loadImageFromBitmap(errorPhotoSphereBitmap, options);
        }
      });
    }
  }

  private int urlIndexOf(String imageUri) {
    if (imageUri != null) {
      for (int i = 0; i < photoSphereUrls.length; ++i) {
        if (imageUri.equals(photoSphereUrls[i])) {
          return i;
        }
      }
    }
    return -1;
  }

  @NonNull private VrPanoramaView createPanoramaView() {
    final VrPanoramaView sphere = new VrPanoramaView(getContext());
    final int widthInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.panorama_preview_width);
    final int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.panorama_preview_height);

    final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(widthInPixel, heightInPixel);
    sphere.setLayoutParams(layoutParams);

    sphere.setCardboardButtonEnabled(true);
    sphere.setInfoButtonEnabled(false);

    return sphere;
  }
}
