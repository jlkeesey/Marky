package pub.carkeys.marky;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

class LocationServiceController extends pub.carkeys.marky.util.ServiceController {

  private final Intent intent = new Intent(context, LocationService.class);
  private LocationService locationService;

  LocationServiceController(Context context) {
    super(context);
  }

  @Override
  @NonNull
  protected Intent getServiceIntent(ServiceIntentType type) {
    return intent;
  }

  @Override
  protected Service handleServiceConnected(final ComponentName componentName,
                                           final IBinder iBinder) {
    locationService = ((LocationService.LocationServiceBinder) iBinder).getService();
    return locationService;
  }
}
