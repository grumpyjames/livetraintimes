package net.digihippo.ltt.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import com.astuetz.viewpager.extensions.FixedTabsView;
import com.astuetz.viewpager.extensions.TabsAdapter;
import com.astuetz.viewpager.extensions.ViewPagerTabButton;
import net.digihippo.ltt.Station;

import java.util.ArrayList;
import java.util.List;

public class ViewFlippingActivity extends FragmentActivity implements FavouriteListener {
    private List<SelectableFragment> fragments = new ArrayList<>();
    private static final String[] titles = {"Favourites", "Search"};
    private FavouriteStationFragment favouriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.digihippo.ltt.R.layout.flipping_views);

        Bundle extras = getIntent().getExtras();

        final SharedPreferences preferences = getPreferences(0);
        Favourites.deserializeFrom(preferences);

        favouriteFragment = new FavouriteStationFragment();
        StationSearchFragment searchFragment = new StationSearchFragment();
        addFragment(favouriteFragment, extras);
        addFragment(searchFragment, extras);

        final ViewPager pager = (ViewPager) findViewById(net.digihippo.ltt.R.id.pager);
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

        final FixedTabsView fixedTabsView = (FixedTabsView) findViewById(net.digihippo.ltt.R.id.tabs);
        fixedTabsView.setAdapter(new TabsAdapter() {
            @Override
            public View getView(int position) {
                ViewPagerTabButton tab;

                LayoutInflater inflater = getLayoutInflater();
                tab = (ViewPagerTabButton) inflater.inflate(net.digihippo.ltt.R.layout.tab_fixed, null);

                if (position < titles.length) tab.setText(titles[position]);

                return tab;
            }
        });
        fixedTabsView.setViewPager(pager);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {
                fixedTabsView.onPageScrolled(i, v, i1);
            }

            @Override
            public void onPageSelected(int i)
            {
                fixedTabsView.onPageSelected(i);
                for (int j = 0; j < fragments.size(); j++)
                {
                     fragments.get(j).onSelected(j == i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {
                fixedTabsView.onPageScrollStateChanged(i);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    @Override
    public void favouriteAdded(Station station) {
        favouriteFragment.favouriteAdded(station);
        Favourites.getFavourites().add(station);
    }

    @Override
    public void favouriteRemoved(Station station) {
        favouriteFragment.favouriteRemoved(station);
        Favourites.getFavourites().remove(station);
    }

    private void addFragment(SelectableFragment fragment, Bundle extras) {
        fragments.add(fragment);
        fragment.setArguments(extras);
    }
}
