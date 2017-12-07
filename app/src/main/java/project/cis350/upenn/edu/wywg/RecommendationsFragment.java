package project.cis350.upenn.edu.wywg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

/**
 * Created by abhaved on 4/9/17.
 */

public class RecommendationsFragment extends DialogFragment {

    public EditText etCostFilter;
    boolean cost = false;
    boolean rating = false;
    public CheckBox cbCost;
    public CheckBox cbRating;
    RatingBar rb;
    View v;


    public RecommendationsFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RecommendationsFragment newInstance(String title) {
        RecommendationsFragment frag = new RecommendationsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.recommendations_fragment, container);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;

        rb = (RatingBar) view.findViewById(R.id.ratingBar);
        etCostFilter = (EditText) view.findViewById(R.id.etCostFilter);
        cbCost = (CheckBox) view.findViewById(R.id.cbCost);
        cbRating = (CheckBox) view.findViewById(R.id.cbRating);


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}