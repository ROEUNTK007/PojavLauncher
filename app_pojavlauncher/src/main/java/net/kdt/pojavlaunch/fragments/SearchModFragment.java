package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.ModItemAdapter;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModpackApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModrinthApi;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.profiles.VersionSelectorDialog;

import java.util.Arrays;

public class SearchModFragment extends Fragment {

    public static final String TAG = "SearchModFragment";
    private TextView mSelectedVersion;
    private Button mSelectVersionButton;
    private EditText mSearchEditText;
    private RecyclerView mRecyclerview;
    private ModItemAdapter mModItemAdapter;

    private ModpackApi modpackApi;

    private SearchFilters mSearchFilters;


    public SearchModFragment(){
        super(R.layout.fragment_mod_search);
        modpackApi = new ModrinthApi();
        mModItemAdapter = new ModItemAdapter(modpackApi);
        mSearchFilters = new SearchFilters();
        mSearchFilters.isModpack = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSearchEditText = view.findViewById(R.id.search_mod_edittext);
        mSelectedVersion = view.findViewById(R.id.search_mod_selected_mc_version_textview);
        mSelectVersionButton = view.findViewById(R.id.search_mod_mc_version_button);
        mRecyclerview = view.findViewById(R.id.search_mod_list);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.setAdapter(mModItemAdapter);

        // Setup the expendable list behavior
        mSelectVersionButton.setOnClickListener(v -> VersionSelectorDialog.open(v.getContext(), true, (id, snapshot)->{
            mSelectedVersion.setText(id);
            mSearchFilters.mcVersion = id;
        }));

        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            PojavApplication.sExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mSearchFilters.name = mSearchEditText.getText().toString();
                    ModItem[] items = modpackApi.searchMod(mSearchFilters);
                    Log.d(SearchModFragment.class.toString(), Arrays.toString(items));
                    Tools.runOnUiThread(() -> mModItemAdapter.setModItems(items, mSelectedVersion.getText().toString()));
                }
            });

            return true;
        });
    }
}