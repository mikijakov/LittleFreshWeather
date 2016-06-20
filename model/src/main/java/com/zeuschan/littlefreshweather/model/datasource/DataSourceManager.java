package com.zeuschan.littlefreshweather.model.datasource;

import android.content.Context;

import com.zeuschan.littlefreshweather.common.util.NetUtil;
import com.zeuschan.littlefreshweather.model.cache.DiskCacheManager;
import com.zeuschan.littlefreshweather.model.entity.CityEntity;
import com.zeuschan.littlefreshweather.model.entity.WeatherConditionEntity;
import com.zeuschan.littlefreshweather.model.entity.WeatherEntity;
import com.zeuschan.littlefreshweather.model.restapi.ServicesManager;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by chenxiong on 2016/6/17.
 */
public class DataSourceManager implements DataSource {
    private static final String DATA_CACHE_DIR = "data";

    private static DataSourceManager ourInstance = null;
    public static DataSourceManager getInstance(Context context) {
        if (null == ourInstance) {
            ourInstance = new DataSourceManager(context);
        }
        return ourInstance;
    }

    private Context mContext;

    private DiskCacheManager mDiskCacheManager;
    private ServicesManager mServiceManager;

    private boolean mIsWeatherEntityDiskCacheExists = false;
    private boolean mIsCityEntitiesDiskCacheExists = false;
    private boolean mIsWeatherConditionEntitiesDiskCacheExists = false;

    private DataSourceManager(Context context) {
        if (null == context)
            throw new IllegalArgumentException("context cannot be null");

        mContext = context;
        mDiskCacheManager = new DiskCacheManager(context, DATA_CACHE_DIR);
        mServiceManager = ServicesManager.getInstance();
    }

    @Override
    public Observable<List<CityEntity>> getCityEntities() {
        return mDiskCacheManager.getCityEntities()
                .concatMap(new Func1<List<CityEntity>, Observable<? extends List<CityEntity>>>() {
                    @Override
                    public Observable<? extends List<CityEntity>> call(List<CityEntity> cityEntities) {
                        if (null == cityEntities) {
                            mIsCityEntitiesDiskCacheExists = false;
                            return mServiceManager.getCityEntities();
                        }
                        else {
                            mIsCityEntitiesDiskCacheExists = true;
                            return Observable.just(cityEntities);
                        }
                    }
                }).doOnNext(new Action1<List<CityEntity>>() {
                    @Override
                    public void call(List<CityEntity> cityEntities) {
                        if (!mIsCityEntitiesDiskCacheExists)
                            mDiskCacheManager.putCityEntities(cityEntities);
                    }
                });
    }

    @Override
    public Observable<List<WeatherConditionEntity>> getWeatherConditionEntities() {
        return mDiskCacheManager.getWeatherConditionEntities()
                .concatMap(new Func1<List<WeatherConditionEntity>, Observable<? extends List<WeatherConditionEntity>>>() {
                    @Override
                    public Observable<? extends List<WeatherConditionEntity>> call(List<WeatherConditionEntity> weatherConditionEntities) {
                        if (null == weatherConditionEntities) {
                            mIsWeatherConditionEntitiesDiskCacheExists = false;
                            return mServiceManager.getWeatherConditionEntities();
                        }
                        else {
                            mIsWeatherConditionEntitiesDiskCacheExists = true;
                            return Observable.just(weatherConditionEntities);
                        }
                    }
                }).doOnNext(new Action1<List<WeatherConditionEntity>>() {
                    @Override
                    public void call(List<WeatherConditionEntity> weatherConditionEntities) {
                        if (!mIsWeatherConditionEntitiesDiskCacheExists)
                            mDiskCacheManager.putWeatherConditionEntities(weatherConditionEntities);
                    }
                });
    }

    @Override
    public Observable<WeatherEntity> getCityWeather(String cityId) {
        final String id = cityId;
        if (NetUtil.isNetworkAvailable(mContext)) {
            return mServiceManager.getCityWeather(cityId)
                    .doOnNext(new Action1<WeatherEntity>() {
                        @Override
                        public void call(WeatherEntity weatherEntity) {
                            mDiskCacheManager.putCityWeather(weatherEntity, id);
                        }
                    });
        }

        return mDiskCacheManager.getCityWeather(cityId)
                .concatMap(new Func1<WeatherEntity, Observable<? extends WeatherEntity>>() {
                    @Override
                    public Observable<? extends WeatherEntity> call(WeatherEntity weatherEntity) {
                        if (null == weatherEntity ) {
                            mIsWeatherEntityDiskCacheExists = false;
                            return mServiceManager.getCityWeather(id);
                        }
                        else {
                            mIsWeatherEntityDiskCacheExists = true;
                            return Observable.just(weatherEntity);
                        }
                    }
                }).doOnNext(new Action1<WeatherEntity>() {
                    @Override
                    public void call(WeatherEntity weatherEntity) {
                        if (!mIsWeatherEntityDiskCacheExists)
                            mDiskCacheManager.putCityWeather(weatherEntity, id);
                    }
                });
    }
}
