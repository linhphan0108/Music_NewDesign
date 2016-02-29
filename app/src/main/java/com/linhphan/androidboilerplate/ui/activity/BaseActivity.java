package com.linhphan.androidboilerplate.ui.activity;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.R;

/**
 * Created by linhphan on 11/13/15.
 */
public abstract class BaseActivity extends AppCompatActivity{

    //==================== setters and getters =====================================================


    //==================== overridden methods ======================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getActivityLayoutResource());
        init();
        getWidgets();
        registerEventHandler();
    }

    @Deprecated
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    //==================== implemented methods =====================================================


    //===================== abstract methods =======================================================

    /**
     * @return the layout resource id
     */
    @LayoutRes
    protected abstract int getActivityLayoutResource();

    /**
     * initialize default value for valuables here
     */
    protected abstract void init();

    /**
     * views should retrieve here
     */
    protected abstract void getWidgets();

    /**
     * register event listeners fro views
     */
    protected abstract void registerEventHandler();

    //===================== fragment management ====================================================
    /**
     *
     * @param containerLayoutId identifier of the container whose fragments are to be replaced
     * @param fragmentClass the class name of the fragment to be replaced
     * @param data the data will be passed to the fragment
     * @param isAddBackStack if true the transaction will be added to back stack otherwise do nothing
     * @param transaction this parameter intent for a custom animation during transacting. which is instanced from {@link BaseActivity#getFragmentTransaction(int, int, int, int)}.
     * if you don't want a custom animation then leave it's null.
     */
    protected  <T extends Fragment>void addFragment(@IdRes int containerLayoutId, Class<T> fragmentClass, boolean isAddBackStack, @Nullable Bundle data, @Nullable FragmentTransaction transaction){
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), data);
        if (containerLayoutId == 0 || fragment == null){
            Logger.e(getClassTagName(), "container was null or fragment was null");
            return;
        }
        if (transaction == null){
            transaction = getSupportFragmentManager().beginTransaction();
        }
        transaction.add(containerLayoutId, fragment, fragmentClass.getName());
        if (isAddBackStack) {
            transaction.addToBackStack(fragmentClass.getName());
        }
        transaction.commit();
    }

    /**
     *
     * @param containerLayoutId identifier of the container whose fragments are to be replaced
     * @param fragmentClass the class name of the fragment to be replaced
     * @param data the data will be passed to the fragment
     * @param isAddBackStack if true the transaction will be added to back stack otherwise do nothing
     * @param transaction this parameter intent for a custom animation during transacting. which is instanced from {@link BaseActivity#getFragmentTransaction(int, int, int, int)}.
     * if you don't want a custom animation then leave it's null.
     */
    protected   <T extends Fragment>void replaceFragment(@IdRes int containerLayoutId, Class<T> fragmentClass,boolean isAddBackStack, @Nullable Bundle data, @Nullable FragmentTransaction transaction){
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), data);
        if (containerLayoutId == 0 || fragment == null){
            Logger.e(getClassTagName(), "container was null or fragment was null");
            return;
        }
        if (transaction == null){
            transaction = getSupportFragmentManager().beginTransaction();
        }
        transaction.replace(containerLayoutId, fragment, fragmentClass.getName());
        if (isAddBackStack) {
            transaction.addToBackStack(fragmentClass.getName());
        }
        transaction.commit();
    }

    /**
     * pop the latest fragment in manager's fragment back stack.
     */
    protected void popFragment(){
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();
    }

    /**
     * pop the fragment by id
     * @param id which was returned from commit call
     */
    protected void popFragment(int id){
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    protected void clearBackStack(){
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     *  get an instant of FragmentTransaction
     * @param enter the animation resource for entered screen
     * @param exit the animation resource for exited screen
     * @param popEnter the animation resource for popped enter screen
     * @param popExit the animation resource fro popped exit screen
     * @return FragmentTransaction object
     */
    protected FragmentTransaction getFragmentTransaction(@AnimRes int enter,@AnimRes int exit,@AnimRes int popEnter,@AnimRes int popExit){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
        return fragmentTransaction;
    }

    //============= other methods ==================================================================
    public String getClassTagName(){
        return this.getClass().getName();
    }
}
