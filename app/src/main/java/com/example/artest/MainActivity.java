package com.example.artest;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button removeBtn;
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        removeBtn = findViewById(R.id.remove_btn);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference modelRef = storage.getReference().child("out.glb");

        arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.arFragment);

        findViewById(R.id.downloadBtn)
                .setOnClickListener(v -> {

                    try {
                        File file = File.createTempFile("out", "glb");

                        modelRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                buildModel(file);

                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

        ////// updated transformable node

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setParent(anchorNode);
            transformableNode.setRenderable(renderable);
            transformableNode.getScaleController().setMinScale(0.2999f);
            transformableNode.getScaleController().setMaxScale(0.3000f);
            transformableNode.getRotationController().setEnabled(true);
            transformableNode.getScaleController().setEnabled(true);
            transformableNode.getTranslationController().setEnabled(true);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            transformableNode.select();

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAnchorNode(anchorNode);
                }
            });

        });
        ////// updated transformable node



        ////// previous code
//        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
//
//            AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
//            anchorNode.setRenderable(renderable);
//            arFragment.getArSceneView().getScene().addChild(anchorNode);
//
//        });
        ////// previous code

        ////// reference
//        private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
//        transformableNode.setParent(anchorNode);
//        transformableNode.setRenderable(modelRenderable);
//        arFragment.getArSceneView().getScene().addChild(anchorNode);
//        transformableNode.select();
//    }
        ////// reference

    }

    private ModelRenderable renderable;

    private void buildModel(File file) {


        RenderableSource renderableSource = RenderableSource
                .builder()
                .setSource(this, Uri.parse(file.getPath()), RenderableSource.SourceType.GLB)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        ModelRenderable
                .builder()
                .setSource(this, renderableSource)
                .setRegistryId(file.getPath())
                .build()
                .thenAccept(modelRenderable -> {
                    Toast.makeText(this, "built Successful...Tap to Place ", Toast.LENGTH_SHORT).show();
                    ;
                    renderable = modelRenderable;
                });
    }

    /////// remove
    private void removeAnchorNode(AnchorNode nodeToremove) {
        //Remove an anchor node
        if (nodeToremove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToremove);
            nodeToremove.getAnchor().detach();
            nodeToremove.setParent(null);
            nodeToremove = null;
            Toast.makeText(MainActivity.this, "Product removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Test Delete - markAnchorNode was null", Toast.LENGTH_SHORT).show();
        }
    }
}
