package pub.carkeys.marky;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public final class MarkyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Fresco.initialize(this);
  }
}
