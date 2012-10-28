package org.grumpysoft;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.grumpysoft.impl.Stations;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SelectorTabsActivity extends FragmentActivity {

    private TabManager tabManager;
    private TabHost host;

    @Override
    public void onBackPressed() {
        State.unsetStation();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_selectors);
        
        setFromOrTo();

        host = (TabHost) findViewById(android.R.id.tabhost);
        host.setup();

        tabManager = new TabManager(this, host, R.id.realtabcontent);

        tabManager.addTab(host.newTabSpec("List").setIndicator("List", getResources().getDrawable(R.drawable.ic_tab_a_to_z)),
                StationSelectorFragment.class, savedInstanceState);
        tabManager.addTab(host.newTabSpec("Favourites").setIndicator("Favourites", getResources().getDrawable(R.drawable.ic_tab_favourite)),
                FavouriteStationFragment.class, savedInstanceState);
        tabManager.addTab(host.newTabSpec("Search").setIndicator("Search", getResources().getDrawable(R.drawable.ic_tab_search)),
                StationSearchFragment.class, savedInstanceState);

        if (savedInstanceState != null) {
            host.setCurrentTabByTag(savedInstanceState.getString("tab"));
            for (Station station : deserializeFavouritesFrom(savedInstanceState.getCharSequenceArray("favourites")))
                Favourites.toggleFavourite(station, true);
        }
    }

    private List<Station> deserializeFavouritesFrom(CharSequence[] favourites) {
        return Lists.transform(ImmutableList.copyOf(favourites), new Function<CharSequence, Station>() {
            @Override
            public Station apply(CharSequence charSequence) {
                return Iterables.getOnlyElement(StationService.findStations(charSequence.toString()));
            }
        });
    }

    private void setFromOrTo() {
        final TextView textView = (TextView) findViewById(R.id.fromOrTo);
        final Typeface tf = Typeface.createFromAsset(getAssets(), "britrln.ttf");
        textView.setTypeface(tf);
        textView.setText(State.fromOrTo());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", host.getCurrentTabTag());
        outState.putCharSequenceArray("favourites", currentFavouritesAsArray());
    }

    private CharSequence[] currentFavouritesAsArray() {
        return Lists.transform(ImmutableList.copyOf(currentFavourites()), new Function<Station, CharSequence>() {
            @Override
            public CharSequence apply(Station station) {
                return station.threeLetterCode();
            }
        }).toArray(new CharSequence[currentFavourites().size()]);
    }

    private Set<Station> currentFavourites() {
        return Favourites.getFavourites();
    }

    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost host;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context context;

            public DummyTabFactory(Context context) {
                this.context = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(context);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            host = tabHost;
            mContainerId = containerId;
            host.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            host.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }


}
