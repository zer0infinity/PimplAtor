package ch.hsr.ifs.cdttesting;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TestingPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.hsr.ifs.cdttesting"; //$NON-NLS-1$
	public static final String XML_EXTENSION_POINT_ID = "ch.hsr.ifs.cdttesting.testingPlugin";
	public static final String XML_RTS_LOCATION_ELEMTENT_NAME = "rtsLocation";
	public static final String XML_ACTIVATOR_ELEMTENT_NAME = "activator";

	// The shared instance
	private static TestingPlugin plugin;
	
	/**
	 * The constructor
	 */
	public TestingPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TestingPlugin getDefault() {
		return plugin;
	}

}
