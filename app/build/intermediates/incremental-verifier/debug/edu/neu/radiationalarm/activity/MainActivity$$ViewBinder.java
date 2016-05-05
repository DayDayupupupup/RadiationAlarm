// Generated code from Butter Knife. Do not modify!
package edu.neu.radiationalarm.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends edu.neu.radiationalarm.activity.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492969, "field 'mToolBar'");
    target.mToolBar = finder.castView(view, 2131492969, "field 'mToolBar'");
    view = finder.findRequiredView(source, 2131492970, "field 'mMainAppTab'");
    target.mMainAppTab = finder.castView(view, 2131492970, "field 'mMainAppTab'");
    view = finder.findRequiredView(source, 2131492971, "field 'mMainViewPager'");
    target.mMainViewPager = finder.castView(view, 2131492971, "field 'mMainViewPager'");
  }

  @Override public void unbind(T target) {
    target.mToolBar = null;
    target.mMainAppTab = null;
    target.mMainViewPager = null;
  }
}
