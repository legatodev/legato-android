package com.example.spoluri.legato;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";
    private NearbyUsersAdapter adapter;

    public FilterDialogFragment(NearbyUsersAdapter adapter){
        this.adapter = adapter;
    }

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private View mRootView;

    @BindView(R.id.lookfor)
    Spinner mLookingfor;

    @BindView(R.id.genres)
    Spinner mGenres;

    @BindView(R.id.skills)
    Spinner mSkills;

    private FilterListener mFilterListener;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.buttonSearch)
    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    @OnClick(R.id.buttonCancel)
    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedLookingfor() {
        String selected = (String) mLookingfor.getSelectedItem();
        if (getString(R.string.value_any_lookfor).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }
    @Nullable
    private String getSelectedGenres() {
        String selected = (String) mGenres.getSelectedItem();
        if (getString(R.string.value_any_lookfor).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }
    @Nullable
    private String getSelectedSkills() {
        String selected = (String) mSkills.getSelectedItem();
        if (getString(R.string.value_any_lookfor).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if(mRootView != null){
            filters.setLookingfor(getSelectedLookingfor());
            filters.setGenres(getSelectedLookingfor());
            filters.setSkills(getSelectedLookingfor());
        }

        return filters;
    }
}
