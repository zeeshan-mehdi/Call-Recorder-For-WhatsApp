package com.shaikhutech.callrecorder.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shaikhutech.callrecorder.BuildConfig;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.shaikhutech.callrecorder.MainActivity;
import com.shaikhutech.callrecorder.R;
import com.shaikhutech.callrecorder.adapter.OutgoingAdapter;
import com.shaikhutech.callrecorder.contacts.ContactProvider;
import com.shaikhutech.callrecorder.pojo_classes.Contacts;
import com.shaikhutech.callrecorder.utils.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class Outgoing extends Fragment implements MainActivity.refreshstener, ActionMode.Callback {
    private static final String TAG = "Outgoing";

    private OutgoingAdapter recyclerAdapter;
    RecyclerView recyclerView;
    int temp;
    ArrayList<String> recording2 = new ArrayList<>();
    ArrayList<Contacts> recordedContacts = new ArrayList<>();
    ArrayList<Object> searchPeople = new ArrayList<>();
    ArrayList<Integer> integers = new ArrayList<>();
    ArrayList<Object> realrecordingcontact = new ArrayList<>();
    TreeMap<String, ArrayList<Contacts>> headerevent = new TreeMap<>();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    boolean mensu = false;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout message;
    Context ctx;
    private Menu mSelectionMenu;

    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    private List<Object> selectedContactsTimeStamp = new ArrayList<>();

    public Outgoing() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.outgoing_fragment, container, false);
        ctx = view.getContext();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(Color.parseColor("#dadde2"))
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        recyclerView.setHasFixedSize(true);
        message = view.findViewById(R.id.hidemessage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new OutgoingAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        Bundle bundle;
        bundle = getArguments();
        MainActivity.setrefreshlistener(this);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!(((MainActivity) getActivity()).setSearchQuery())) {
                    refreshItems();
                }
                // Refresh items
                //refreshItems();
            }
        });
        recording2 = bundle.getStringArrayList("RECORDING");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ctx.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            showContact();
        }
        if (realrecordingcontact.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
        }
        recyclerAdapter.setContacts(realrecordingcontact);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: ");
