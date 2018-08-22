package com.github.irshulx.wysiwyg;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SerializedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SerializedFragment extends Fragment {
    private static final String SERIALIZED = "";

    // TODO: Rename and change types of parameters
    private String mSerialized;

    public SerializedFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serialized Parameter 1.
     * @return A new instance of fragment PreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SerializedFragment newInstance(String serialized) {
        SerializedFragment fragment = new SerializedFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED, serialized);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSerialized = getArguments().getString(SERIALIZED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_serialized, container, false);
        ((TextView)view.findViewById(R.id.lblRendered)).setText(mSerialized);
        return view;
    }

}
