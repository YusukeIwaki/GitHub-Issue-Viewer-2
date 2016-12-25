package io.github.yusukeiwaki.githubviewer.renderer;

import android.content.Context;
import android.view.View;

abstract class AbstractRenderer<T> {
  protected final Context context;
  protected final T object;

  protected AbstractRenderer(Context context, T object) {
    this.context = context;
    this.object = object;
  }

  protected boolean shouldHandle(View view) {
    return object != null && view != null;
  }
}
