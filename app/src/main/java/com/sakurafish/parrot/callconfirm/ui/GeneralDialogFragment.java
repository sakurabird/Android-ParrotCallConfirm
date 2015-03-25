package com.sakurafish.parrot.callconfirm.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

public class GeneralDialogFragment extends DialogFragment {

    /**
     * コールバック.
     */
    private Callbacks mCallbacks;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        if (getTargetFragment() != null && getTargetFragment() instanceof Callbacks) {
            mCallbacks = (Callbacks) getTargetFragment();
        } else if (getActivity() instanceof Callbacks) {
            mCallbacks = (Callbacks) getActivity();
        } else {
            mCallbacks = new Callbacks() {
                @Override
                public void onDialogClicked(final String tag, final Bundle args, final int which, final boolean isChecked) {
                    dismiss();
                }

                @Override
                public void onDialogCancelled(final String tag, final Bundle args) {
                    dismiss();
                }
            };
        }

        // ダイアログのボタンを押された時のリスナを定義する.
        final DialogInterface.OnMultiChoiceClickListener multiChoiceListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                mCallbacks.onDialogClicked(getTag(), getArguments().getBundle("params"), which,
                        isChecked);
            }
        };
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCallbacks.onDialogClicked(getTag(), getArguments().getBundle("params"), which,
                        false);
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();

        if (args.getBoolean("progress")) {
            final GeneralProgressDialog dialog = new GeneralProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            if (args.containsKey("title")) {
                dialog.setTitle(args.getString("title"));
            }
            if (args.containsKey("progress_style")) {
                final int style = args.getInt("progress_style");
                dialog.setProgressStyle(style);
                if (style == ProgressDialog.STYLE_HORIZONTAL) {
                    dialog.setIndeterminate(false);
                }
            }
            if (args.containsKey("progress_max")) {
                dialog.setMax(args.getInt("progress_max"));
            }
            dialog.setMessage(args.getString("message"));
            dialog.setCancelable(args.getBoolean("cancelable"));
            return dialog;
        }

        if (args.containsKey("icon")) {
            builder.setIcon(args.getInt("icon"));
        }
        if (args.containsKey("title")) {
            builder.setTitle(args.getString("title"));
        }
        builder.setMessage(args.getString("message"));
        if (args.containsKey("positive_text")) {
            builder.setPositiveButton(args.getString("positive_text"), listener);
        }
        if (args.containsKey("negative_text")) {
            builder.setNegativeButton(args.getString("negative_text"), listener);
        }
        if (args.containsKey("neutral_text")) {
            builder.setNeutralButton(args.getString("neutral_text"), listener);
        }
        if (args.containsKey("checked_items")) {
            builder.setMultiChoiceItems(args.getCharSequenceArray("items"),
                    args.getBooleanArray("checked_items"), multiChoiceListener);
        } else if (args.containsKey("checked_item")) {
            builder.setSingleChoiceItems(args.getCharSequenceArray("items"),
                    args.getInt("checked_item"), listener);
        } else if (args.containsKey("items")) {
            builder.setItems(args.getCharSequenceArray("items"), listener);
        }
        return builder.create();
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        mCallbacks.onDialogCancelled(getTag(), getArguments().getBundle("params"));
    }

    /**
     * 各ボタンを押下した際のコールバック.
     */
    public interface Callbacks {

        /**
         * ダイアログのボタン及びリストを押下した際のイベント処理.
         *
         * @param tag       Fragmentのタグ
         * @param args      setParamsで渡されたパラメータ
         * @param which     DialogInterfaceのID
         * @param isChecked 複数選択項目で選択されたかどうか(MultiChoiceItemsでのみ使用される)
         */
        void onDialogClicked(String tag, Bundle args, int which, boolean isChecked);

        /**
         * ダイアログがキャンセルされた際のイベント処理.
         *
         * @param tag  Fragmentのタグ
         * @param args setParamsで渡されたパラメータ
         */
        void onDialogCancelled(String tag, Bundle args);
    }

    /**
     * MyDialogFragmentを生成する為のビルダー.
     */
    public static class Builder {

        /**
         * アイコンのリソースID.
         */
        private int mIcon = -1, mCheckedItem = -1;

        /**
         * タイトル. メッセージ. PositiveButtonのラベル, NegativeButtonのラベル,
         * NeutralButtonのラベル.
         */
        private String mTitle, mMessage, mPositiveText, mNegativeText, mNeutralText;

        /**
         * アイテム系.
         */
        private CharSequence[] mItems;

        /**
         * MultiChoice時の選択されている項目.
         */
        private boolean[] mCheckedItems;

        /**
         * キャンセル可能かどうか.
         */
        private boolean mCancelable = true;

        /**
         * パラメータ.
         */
        private Bundle mParams;

        /**
         * リクエストコード.
         */
        private int mRequestCode;

        /**
         * 処理対象（コールバック実行対象）となるフラグメント.
         */
        private Fragment mTargetFragment;

        private boolean mProgress = false;
        private int mProgressMax = -1;
        private int mProgressStyle = -1;

        /**
         * アイコンのリソースIDをセットする.
         *
         * @param icon アイコンのリソースID
         * @return MyDialogFragmentのBuilder
         */
        public Builder setIcon(final int icon) {
            mIcon = icon;
            return this;
        }

        /**
         * タイトルをセットする.
         *
         * @param title タイトル
         * @return MyDialogFragmentのBuilder
         */
        public Builder setTitle(final String title) {
            mTitle = title;
            return this;
        }

        /**
         * メッセージをセットする.
         *
         * @param message メッセージ
         * @return MyDialogFragmentのBuilder
         */
        public Builder setMessage(final String message) {
            mMessage = message;
            return this;
        }

        /**
         * PositiveButtonのラベルをセットする.
         *
         * @param positiveText PositiveButtonのラベル
         * @return MyDialogFragmentのBuilder
         */
        public Builder setPositiveText(final String positiveText) {
            mPositiveText = positiveText;
            return this;
        }

        /**
         * NegativeButtonのラベルをセットする.
         *
         * @param negativeText NegativeButtonのラベル
         * @return MyDialogFragmentのBuilder
         */
        public Builder setNegativeText(final String negativeText) {
            mNegativeText = negativeText;
            return this;
        }

        /**
         * NeutralButtonのラベルをセットする.
         *
         * @param neutralText NeutralButtonのラベル
         * @return MyDialogFragmentのBuilder
         */
        public Builder setNeutralText(final String neutralText) {
            mNeutralText = neutralText;
            return this;
        }

        /**
         * Itemsをセットする.
         *
         * @param items 複数選択項目
         * @return MyDialogFragmentのBuilder
         */
        public Builder setItems(final CharSequence[] items) {
            mItems = items;
            return this;
        }

        /**
         * SingleChoiceItemsをセットする.
         *
         * @param items       複数選択項目
         * @param checkedItem 選択されている項目
         * @return MyDialogFragmentのBuilder
         */
        public Builder setSingleChoiceItems(final CharSequence[] items, final int checkedItem) {
            mItems = items;
            mCheckedItem = checkedItem;
            return this;
        }

        /**
         * MultiChoiceItemsをセットする.
         *
         * @param items        複数選択項目
         * @param checkedItems 選択されている項目
         * @return MyDialogFragmentのBuilder
         */
        public Builder setMultiChoiceItems(final CharSequence[] items, final boolean[] checkedItems) {
            mItems = items;
            mCheckedItems = checkedItems;
            return this;
        }

        /**
         * キャンセル可能かどうかをセットする.
         *
         * @param cancelable キャンセル可能かどうか
         * @return MyDialogFragmentのBuilder
         */
        public Builder setCancelable(final boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        /**
         * パラメータをセットする.
         *
         * @param params パラメータ
         * @return MyDialogFragmentのBuilder
         */
        public Builder setParams(final Bundle params) {
            mParams = new Bundle(params);
            return this;
        }

        /**
         * 対象となるフラグメントと, そのリクエストコードをセットする.
         *
         * @param targetFragment 対象となるフラグメント
         * @param requestCode    リクエストコード
         * @return MyDialogFragmentのBuilder
         */
        public Builder setTargetFragment(final Fragment targetFragment, final int requestCode) {
            mTargetFragment = targetFragment;
            mRequestCode = requestCode;
            return this;
        }

        /**
         * 今まで与えられた引数に基づき, MyDialogFragmentをビルドする.
         *
         * @return MyDialogFragment
         */
        public GeneralDialogFragment create() {
            GeneralDialogFragment f = new GeneralDialogFragment();
            if (mTargetFragment != null) {
                f.setTargetFragment(mTargetFragment, mRequestCode);
            }
            Bundle args = new Bundle();
            if (mIcon != -1) {
                args.putInt("icon", mIcon);
            }
            args.putString("title", mTitle);
            args.putString("message", mMessage);
            if (!TextUtils.isEmpty(mPositiveText)) {
                args.putString("positive_text", mPositiveText);
            }
            if (!TextUtils.isEmpty(mNegativeText)) {
                args.putString("negative_text", mNegativeText);
            }
            if (!TextUtils.isEmpty(mNeutralText)) {
                args.putString("neutral_text", mNeutralText);
            }
            if (mItems != null) {
                args.putCharSequenceArray("items", mItems);
            }
            if (mCheckedItem != -1) {
                args.putInt("checked_item", mCheckedItem);
            }
            if (mCheckedItems != null) {
                args.putBooleanArray("checked_items", mCheckedItems);
            }
            if (mParams != null) {
                args.putBundle("params", mParams);
            }
            args.putBoolean("progress", mProgress);
            args.putBoolean("cancelable", mCancelable);
            if (mProgressMax != -1) {
                args.putInt("progress_max", mProgressMax);
            }
            if (mProgressStyle != -1) {
                args.putInt("progress_style", mProgressStyle);
            }
            f.setCancelable(mCancelable);
            f.setArguments(args);
            return f;
        }

        public void setProgressbar(final boolean b) {
            mProgress = b;
        }

        public void setProgressStyle(final int style) {
            mProgressStyle = style;
        }

        public void setProgressMax(final int max) {
            mProgressMax = max;
        }
    }
}
