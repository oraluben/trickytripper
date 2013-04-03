package de.koelle.christian.trickytripper.activities;

import java.util.Currency;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.koelle.christian.common.abs.ActionBarSupport;
import de.koelle.christian.common.options.OptionContraintsAbs;
import de.koelle.christian.common.utils.CurrencyUtil;
import de.koelle.christian.trickytripper.R;
import de.koelle.christian.trickytripper.TrickyTripperApp;
import de.koelle.christian.trickytripper.activitysupport.PopupFactory;
import de.koelle.christian.trickytripper.constants.Rc;
import de.koelle.christian.trickytripper.constants.Rd;

public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configurePreferencesToBeUsed();
        setPreferenceScreen(createPreferenceHierarchy());
        
        ActionBarSupport.addBackButton(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Rc.PREFS_VALUE_ID_BASE_CURRENCY)) {
            Preference currencyDefaultPrefs = findPreference(key);
            // The following is expected to result in a currency string that is
            // supported by the runtime.
            String string = sharedPreferences.getString(key, "");
            currencyDefaultPrefs.setSummary(getDisplayStringForCurrency(Currency.getInstance(string)));
        }
    }

    private void configurePreferencesToBeUsed() {
        getPreferenceManager().setSharedPreferencesMode(Rc.PREFS_MODE);
        getPreferenceManager().setSharedPreferencesName(Rc.PREFS_NAME_ID);
    }

    private PreferenceScreen createPreferenceHierarchy() {

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        /*
         * =========== Launcher for the exchange rate management =============
         */
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
        intentPref.setIntent(new Intent().setClass(this, ManageExchangeRatesActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        intentPref.setTitle(R.string.prefs_view_title_exchange_rate_management);

        root.addPreference(intentPref);

        /* =============== Default currency picker ================= */
        Currency defaultBaseCurrency = getApp().getMiscController()
                .getDefaultBaseCurrency();

        ListPreference listPref = new ListPreference(this);
        listPref.setEntries(CurrencyUtil.getSupportedCurrencyFullNames(getResources()));
        listPref.setEntryValues(CurrencyUtil.getSupportedCurrencyCodes(getResources()));
        listPref.setKey(Rc.PREFS_VALUE_ID_BASE_CURRENCY);
        listPref.setDialogTitle(R.string.prefs_view_heading_chooser_currency);
        listPref.setTitle(R.string.prefs_view_title_currency);
        listPref.setDefaultValue(defaultBaseCurrency.getCurrencyCode());
        listPref.setSummary(getDisplayStringForCurrency(defaultBaseCurrency));

        root.addPreference(listPref);

        return root;
    }

    private String getDisplayStringForCurrency(Currency currency) {
        return CurrencyUtil.getFullNameToCurrency(getResources(), currency);
    }
	private TrickyTripperApp getApp() {
		return (TrickyTripperApp)getApplication();
	}

    /* ============== Options Shit [BGN] ============== */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return getApp().getMiscController().getOptionSupport().populateOptionsMenu(
                new OptionContraintsAbs().activity(getSupportMenuInflater()).menu(menu)
                        .options(new int[] {
                                R.id.option_help
                        }));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.option_help:
            showDialog(Rd.DIALOG_HELP);
            return true;
        case android.R.id.home:
            onBackPressed();
            return true;                  
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    /* ============== Options Shit [END] ============== */
    /* ============== Dialog Shit [BGN] ============== */

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog;
        switch (id) {
        case Rd.DIALOG_HELP:
            dialog = PopupFactory.createHelpDialog(this, getApp().getMiscController(), Rd.DIALOG_HELP);
            break;
        default:
            dialog = null;
        }

        return dialog;
    }
    /* ============== Dialog Shit [END] ============== */
}
