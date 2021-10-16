package org.smartregister.chw.barebones.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartregister.chw.barebones.TestLibrary;
import org.smartregister.chw.barebones.contract.BaseTestCallDialogContract;
import org.smartregister.chw.barebones.dao.TestDao;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.barebones.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.PermissionUtils;

import java.util.Date;

import timber.log.Timber;

import static org.smartregister.util.Utils.getAllSharedPreferences;

public class TestUtil {

    public static void processEvent(AllSharedPreferences allSharedPreferences, Event baseEvent) throws Exception {
        if (baseEvent != null) {
            TestJsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
            JSONObject eventJson = new JSONObject(TestJsonFormUtils.gson.toJson(baseEvent));

            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, BaseRepository.TYPE_Unprocessed);
            startClientProcessing();
        }
    }

    public static void startClientProcessing() {
        try {
            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public static ECSyncHelper getSyncHelper() {
        return TestLibrary.getInstance().getEcSyncHelper();
    }

    public static ClientProcessorForJava getClientProcessorForJava() {
        return TestLibrary.getInstance().getClientProcessorForJava();
    }

    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static boolean launchDialer(final Activity activity, final BaseTestCallDialogContract.View callView, final String phoneNumber) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // set a pending call execution request
            if (callView != null) {
                callView.setPendingCallRequest(() -> TestUtil.launchDialer(activity, callView, phoneNumber));
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);

            return false;
        } else {

            if (((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number()
                    == null) {

                Timber.i("No dial application so we launch copy to clipboard...");

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getText(R.string.copied_phone_number), phoneNumber);
                clipboard.setPrimaryClip(clip);

                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(activity, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
                // no phone
                Toast.makeText(activity, activity.getText(R.string.copied_phone_number), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                activity.startActivity(intent);
            }
            return true;
        }
    }

    public static void saveFormEvent(final String jsonString) throws Exception {
        AllSharedPreferences allSharedPreferences = TestLibrary.getInstance().context().allSharedPreferences();
        Event baseEvent = TestJsonFormUtils.processJsonForm(allSharedPreferences, jsonString);
        TestUtil.processEvent(allSharedPreferences, baseEvent);
    }

    public static int getMemberProfileImageResourceIdentifier(String entityType) {
        return R.mipmap.ic_member;
    }

    public static class CloseMalariaMemberFromRegister extends AsyncTask<Void, Void, Void> {
        private final String baseEntityId;

        public CloseMalariaMemberFromRegister(String baseEntityId) {
            this.baseEntityId = baseEntityId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TestDao.closeMalariaMemberFromRegister(baseEntityId);
            return null;
        }

    }

    public static String getGenderTranslated(Context context, String gender) {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            return context.getResources().getString(R.string.male);
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            return context.getResources().getString(R.string.female);
        }
        return "";
    }
}
