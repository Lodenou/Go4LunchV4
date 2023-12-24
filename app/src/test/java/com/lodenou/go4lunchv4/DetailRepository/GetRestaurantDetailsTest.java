package com.lodenou.go4lunchv4.DetailRepository;

import static org.junit.Assert.assertNotNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.lodenou.go4lunchv4.data.detail.DetailRepository;
import com.lodenou.go4lunchv4.data.user.UserCallData;
import com.lodenou.go4lunchv4.model.detail.Result;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;


public class GetRestaurantDetailsTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private UserCallData userCallData;
    private DetailRepository detailRepository;
    @Test
    public void testGetRestaurantDetails() {
        setUpMocksForGetRestaurantDetails();
        String restaurantId = "restaurantId";
        MutableLiveData<Result> result = detailRepository.getRestaurantDetails(restaurantId);
        assertNotNull(result);
    }

    private void setUpMocksForGetRestaurantDetails() {
        MockitoAnnotations.initMocks(this);

        //Allow Rx call tests
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        MutableLiveData<com.lodenou.go4lunchv4.model.detail.Result> mockDataDetail = new MutableLiveData<>();
        detailRepository = DetailRepository.getInstance(userCallData, "userUid","username", "userPhotoUrl", "UserEmail", mockDataDetail);
    }
}
