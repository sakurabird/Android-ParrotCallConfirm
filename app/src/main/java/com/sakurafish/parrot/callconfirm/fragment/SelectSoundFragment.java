package com.sakurafish.parrot.callconfirm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.FragmentSelectsoundBinding;
import com.sakurafish.parrot.callconfirm.databinding.RowSelectsoundBinding;
import com.sakurafish.parrot.callconfirm.utils.CallConfirmUtils;
import com.sakurafish.parrot.callconfirm.utils.Utils;

public class SelectSoundFragment extends Fragment {
    private FragmentSelectsoundBinding binding;
    private int soundID;

    public static SelectSoundFragment newInstance() {
        SelectSoundFragment fragment = new SelectSoundFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_selectsound, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CallConfirmUtils.stopSound(soundID);
    }

    private void initView() {
        // Sound switch settings
        boolean playSound = Pref.getPrefBool(getActivity(), Config.PREF_SOUND_PLAY_SWITCH, true);
        binding.playSoundSwitch.setChecked(playSound);
        binding.playSoundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Pref.setPref(getActivity(), Config.PREF_SOUND_PLAY_SWITCH, isChecked);
            if (!isChecked) {
                CallConfirmUtils.stopSound(soundID);
            }
        });

        // Sound list settings
        SelectSoundAdapter adapter = new SelectSoundAdapter(getActivity());
        binding.listView.setAdapter(adapter);
        binding.listView.setOnItemClickListener((parent, view, position, id) -> {
            // set Pref new value
            Pref.setPref(getActivity(), getString(R.string.PREF_SOUND), String.valueOf(position));
            // Play sound
            boolean play = Pref.getPrefBool(getActivity(), Config.PREF_SOUND_PLAY_SWITCH, true);
            if (play) {
                soundID = CallConfirmUtils.playSound(getActivity());
            }
            adapter.setSelectedIndex(position);
        });
    }

    class SelectSoundData {
        String text;
        String summary;
    }

    public final class SelectSoundAdapter extends ArrayAdapter<SelectSoundData> {

        private final Context context;
        private final LayoutInflater inflater;
        private int selectedIndex = -1;

        SelectSoundAdapter(Context context) {
            super(context, 0);
            this.context = context;
            inflater = LayoutInflater.from(context);
            setupData();
        }

        private void setupData() {
            String soundIdxString = Pref.getPrefString(context, context.getString(R.string.PREF_SOUND), "0");
            int soundIdx = 0;
            try {
                soundIdx = Integer.parseInt(soundIdxString);
            } catch (NumberFormatException e) {
                Utils.logError(e.getLocalizedMessage());
            }
            selectedIndex = soundIdx;

            String[] arrayText = getResources().getStringArray(R.array.setting_sound);
            String[] arraySummary = getResources().getStringArray(R.array.setting_sound_desc);

            for (int i = 0; i < arrayText.length; i++) {
                SelectSoundData data = new SelectSoundData();
                data.text = arrayText[i];
                data.summary = arraySummary[i];
                add(data);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RowSelectsoundBinding binding;
            if (convertView == null) {
                binding = DataBindingUtil.inflate(inflater, R.layout.row_selectsound, parent, false);
                convertView = binding.getRoot();
                convertView.setTag(binding);
            } else {
                binding = (RowSelectsoundBinding) convertView.getTag();
            }
            SelectSoundData selectSoundData = getItem(position);
            if (selectSoundData != null) {
                binding.text.setText(selectSoundData.text);
                binding.summary.setText(selectSoundData.summary);
            }
            binding.radioButton.setChecked(position == selectedIndex);

            return binding.getRoot();
        }

        void setSelectedIndex(int index) {
            selectedIndex = index;
            notifyDataSetChanged();
        }
    }
}
