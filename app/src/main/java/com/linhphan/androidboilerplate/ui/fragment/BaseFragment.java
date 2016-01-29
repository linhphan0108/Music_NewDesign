package com.linhphan.androidboilerplate.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linhphan.androidboilerplate.ui.activity.BaseActivity;
import com.linhphan.androidboilerplate.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by linhphan on 11/13/15.
 */
public abstract class BaseFragment extends Fragment {

//    public static BaseMusicFragment newInstance(Class<?> c, Bundle bundle){
//        BaseMusicFragment baseFragment = null;
//        try {
//            Constructor<?> constructor = c.getConstructors()[0];
//            baseFragment = (BaseMusicFragment) constructor.newInstance();
//            if (bundle != null){
//                baseFragment.setArguments(bundle);
//            }
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (java.lang.InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return baseFragment;
//    }

    //================= overridden methods =========================================================
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d(getClassTagName(), "On onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(getClassTagName(), "On onCreate");
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(getClassTagName(), "On onCreateView");
        return inflater.inflate(getFragmentLayoutResource(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d(getClassTagName(), "On onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d(getClassTagName(), "On onStart");

        getWidgets(getView());
        registerEventHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(getClassTagName(), "On resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(getClassTagName(), "On onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(getClassTagName(), "On onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(getClassTagName(), "On onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(getClassTagName(), "On onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d(getClassTagName(), "On onDestroy");
    }

    //================== abstract methods ==========================================================
    /**
     * @return the layout resource which will be inflated to layout
     */
    protected abstract int getFragmentLayoutResource();
    /**
     * initialize default value for valuables here
     * this method will be called in onCreate method of Fragment.
     */
    protected abstract void init();

    /**
     * views should retrieve here
     */
    protected abstract void getWidgets(View root);

    /**
     * register event listeners fro views
     */
    protected abstract void registerEventHandler();

    //================== children fragment management ==============================================
    /**
     *
     * @param containerLayoutId identifier of the container whose fragments are to be replaced
     * @param childFragmentClassName the class name of the fragment to be replaced
     * @param data the data will be passed to the fragment
     * @param isAddBackStack if true the transaction will be added to back stack otherwise do nothing
     * @param transaction this parameter intent for a custom animation during transacting. which is instanced from {@link BaseFragment#getChildFragmentTransaction(int, int, int, int)}.
     * if you don't want a custom animation then leave it's null.
     */
    protected <T extends Fragment> void addChildFragment(@IdRes int containerLayoutId, Class<T> childFragmentClassName, boolean isAddBackStack,
                                                         @Nullable Bundle data,  @Nullable FragmentTransaction transaction){
        Fragment fragment = Fragment.instantiate(getContext(), childFragmentClassName.getName(), data);
        if (containerLayoutId == 0 || fragment == null){
            Logger.e(getClassTagName(), "container was null or fragment was null");
            return;
        }

        if (transaction == null){
            transaction = getChildFragmentManager().beginTransaction();
        }
        transaction.add(containerLayoutId, fragment, childFragmentClassName.getName());
        if (isAddBackStack) {
            transaction.addToBackStack(childFragmentClassName.getName());
        }
        transaction.commit();
    }

    /**
     *
     * @param containerLayoutId identifier of the container whose fragments are to be replaced
     * @param childFragmentClassName the class name of the fragment to be replaced
     * @param data the data will be passed to the fragment
     * @param isAddBackStack if true the transaction will be added to back stack otherwise do nothing
     * @param transaction this parameter intent for a custom animation during transacting. which is instanced from {@link BaseActivity#getFragmentTransaction(int, int, int, int)}.
     * if you don't want a custom animation then leave it's null.
     */
    protected <T extends Fragment> void replaceChildFragment(@IdRes int containerLayoutId, Class<T> childFragmentClassName, boolean isAddBackStack,
                                                        @Nullable Bundle data,  @Nullable FragmentTransaction transaction){
        Fragment fragment = Fragment.instantiate(getContext(), childFragmentClassName.getName(), data);
        if (containerLayoutId == 0 || fragment == null){
            Logger.e(getClassTagName(), "container was null or fragment was null");
            return;
        }

        if (transaction == null){
            transaction = getChildFragmentManager().beginTransaction();
        }
        transaction.replace(containerLayoutId, fragment, childFragmentClassName.getName());
        if (isAddBackStack) {
            transaction.addToBackStack(childFragmentClassName.getName());
        }
        transaction.commit();
    }
    protected void clearChildBackStack(){
        FragmentManager manager = getChildFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * get an instant of FragmentTransaction
     * @param enter the animation resource for entered screen
     * @param exit the animation resource for exited screen
     * @param popEnter the animation resource for popped enter screen
     * @param popExit the animation resource fro popped exit screen
     * @return FragmentTransaction object
     */
    protected FragmentTransaction getChildFragmentTransaction(@AnimRes int enter,@AnimRes int exit,@AnimRes int popEnter,@AnimRes int popExit){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter, exit, popEnter, popExit);
        return transaction;
    }

    //================== inner methods =============================================================

    public String getClassTagName(){
        return this.getClass().getName();
    }

    @SuppressWarnings("unchecked")
    protected  <T extends BaseActivity>T getOwnerActivity(){
        return (T) getActivity();
    }

    protected BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
    }

    public static boolean isFragmentVisisble(int id, FragmentManager manager){
        Fragment f = manager.findFragmentById(id);
        return f != null && f.isVisible();
    }

    public static boolean isFragmentVisisble(String tag, FragmentManager manager){
        Fragment f = manager.findFragmentByTag(tag);
        return f != null && f.isVisible();
    }

    public static boolean isFragmentInStack(int id, FragmentManager manager){
        Fragment f = manager.findFragmentById(id);
        return f != null;
    }

    public static boolean isFragmentInStack(String tag, FragmentManager manager){
        Fragment f = manager.findFragmentByTag(tag);
        return f != null;
    }
}
