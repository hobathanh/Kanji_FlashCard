package team.loser.kanjiflashcard.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.loser.kanjiflashcard.R;


public class AboutFragment extends Fragment {

    public static final String ABOUT_FRAGMENT_NAME = AboutFragment.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}