package per.gracefulife.cordova.androbadge.AndroBadge;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AndroBadge extends CordovaPlugin {
	private static final String PREF_NAME = "_badge";
	private static final String BADGE_NAME = "badge_item";
	private static final String ACTION_INCREASE = "increase";
	private static final String ACTION_DECREASE = "decrease";
	private static final String ACTION_CLEAR = "clear";
	private static final String ACTION_GET = "get";

	private Context getApplicationContext() {
		return this.cordova.getActivity().getApplicationContext();
	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if (action.equals(ACTION_INCREASE)) {
			AndroBadge.increaseBadgeValue(getApplicationContext());
			updateUI(this.cordova.getActivity());
		} else if (action.equals(ACTION_DECREASE)) {
			AndroBadge.decreaseBadgeValue(getApplicationContext());
			updateUI(this.cordova.getActivity());
		} else if (action.equals(ACTION_CLEAR)) {
			AndroBadge.clearBadge(getApplicationContext());
			updateUI(this.cordova.getActivity());
		} else if (action.equals(ACTION_GET)) {
			callbackContext.success(AndroBadge
					.getBadgeValue(getApplicationContext()));
		} else {
			return false;
		}
		return true;
	}

	public static void updateUI(Activity activity) {
		Log.i("updateUI", "Current badge value = " + getBadgeValue(activity));
		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
		intent.putExtra("badge_count_package_name", activity.getComponentName()
				.getPackageName());
		intent.putExtra("badge_count_class_name", activity.getComponentName()
				.getClassName());
		intent.putExtra("badge_count", getBadgeValue(activity));
		activity.sendBroadcast(intent);
	}

	public static void increaseBadgeValue(Context context) {
		updateBadgeValue(context, (getBadgeValue(context)) + 1);
	}

	public static void decreaseBadgeValue(Context context) {
		updateBadgeValue(context, (getBadgeValue(context)) - 1);
	}

	public static void clearBadge(Context context) {
		updateBadgeValue(context, 0);
	}

	public static int getBadgeValue(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				getApplicationName(context) + PREF_NAME, Context.MODE_PRIVATE);
		return pref.getInt(BADGE_NAME, 0);
	}

	private static void updateBadgeValue(Context context, int count) {
		SharedPreferences pref = context.getSharedPreferences(
				getApplicationName(context) + PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(BADGE_NAME, count);
		editor.commit();
	}

	private static String getApplicationName(Context context) {
		int stringId = context.getApplicationInfo().labelRes;
		return context.getString(stringId);
	}
}
