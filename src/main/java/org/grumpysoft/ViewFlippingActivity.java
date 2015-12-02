package org.grumpysoft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.astuetz.viewpager.extensions.FixedTabsView;
import com.astuetz.viewpager.extensions.TabsAdapter;
import com.astuetz.viewpager.extensions.ViewPagerTabButton;
import com.google.common.collect.Lists;

import java.util.List;

public class ViewFlippingActivity extends FragmentActivity {

    private State state;
    private List<Fragment> fragments = Lists.newArrayList();
    private String[] titles = {"Directory", "Favourites", "Search"};

    @Override
    public void onBackPressed() {
        if (state.unwind()) super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flipping_views);

        final SharedPreferences preferences = getPreferences(0);
        Favourites.deserializeFrom(preferences);

        final TextView textView = (TextView) findViewById(R.id.fromOrTo);
        Utility.changeFonts(textView, getAssets(), getResources());
        state = new State(textView);

        addFragment(new StationSelectorFragment(state));
        addFragment(new FavouriteStationFragment(state));
        addFragment(new StationSearchFragment(state));

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        final FixedTabsView fixedTabsView = (FixedTabsView) findViewById(R.id.tabs);
        fixedTabsView.setAdapter(new TabsAdapter() {
            @Override
            public View getView(int position) {
                ViewPagerTabButton tab;

                LayoutInflater inflater = getLayoutInflater();
                tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed, null);

                if (position < titles.length) tab.setText(titles[position]);

                return tab;
            }
        });
        fixedTabsView.setViewPager(pager);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    private void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }
}
