package com.example.genius.vitubeclient.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * This Activity provides a framework that automatically handles
 * runtime configuration changes in conjunction with an instance of
 * OpsType, which must implement the ConfigurableOps interface.  It
 * also extends LifecycleLoggingActivity so that all lifecycle hook
 * method calls are automatically logged.
 * It is also an instance of the ContextView class of the view in MVP pattern
 */
public abstract class GenericActivity<Interface, OpsType extends ConfigurableOps<Interface>>
        extends LifecycleLoggingActivity implements ContextView {
    /**
     * Used to retain the OpsType state between runtime configuration
     * changes.
     */
    private final RetainedFragmentManager mRetainedFragmentManager  = new RetainedFragmentManager(getFragmentManager(), TAG);

    /**
     * Instance of the operations ("Ops") type.
     */
    private OpsType mOpsInstance;

    /**
     * Lifecycle hook method that's called when this Activity is
     * created.
     *
     * @param savedInstanceState Object that contains saved state information.
     * @param opsType            Class object that's used to create an operations
     *                           ("Ops") object.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState,
                         Class<OpsType> opsType,
                         Interface instance) {
        // Call up to the super class.
        super.onCreate(savedInstanceState);

        try {
            // Handle configuration-related events, including the
            // initial creation of an Activity and any subsequent
            // runtime configuration changes.
            handleConfiguration(opsType,
                    instance);
        } catch (InstantiationException  | IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG,
                    "handleConfiguration "
                            + e);
            // Propagate this as a runtime exception.
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle hardware (re)configurations, such as rotating the
     * display.
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void handleConfiguration(Class<OpsType> opsType, Interface instance)
            throws InstantiationException, IllegalAccessException {

        // If this method returns true it's the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG,
                    "First time onCreate() call");

            // Initialize the GenericActivity fields.
            initialize(opsType, instance);
        } else {
            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change
            // occured.
            Log.d(TAG,
                    "Second or subsequent onCreate() call");

            // Try to obtain the OpsType instance from the
            // RetainedFragmentManager.
            mOpsInstance = mRetainedFragmentManager.get(opsType.getSimpleName());

            // This check shouldn't be necessary under normal
            // circumstances, but it's better to lose state than to
            // crash!
            if (mOpsInstance == null)
                // Initialize the GenericActivity fields.
                initialize(opsType,
                        instance);
            else
                // Inform it that the runtime configuration change has
                // completed.
                mOpsInstance.onConfiguration(instance, false);
        }
    }

    /**
     * Initialize the GenericActivity fields.
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void initialize(Class<OpsType> opsType, Interface instance)
            throws InstantiationException, IllegalAccessException {
        // Create the OpsType object.
        mOpsInstance = opsType.newInstance();

        // Put the OpsInstance into the RetainedFragmentManager under
        // the simple name.
        mRetainedFragmentManager.put(opsType.getSimpleName(),  mOpsInstance);

        // Perform the first initialization.
        mOpsInstance.onConfiguration(instance, true);
    }

    /**
     * Return the initialized OpsType instance for use by the
     * application.
     */
    public OpsType getOps() {
        return mOpsInstance;
    }

    /**
     * set Ops to be used by the activity;
     * this increases level of flexibility
     */
    public void setmOpsInstance(OpsType opsInstance) {
        this.mOpsInstance = opsInstance;
    }

    /**
     * Return the initialized OpsType instance for use by the
     * application.
     */
    public RetainedFragmentManager getRetainedFragmentManager() {
        return mRetainedFragmentManager;
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return this;
    }

    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }


}

