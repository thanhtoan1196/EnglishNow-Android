package com.iceteaviet.englishnow.ui.main.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;

import com.iceteaviet.englishnow.data.DataManager;
import com.iceteaviet.englishnow.data.model.others.StatusItemData;
import com.iceteaviet.englishnow.ui.base.BaseViewModel;
import com.iceteaviet.englishnow.ui.main.MainNavigator;
import com.iceteaviet.englishnow.utils.rx.SchedulerProvider;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by Genius Doan on 29/12/2017.
 */

public class MainViewModel extends BaseViewModel<MainNavigator> {

    private final ObservableField<String> appVersion = new ObservableField<>();
    private final ObservableField<String> userName = new ObservableField<>();
    private final ObservableField<String> userEmail = new ObservableField<>();
    private final ObservableField<String> userProfilePicUrl = new ObservableField<>();
    private final ObservableArrayList<StatusItemData> statusItems = new ObservableArrayList<>(); //TODO: try to use list viewmodel

    private final MutableLiveData<List<StatusItemData>> statusItemsLiveData;


    public MainViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        statusItemsLiveData = new MutableLiveData<>();
        loadNewsFeedItems();
    }

    private void loadNewsFeedItems() {
        getCompositeDisposable().add(getDataManager()
                .getAllStatusItems()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<StatusItemData>>() {
                    @Override
                    public void accept(List<StatusItemData> statusItemData) throws Exception {
                        if (statusItemData != null) {
                            statusItemsLiveData.setValue(statusItemData);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }));
    }

    public void deleteNewsFeedItem(int position) {
        statusItems.remove(position);
        statusItemsLiveData.getValue().remove(position);
    }

    public void setNewsFeedItems(List<StatusItemData> list) {
        statusItems.clear();
        statusItems.addAll(list);
    }

    public void updateAppVersion(String version) {
        appVersion.set(version);
    }

    public void onNavigationViewCreated() {
        final String currentUserName = getDataManager().getCurrentUserDisplayName();
        if (currentUserName != null && !currentUserName.isEmpty()) {
            userName.set(currentUserName);
        }

        final String currentUserEmail = getDataManager().getCurrentUserEmail();
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            userEmail.set(currentUserEmail);
        }

        final String profilePicUrl = getDataManager().getCurrentUserPhotoUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            userProfilePicUrl.set(profilePicUrl);
        }
    }


    public void logout() {
        setIsLoading(true);
        getDataManager().doFirebaseLogoutCall();
        getDataManager().setUserAsLoggedOut();
        setIsLoading(false);
        getNavigator().navigateToLoginScreen();
    }

    public ObservableField<String> getAppVersion() {
        return appVersion;
    }

    public ObservableField<String> getUserName() {
        return userName;
    }

    public ObservableField<String> getUserEmail() {
        return userEmail;
    }

    public ObservableField<String> getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public ObservableArrayList<StatusItemData> getStatusItems() {
        return statusItems;
    }

    public MutableLiveData<List<StatusItemData>> getStatusItemsLiveData() {
        return statusItemsLiveData;
    }
}
