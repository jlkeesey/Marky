package pub.carkeys.marky.util;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.common.base.Preconditions;

/**
 * Provides support for controlling the lifecycle of a service and accessing the service's
 * functionality.
 *
 * @param <T> the service object type.
 */
public abstract class ServiceController<T extends Service> {
  protected final Context context;
  private T service;
  private boolean bound;
  /**
   * Handles the callbacks for binding and unbinding to the service.
   */
  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
      service = handleServiceConnected(componentName, iBinder);
      bound = true;
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
      service = null;
      bound = false;
    }
  };
  private boolean unbindPending;
  private View view;
  private boolean started;

  /**
   * Constructs a new service controller.
   *
   * @param context the owning {@link Context}. This will usually be an {@link Activity} or the
   *     {@link Application}. This controls the lifetime of the service. Generally, services do not
   *     outlive the context that created them.
   */
  protected ServiceController(Context context) {
    this.context = context;
  }

  /**
   * Starts the service. This will usually be called from the {@code onStart()} of a context.
   *
   * @param view the {@link View} used as the parent to display error and informative messages
   *     to the user through some feature like a {@link android.support.design.widget.Snackbar}.
   */
  public void startService(final View view) {
    Preconditions.checkState(!started, "ServiceController already started.");
    this.view = Preconditions.checkNotNull(view);
    context.startService(getServiceIntent(ServiceIntentType.START));
    started = true;
  }

  @NonNull
  protected abstract Intent getServiceIntent(ServiceIntentType type);

  /**
   * Stops the service. If the service is bound this will unbind the service first. Usually called
   * from the {@code onStop()} of a context.
   */
  public void stopService() {
    Preconditions.checkState(started, "ServiceController has not been started.");
    if (bound && !unbindPending) {
      unbindService();
    }
    context.stopService(getServiceIntent(ServiceIntentType.STOP));
    view = null;
    started = false;
  }

  /**
   * Unbinds the service. After this the service cannot be interacted with until another call to
   * {@link #bindService()} is called. Usually called in the {@code onPause()} of a context.
   */
  public void unbindService() {
    Preconditions.checkState(!bound, "ServiceController not bound.");
    context.unbindService(serviceConnection);
    unbindPending = true;
  }

  /**
   * Binds to the service. This must be called before the service can be interacted with. Usually
   * called from the {@code onResume()} of a context.
   */
  public void bindService() {
    Preconditions.checkState(!bound, "ServiceController already bound.");
    context.bindService(getServiceIntent(ServiceIntentType.BIND),
                        serviceConnection,
                        Context.BIND_AUTO_CREATE);
  }

  /**
   * Called during binding so that a derived class can do the necessary processing to return a
   * reference to the {@link Service} that this controller interacts with.
   *
   * @param componentName the component name passed to {@code onServiceConnected(ComponentName,
   *     IBinder)} above.
   * @param iBinder the binder passed to {@code onServiceConnected(ComponentName, IBinder)}
   *     above.
   *
   * @return the {@link Service} this controller interacts with.
   */
  protected abstract T handleServiceConnected(final ComponentName componentName,
                                              final IBinder iBinder);

  /**
   * Returns the service that this controller interacts with.
   *
   * @return the {@link Service} this controller interacts with if it has been bound.
   */
  @SuppressWarnings("unused")
  protected T getService() {
    return Preconditions.checkNotNull(service);
  }

  /**
   * Returns the {@link View} that this controller uses to communicated to the user with.
   *
   * @return the {@link View} to communicate to the user with.
   */
  protected View getView() {
    return Preconditions.checkNotNull(view);
  }

  /**
   * Defines the type of intent to retrieve. For most situations the same intent can be used for all
   * cases and this can be ignored.
   */
  public enum ServiceIntentType {
    /**
     * Requesting an intent to start the service.
     */
    START,

    /**
     * Requesting an intent to stop the service.
     */
    STOP,

    /**
     * Requesting an intent to bind to the service.
     */
    BIND
  }
}
