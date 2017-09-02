package srlabs.com.ava;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shrey on 14-09-2016.
 **/
public class fragment_firstrun_pages extends Fragment {

    public fragment_firstrun_pages() {
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
        return inflater.inflate(R.layout.fragment_firstrun_pages, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (getView() != null) {
            String page_no = getArguments().getString("page_no");

            FrameLayout first_run_fl = (FrameLayout) getView().findViewById(R.id.frame_layout_first_run_frag);
            ImageView first_run_image = (ImageView) getView().findViewById(R.id.first_run_image);
            TextView title = (TextView) getView().findViewById(R.id.first_run_title);
            TextView body = (TextView) getView().findViewById(R.id.first_run_body);
            FloatingActionButton fab_firstrun = (FloatingActionButton) getView().findViewById(R.id.fab_firstrun);

            if (page_no != null) {
                switch (page_no) {
                    case "1":
                        first_run_fl.setBackgroundResource(R.color.fr_p1);
                        first_run_image.setImageResource(R.drawable.fr_p1);
                        title.setText(R.string.firstrun_p1_t);
                        body.setText(R.string.firstrun_p1_b);
                        break;
                    case "2":
                        first_run_fl.setBackgroundResource(R.color.fr_p2);
                        first_run_image.setImageResource(R.drawable.fr_p2);
                        title.setText(R.string.firstrun_p2_t);
                        body.setText(R.string.firstrun_p2_b);
                        break;
                    case "3":
                        first_run_fl.setBackgroundResource(R.color.fr_p3);
                        first_run_image.setImageResource(R.drawable.fr_p3);
                        title.setText(R.string.firstrun_p3_t);
                        body.setText(R.string.firstrun_p3_b);
                        break;
                    case "4":
                        first_run_fl.setBackgroundResource(R.color.fr_p4);
                        first_run_image.setImageResource(R.drawable.fr_p4);
                        title.setText(R.string.firstrun_p4_t);
                        body.setText(R.string.firstrun_p4_b);
                        break;
                    case "5":
                        first_run_fl.setBackgroundResource(R.color.fr_p5);
                        first_run_image.setImageResource(R.drawable.fr_p5);
                        title.setText(R.string.firstrun_p5_t);
                        body.setText(R.string.firstrun_p5_b);
                        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager_splash_first_run_frag);
                        if (viewPager.getAdapter().getCount() != 6)
                            fab_firstrun.setVisibility(View.VISIBLE);
                        break;
                    default:
                        first_run_fl.setBackgroundResource(R.color.fr_p6);
                        first_run_image.setImageResource(R.drawable.fr_p6);
                        title.setText(R.string.firstrun_p6_t);
                        body.setText(R.string.firstrun_p6_b);
                        fab_firstrun.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.fab_fr)));
                        fab_firstrun.setVisibility(View.VISIBLE);
                        fab_firstrun.setImageResource(R.drawable.done);
                        break;
                }
            }

        }

    }


}
