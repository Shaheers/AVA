package srlabs.com.ava;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shrey on 14-09-2016.
 **/
public class fragment_splash_first_run extends Fragment {
    CircleImageView ciarray[] = new CircleImageView[6];
    private ViewPager viewPager;

    public fragment_splash_first_run() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_first_run_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getView() != null) {
            viewPager = (ViewPager) getView().findViewById(R.id.viewpager_splash_first_run_frag);
            ciarray[0] = (CircleImageView) getView().findViewById(R.id.ci1);
            ciarray[1] = (CircleImageView) getView().findViewById(R.id.ci2);
            ciarray[2] = (CircleImageView) getView().findViewById(R.id.ci3);
            ciarray[3] = (CircleImageView) getView().findViewById(R.id.ci4);
            ciarray[4] = (CircleImageView) getView().findViewById(R.id.ci5);
            ciarray[5] = (CircleImageView) getView().findViewById(R.id.ci6);
            viewPager.setPageTransformer(false, new FadePageTransformer());
            setupViewPager(viewPager);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new fragment_firstrun_pages(), "1");
        adapter.addFragment(new fragment_firstrun_pages(), "2");
        adapter.addFragment(new fragment_firstrun_pages(), "3");
        adapter.addFragment(new fragment_firstrun_pages(), "4");
        adapter.addFragment(new fragment_firstrun_pages(), "5");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && !checkPermission()) {
            ciarray[5].setVisibility(View.VISIBLE);
            adapter.addFragment(new fragment_firstrun_pages(), "6");
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        ciarray[0].setImageResource(R.color.white);
        for (int i = 1; i < 6; i++) ciarray[i].setImageResource(R.color.semi_trans);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            int prevpos;

            @Override
            public void onPageSelected(int position) {
                ciarray[prevpos].setImageResource(R.color.semi_trans);
                ciarray[position].setImageResource(R.color.white);
                prevpos = position;
            }
        });

    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED ;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            Bundle bundle = new Bundle();
            bundle.putString("page_no", title);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            fragment.setArguments(bundle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            // Ensures the views overlap each other.
            view.setTranslationX(view.getWidth() * -position);

            // Alpha property is based on the view position.
            if (position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if (position == 0.0F) {
                view.setAlpha(1.0F);
            } else { // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setAlpha(1.0F - Math.abs(position));
            }


            view.findViewById(R.id.first_run_title).setTranslationX(view.getWidth() * position);
            view.findViewById(R.id.first_run_body).setTranslationX(view.getWidth() * position * 0.8f);
            view.findViewById(R.id.first_run_image).setTranslationX(view.getWidth() * position * 1.6f);
        }

    }
}