//                Log.i(TAG, "onItemClick: view: " + view.toString());
//                Log.i(TAG, "onItemClick: position: " + position);
                if (isMultiSelect) {
                    //if multiple selection is enabled then select item on single click else perform normal click on item.
                    multiSelect(position);
                } else {
                    if (mensu) {
                        if (searchPeople.get(position) instanceof Contacts) {
                            Contacts contacts1 = (Contacts) searchPeople.get(position);
                            String records = ContactProvider.getRecordingNameByContactAndType(view.getContext(), recording2, "OUT", contacts1);
                            if (Build.VERSION.SDK_INT > 18) {
                                ContactProvider.openMaterialSheetDialog(getLayoutInflater(), position, records, StringUtils.prepareContacts(ctx, contacts1.getNumber()));
                            } else {
                                ContactProvider.showDialog(view.getContext(), records, contacts1);
                            }
                        }
                    } else {
                        if (realrecordingcontact.get(position) instanceof Contacts) {
                            Contacts contacts = (Contacts) realrecordingcontact.get(position);
                            String records = ContactProvider.getRecordingNameByContactAndType(view.getContext(), recording2, "OUT", contacts);
                            if (Build.VERSION.SDK_INT > 18) {
                                ContactProvider.openMaterialSheetDialog(getLayoutInflater(), position, records, StringUtils.prepareContacts(ctx, contacts.getNumber()));
                            } else {
                                ContactProvider.showDialog(view.getContext(), records, contacts);
                            }
                        }
                    }
                    ContactProvider.setItemrefresh(new ContactProvider.refresh() {
                        @Override
                        public void refreshList(boolean var) {
                            if (var)
                                showContact();
                        }
                    });
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "onItemLongClick: ");
//                Log.i(TAG, "onItemClick: view: " + view.toString());
//                Log.i(TAG, "onItemClick: position: " + position);
                if (!isMultiSelect) {
                    selectedContactsTimeStamp = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = Outgoing.this.getActivity().startActionMode(Outgoing.this); //show ActionMode.
                        ((MainActivity) getActivity()).mActionMode = actionMode;

                    }
                }

                multiSelect(position);
            }
        }));

        MainActivity.setQueylistener3(new MainActivity.querySearch3() {
            @Override
            public void Search_name3(String name) {
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(ctx, ctx.getString(R.string.records_refreshed), Toast.LENGTH_SHORT).show();
                    }
                }
                if (name.length() > 2) {
                    mensu = true;
                    searchPeople.clear();
                    for (Contacts contacts : recordedContacts) {
                        if (contacts.getNumber().contains(name)) {
                            searchPeople.add(contacts);
                            continue;
                        }
                        if (contacts.getName() != null && contacts.getName().toLowerCase().contains(name.toLowerCase())) {
                            searchPeople.add(contacts);
                        }

                    }
                    recyclerAdapter.setContacts(searchPeople);
                    recyclerAdapter.notifyDataSetChanged();

                } else {
                    mensu = false;
                    if (realrecordingcontact.isEmpty()) {
                        message.setVisibility(View.VISIBLE);
                    } else {
                        message.setVisibility(View.GONE);
                    }
                    recyclerAdapter.setContacts(realrecordingcontact);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
        refreshItems();
        return view;
    }

    private void multiSelect(int position) {
        Contacts contact = recyclerAdapter.getItem(position);
        if (contact != null) {
            if (actionMode != null) {
                if (selectedContactsTimeStamp.contains(contact.getTimestamp()))
                    selectedContactsTimeStamp.remove(contact.getTimestamp());
                else
                    selectedContactsTimeStamp.add(contact.getTimestamp());

                if (selectedContactsTimeStamp.size() > 0)
                    actionMode.setTitle(String.valueOf(selectedContactsTimeStamp.size())); //show selected item count on action mode.
                else {
                    actionMode.setTitle(""); //remove item count from action mode.
                    actionMode.finish(); //hide action mode.
                }
                recyclerAdapter.setSelectedContactsTimeStamps(selectedContactsTimeStamp);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContact();
            } else {
                Toast.makeText(getContext(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void refreshItems() {
        recording2 = ContactProvider.showAllRecordedlistfiles(ctx);
        showContact();
        if (realrecordingcontact.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
        }
        recyclerAdapter.setContacts(realrecordingcontact);
        recyclerAdapter.notifyDataSetChanged();
        MainActivity.fetchSearchRecords();
    }

    private void showContact() {
        headerevent.clear();
        recording2 = ContactProvider.showAllRecordedlistfiles(ctx);
        ArrayList<Contacts> contactses = new ArrayList<>();
        if (!realrecordingcontact.isEmpty()) {
            realrecordingcontact.clear();
        }
        if (!recordedContacts.isEmpty()) {
            recordedContacts.clear();
        }
        //crash  recordedContacts = ContactProvider.getCallList(getContext(), recording2, "OUT");
        if (getContext() != null) {
            recordedContacts = ContactProvider.getCallList(getContext(), recording2, "OUT");
        }
//        Log.d(TAG, "showContacts: recordedContacts: " + recordedContacts);
        for (Contacts contacts : recordedContacts) {
            if (contacts.getView() == 1) {
                if (!headerevent.containsKey("1")) {
                    headerevent.put("1", new ArrayList<Contacts>());
                }
                headerevent.get("1").add(contacts);
            } else if (contacts.getView() == 2) {
                if (!headerevent.containsKey("2")) {
                    headerevent.put("2", new ArrayList<Contacts>());
                }
                headerevent.get("2").add(contacts);
            } else {
                if (!headerevent.containsKey(contacts.getDate())) {
                    headerevent.put(contacts.getDate(), new ArrayList<Contacts>());
                }
                headerevent.get(contacts.getDate()).add(contacts);
            }
        }
        for (String date2 : headerevent.keySet()) {
            if (date2.equals("1")) {
                if (headerevent.keySet().contains("2")) {
                    date2 = "2";
                }
            } else if (date2.equals("2")) {
                if (headerevent.keySet().contains("1")) {
                    date2 = "1";
                }
            }
            contactses.clear();
            for (Contacts contacts : headerevent.get(date2)) {
                contactses.add(contacts);
            }
            for (Contacts contacts : sorts(contactses)) {
                realrecordingcontact.add(contacts);
            }
            realrecordingcontact.add(date2);
        }
        if (realrecordingcontact.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
        }
        recyclerAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(ctx, ctx.getString(R.string.records_refreshed), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Contacts> sorts(ArrayList<Contacts> contactses) {
        Collections.sort(contactses);
        return contactses;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_select, menu);
        mSelectionMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                //just to show selected items.
//                StringBuilder stringBuilder = new StringBuilder();
                for (Contacts contacts : recordedContacts) {
                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
//                        stringBuilder.append("\n").append(contacts.getName() + " " + contacts.getTimestamp());
                        String recording = ContactProvider.getRecordingNameByContactAndType(ctx, recording2, "OUT", contacts);
                        File file = new File(ContactProvider.getFolderPath(ctx) + "/" + recording);
                        if (file.delete()) {
                            //deleted
                            Toast.makeText(ctx, ctx.getString(R.string.recording_deleted), Toast.LENGTH_SHORT).show();
                        } else {
                            //not deleted
                            Toast.makeText(ctx, ctx.getString(R.string.recording_deletion_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                refreshItems();
                finishActionMode();
//                Toast.makeText(getActivity(), "Selected items are :" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_select_all: {
                if (mSelectionMenu.findItem(R.id.action_share).isVisible()) {
                    mSelectionMenu.findItem(R.id.action_share).setVisible(false);
                    getActivity().invalidateOptionsMenu();
                    selectedContactsTimeStamp = recyclerAdapter.selectAll();
                    actionMode.setTitle(String.valueOf(selectedContactsTimeStamp.size())); //show selected item count on action mode.
                } else {
                    mSelectionMenu.findItem(R.id.action_share).setVisible(true);
                    getActivity().invalidateOptionsMenu();
                    isMultiSelect = false;
                    selectedContactsTimeStamp = new ArrayList<>();
                    recyclerAdapter.setSelectedContactsTimeStamps(selectedContactsTimeStamp);
                    finishActionMode();
                }
                return true;
            }
            case R.id.action_share:
                for (Contacts contacts : recordedContacts) {
                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
                        String recording = ContactProvider.getRecordingNameByContactAndType(ctx, recording2, "OUT", contacts);
                        File file = new File(ContactProvider.getFolderPath(ctx) + "/" + recording);
                        Uri fileuriShare = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", file);
                        Intent sendintent = new Intent(Intent.ACTION_SEND);
                        sendintent.putExtra(Intent.EXTRA_STREAM, fileuriShare);
                        sendintent.setType("audio/*");
                        ctx.startActivity(sendintent);
                        Toast.makeText(ctx, "Sharing " + file.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
                finishActionMode();
                return true;

            case R.id.action_cloud:
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setTitle("Upgrade to pro");
                alert.setMessage("Cloud storage feature is available in pro version only. Do you want to upgrade? ");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.callrecorder.procallrecorder")));
                        } catch (Exception e) {
                            Toast.makeText(ctx, "Play store not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();

                // This is free version hence commented the code of cloud
//                for (Contacts contacts : recordedContacts) {
//                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
//                        String recording = ContactProvider.getRecordingNameByContactAndType(ctx, recording2, "OUT", contacts);
//
//                        ISaver mSaver;
//                        String ONEDRIVE_APP_ID = "60db8ba3-71aa-4a33-bde2-0ba7ef9d0d79";
//                        final File f = new File(ContactProvider.getFolderPath(ctx) + "/", recording);
//                        mSaver = Saver.createSaver(ONEDRIVE_APP_ID);
//                        Uri fileuri = Uri.parse("file://" + f.getAbsolutePath());
////                Uri fileuri = FileProvider.getUriForFile(view.getContext(),BuildConfig.APPLICATION_ID + ".provider", f);
//                        mSaver.startSaving((Activity) ctx, recording, Uri.fromFile(f));
//                        Toast.makeText(ctx, "Uploading to cloud " + f.getName(), Toast.LENGTH_SHORT).show();
//                    }
//                }
                finishActionMode();
                return true;

            case R.id.action_favourite:
                for (Contacts contacts : recordedContacts) {
                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
                        if (ContactProvider.checkFav(ctx, contacts.getNumber())) {
                            if (ContactProvider.checkFavourite(ctx, contacts.getNumber())) {
                                Toast.makeText(ctx, ctx.getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, ctx.getString(R.string.removed_from_favourites), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                refreshItems();
                finishActionMode();
                return true;

            case R.id.action_removefavourite:
                for (Contacts contacts : recordedContacts) {
                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
                        if (!ContactProvider.checkFav(ctx, contacts.getNumber())) {
                            if (ContactProvider.checkFavourite(ctx, contacts.getNumber())) {
                                Toast.makeText(ctx, ctx.getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, ctx.getString(R.string.removed_from_favourites), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                refreshItems();
                finishActionMode();
                return true;

            case R.id.action_turnoff:
                Toast.makeText(ctx, getString(R.string.recording_turned_off), Toast.LENGTH_SHORT).show();
                for (Contacts contacts : recordedContacts) {
                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
                        //turn off recording
                        if (ContactProvider.checkContactStateToRecord(ctx, contacts.getNumber())) {
                            // recording enabled turn it off
                            if (!ContactProvider.togglestate(ctx, contacts.getNumber())) {
                                //off
                                //Toast.makeText(ctx, "turned off", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                refreshItems();
                finishActionMode();
                return true;

            case R.id.action_turnon:
                Toast.makeText(ctx, getString(R.string.recording_turned_on), Toast.LENGTH_SHORT).show();
                for (Contacts contacts : recordedContacts) {
                    if (selectedContactsTimeStamp.contains(contacts.getTimestamp())) {
                        //turn off recording
                        if (!ContactProvider.checkContactStateToRecord(ctx, contacts.getNumber())) {
                            if (ContactProvider.togglestate(ctx, contacts.getNumber())) {
                                // Toast.makeText(ctx, "turned on", Toast.LENGTH_SHORT).show();
                            }
                            //recording disabled turn it on
                        }
                    }
                }
                refreshItems();
                finishActionMode();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        actionMode = null;
        isMultiSelect = false;
        selectedContactsTimeStamp = new ArrayList<>();
        recyclerAdapter.setSelectedContactsTimeStamps(new ArrayList<Object>());
        ((MainActivity) getActivity()).mActionMode = null;
    }

    private void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(" FRAGMENT " + Outgoing.class.getSimpleName(), " refresh ");
        //refreshItems();
    }

    @Override
    public void refresh(boolean b) {
        if (b) {
            refreshItems();
        }

    }

    public void updateView() {

    }
}
