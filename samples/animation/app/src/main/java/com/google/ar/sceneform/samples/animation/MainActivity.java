package com.google.ar.sceneform.samples.animation;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;

import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.animation.ModelAnimator;

import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;


public class MainActivity extends AppCompatActivity {
  private static final int RENDERABLE = 1;

  private ArFragment arFragment;

  private ModelLoader modelLoader;
  private ModelRenderable globoRenderable;
  private AnchorNode anchorNode;
  private SkeletonNode esqueleto;

  private ModelAnimator animator;

  private FloatingActionButton animationButton;


  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
    modelLoader = new ModelLoader(this);
    modelLoader.loadModel(RENDERABLE, R.raw.globo);
    arFragment.setOnTapArPlaneListener(this::onPlaneTap);
    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onFrameUpdate);
    animationButton = findViewById(R.id.animate);
    animationButton.setEnabled(false);
    animationButton.setOnClickListener(this::onPlayAnimation);
  }

  private void onPlayAnimation(View unusedView) {
    if (animator == null || !animator.isRunning()) {
      AnimationData data = globoRenderable.getAnimationData(0);
      animator = new ModelAnimator(data, globoRenderable);
      animator.start();
      Toast toast = Toast.makeText(this, data.getName(), Toast.LENGTH_SHORT);

      toast.setGravity(Gravity.CENTER, 0, 0);
      toast.show();
    }
  }

  private void onPlaneTap(HitResult hitResult, Plane unusedPlane, MotionEvent unusedMotionEvent) {
    if (globoRenderable == null) {
      return;
    }

    Anchor anchor = hitResult.createAnchor();
    if (anchorNode == null) {
      anchorNode = new AnchorNode(anchor);
      anchorNode.setParent(arFragment.getArSceneView().getScene());
      esqueleto = new SkeletonNode();
      esqueleto.setParent(anchorNode);
      esqueleto.setRenderable(globoRenderable);
    }
  }


  private void onFrameUpdate(FrameTime unusedframeTime) {
    if (anchorNode == null) {
      if (animationButton.isEnabled()) {
        animationButton.setBackgroundTintList(ColorStateList.valueOf(android.graphics.Color.GRAY));
        animationButton.setEnabled(false);
      }
    } else {
      if (!animationButton.isEnabled()) {
        animationButton.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        animationButton.setEnabled(true);
      }
    }
  }

  void setRenderable( ModelRenderable renderable) {
      this.globoRenderable = renderable;
  }

  void onException(Throwable throwable) {
    Toast toast = Toast.makeText(this, "No cargo la imagen" , Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }
}
